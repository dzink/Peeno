~modcount = (1..6);

~keySources = [
	\note,
	\vel,
	\bend,
	\mod,
	\lfo1,
	\lfo2,
	\lfo3,
	\lfo4,
	\env1,
	\trigRand,
];

~keyTargets = [
	\none,
	\freq,
	\leftNote,
	\portamento,
	\sustain,
	\decay,
	\impulse,
	\hardness,
	\resonator,
	\resonatorPitchShift,
	\feedback,
	\harmonics,
	\filter,
	\filterDrive,
	\formant,
	\formantDepth,
	\env1Trigger,
	\envTime,
	\lfo1Speed,
	\lfo1Shape,
	\lfo2Speed,
	\lfo2Spread,
	\lfo3Speed,
	\lfo4Speed,
	\lfo4Sync,
	\vibratoDepth,
	\tremoloDepth,
	\amp,
	\pan,
	\retrigger,
];

~modcount.do {
	arg i;
	~keyTargets = ~keyTargets.add((\modAmount ++ i).asSymbol);
};

SynthDef(\bend, {
  arg bend = 0, out = 0;
  bend = bend.lag2(0.02);
  Out.kr(out, bend);
}).add;

SynthDef(\compress, {
  arg in = 0, out = 0, preGain = 1, postGain = 0.21, parallel = 0, chorusDepth = 0.2, chorusSpeed = 8, chorusShape = 0.5, reverb = 0.2, bassBoost = 10, bias = 70, delayTime = 0.2, delayRegen = 0.8, delayMix = 0.25, delayPingPong = 1;
  var audioIn, audio;
  var combDelay;

	// Save audioIn for later in parallel processing.
	audioIn = In.ar(in + [0, 1]);
  audio = audioIn;
  audio = Limiter.ar(audio * 0.25, 0.75, 0.005);
	audio = (audio * preGain).softclip;

  // Bass boost
  audio = BLowShelf.ar(audio, 300, 5, bassBoost);

  audio = BPeakEQ.ar(audio, bias.midicps, 3, 12);


	// Chorus
  chorusShape = LFNoise2.kr(chorusSpeed.dup * 0.25, mul: chorusShape);
  combDelay = SinOsc.kr(chorusSpeed, chorusShape + [0, pi]);
  combDelay = (combDelay * chorusDepth).range(0.0038222564329714, 0.000675685860797);//.midicps.reciprocal;
  // combDelay = (combDelay * chorusDepth).range(60, 90).midicps.reciprocal;

  audio = LinSelectX.ar(chorusDepth, [audio, CombC.ar(audio.neg, 0.02, combDelay, decaytime: 0.5 * chorusDepth.cubed, mul: 1)]);

	combDelay = DelayN.kr(combDelay, 1, chorusSpeed.reciprocal * 0.5);
  audio = LinSelectX.ar(chorusDepth, [audio, CombC.ar(audio.neg, 0.02, combDelay, decaytime: 0.5 * chorusDepth.cubed, mul: 1)]);

  // Apply a nice subtle room effect, and then delay the incoming audio.
  // audio = audio + FreeVerb2.ar(audio[0], audio[1], reverb.sqrt, reverb);

	// Here's a delay.
	audio = FeedbackSandwich.ar({
		arg delayAudio;
		delayAudio = audio + (delayAudio * delayRegen);
		delayAudio = Select.ar(delayPingPong, [delayAudio, delayAudio.rotate]);
		delayAudio = DelayN.ar(delayAudio, 1, delayTime.lag(0.1));
		delayAudio = LPF.ar(delayAudio, 80.midicps);
	}, 2) * delayMix + audio;

  audio = LeakDC.ar(audio);

  // Post gain.
  audio = audio * postGain;


	// Final processing - add in original signal in parallel if wanted.
	audio = LinSelectX.ar(parallel, [audio, audioIn]);
  audio = Compander.ar(audio, audio, 0.75, 1, 0.05, 1, 1);
  audio = Limiter.ar(audio * 4, 1, 0.005);
  ReplaceOut.ar(out, (audio));
}).add;

SynthDef(\key, {
  arg
		note= 44,
    baseNote = 0,
    leftNote = 0,
		gate = 1,
		vel = 64,
    bendSteps = 2,
		portamento = 0,
		impulse = 0,
		hardness = 0.5,
		hardnessVel = 0,
		resonatorLevel = 0,
		resonatorPitchShift = 1,
		feedback = 0, // The amount of feedback fed back into the audio
		feedbackHiCut = 1, // The LPF on feedback
		filterDrive = 2, // The drive in the filter section
		filter = 40, // The MIDI note of the LPF freq
		filterVel = 4, //
		filterNote = -0.5,
		filterEnv = 0,
		lfo1Speed = 0,
		lfo1Walk = 0,
		lfo1Shape = 1,
		lfo1Enter = 2,
    lfo2Speed = 2,
    lfo2Spread = 0.5,
    lfo2StereoSpin = 0,
		lfo3Speed = 1,
		lfo3Algo = 0,
		lfo3Slew = 0,
		lfo4Speed = 1,
		lfo4Slew = 0,
		lfo4Algo = 0,
		t_lfo4Sync = 0,
		vibratoDepth = 0,
		tremoloDepth = 0,
		panDepth = 0,
		detune = 0.00,
		hold = 0,
		sustain = 10.5,
		decay = 0.075,
		harmonics = 0,
		invOctave = 0,
		formant = 2,
		formantDepth = 0,
		formantNote = 0.5,
		formantEnv = 0,
		envTime = 0.5,
		envShape = 0,
    t_retrig = 0,
		amp = 1,
    panAlgo = 1,
    pan = 0,
		dummy = 0;
  var freq, noteOffset, resonator, resonatorDecay, audio, feedbackAudio, formantAudio, filterFreq, pink, peak, env, retrigger, env1Trigger;
  var lfo1, lfo2, lfo2Phase, lfo3, lfo3Dust, lfo3Phasor, lfo3Trigger, lfo4, trigRand;
  var impulseAudio, impulseFreq;
  var full, inverse;
  var bend, mod;
  var m = Mordule(~keySources, ~keyTargets, 2);

	// One pink noise algo is shared among all voices.
	pink = In.ar(~pinkBus.index) * 1;

	bend = In.kr(~bendBus);
	m.writeSource(\bend, bend);
	mod = In.kr(~modBus);
	m.writeSource(\mod, mod);
	m.writeSource(\note, (note + baseNote + [0, leftNote]).linlin(44, 108, 0, 1));
	m.writeSource(\vel, vel.linlin(0, 127, 0, 1));

	retrigger = Trig1.kr(m.tapTarget(\retrigger), ControlDur.ir) + t_retrig;
  gate = gate * (1 - retrigger);
  trigRand = TRand.kr(-1, 1, gate);
  m.writeSource(\trigRand, trigRand);

  ~modcount.do {
    arg i;
    var source = (\modSource ++ i).asSymbol.kr(0);
    var target = (\modTarget ++ i).asSymbol.kr(0);
    var amount = (\modAmount ++ i).asSymbol.kr(0);
		amount = (amount + m.tapTarget((\modAmount ++ i).asSymbol)).clip(-1, 1);
    m.connectDoubleSelect(source, target, mul: amount);
  };

  // Envelopes
	env1Trigger = Slope.kr(m.tapTarget(\env1Trigger, 16));
  env1Trigger = gate - Trig1.kr(SetResetFF.kr(env1Trigger, 1 - env1Trigger), ControlDur.ir);
  envTime = envTime * m.tapTargetExponential(\envTime, 16);
  env = EnvGen.kr(Env.new(
      [-1, 1, 0, -1],
      [(1 - envShape), envShape, 0.5 * envShape],
      0, 2 // curve of 4, loop at 2.
   ), env1Trigger, timeScale: envTime);
  m.writeSource(\env1, env);
	// env.poll;

  // Applies lfo1 with subtle random variations.
  lfo1Speed = lfo1Speed * m.tapTargetExponential(\lfo1Speed, 4);
  lfo1 = (SinOsc.kr(lfo1Speed, lfo1Walk * LFNoise2.kr((lfo1Walk + 0.2) * lfo1Speed, mul: 5) + [0, pi] + 0.1) * lfo1Shape).softclip;
  lfo1 = lfo1 * Line.kr(0, 1, lfo1Enter);
  m.writeSource(\lfo1, lfo1);

  // Applies lfo2 just a basic sine. The sine can have stereo width (0) or
  // stereo spin (1) based on the value of lfo2StereoSpin.
  lfo2Speed = lfo2Speed * m.tapTargetExponential(\lfo2Speed, 4);
  lfo2Spread = (lfo2Spread + m.tapTarget(\lfo2Spread).at(0)).clip(-1, 1);
  lfo2Speed = lfo2Speed * Select.kr(lfo2StereoSpin, [[1, 1], [1, pow(8, lfo2Spread)]]);
  lfo2Phase = Select.kr(lfo2StereoSpin, [[0, lfo2Spread * pi], [0, pi]]);
  lfo2 = SinOsc.kr(lfo2Speed, lfo2Phase);
  m.writeSource(\lfo2, lfo2);

  // Lfo3 is a randomizer
	lfo3Speed = lfo3Speed * m.tapTargetExponential(\lfo3Speed, 4);
	lfo3Dust = Dust.kr(lfo3Speed);
	lfo3Phasor = Phasor.kr(1, ControlDur.ir * lfo3Speed, 0, 1);

	// Get stereo algos, will be reduced to mono shortly.
	lfo3Trigger = Slope.kr(Select.kr(lfo3Algo, [lfo3Phasor, lfo3Phasor, lfo3Dust, lfo3Dust]));
	lfo3 = TRand.kr(-1, 1, lfo3Trigger);

	// reduce to mono as needed.
	lfo3 = Select.kr(lfo3Algo, [lfo3.at(0).dup, lfo3, lfo3.at(0).dup, lfo3]);
	lfo3 = lfo3.lag2(lfo3Slew);
  m.writeSource(\lfo3, lfo3);

	lfo4Speed = lfo4Speed * m.tapTargetExponential(\lfo4Speed, 4);
	t_lfo4Sync = Slope.kr(t_lfo4Sync + m.tapTarget(\lfo4Sync));

	lfo4 = Phasor.kr(t_lfo4Sync, 2 * ControlDur.ir * lfo4Speed, -1, 1, -1);
	lfo4 = Select.kr(lfo4Algo, [lfo4, lfo4.sign, (lfo4 - 0.3333333).sign, (lfo4 - 0.5).sign, (lfo4 - 0.75).sign, SinOsc.kr(0, lfo4 * pi)]);
	lfo4 = lfo4.lag2(lfo4Slew);
	m.writeSource(\lfo4, lfo4);

  // If the gate is active, and so is hold, hold forever
  gate = (gate + Latch.kr(gate, Changed.kr(hold))).clip(0, 1);

	portamento = (portamento + m.tapTarget(\portamento, 1)).clip(0, inf);
	noteOffset = (bend * bendSteps);
	noteOffset = noteOffset + (detune * TRand.kr(0.5, 1, gate) * [-1, 1]);
	noteOffset = noteOffset + baseNote + [0, leftNote];

	// Portamento should be applied after all portamento mods are accounted for
	// but before any freq mods are applied. A sample with 0 would likely disable
	// the slide entirely, applying it after freq mods would make them too
	// "swoopy."
	note = note.lag2(portamento) + noteOffset;
	note = note + m.tapTarget(\freq, 24) + [0, m.tapTarget(\leftNote, 24).at(0)];

	// vibrato is mono
	vibratoDepth = vibratoDepth *  m.tapTargetExponential(\vibratoDepth, 2);
	note = note + (lfo1[0] * vibratoDepth);
	freq = note.midicps;

	// feedbackHiCut prevents high notes from screeching.
	feedback = feedback * note.linlin(44, 99, 1, 1 - feedbackHiCut);
  feedback = (feedback + m.tapTarget(\feedback).at(0)).clip(0, 2);
  feedbackAudio = (LocalIn.ar(2)).softclip * feedback * 0.18;

  // The resonator is the sum of a white noise source and feedback, which
  // then excites the karplus strong comb.
  // Resonator is theoretically mono to save some processing power.
  resonatorLevel = (resonatorLevel + m.tapTarget(\resonator).at(0)).clip(0, 2);
  resonatorPitchShift = resonatorPitchShift *  m.tapTargetExponential(\resonatorPitchShift, 4).at(0);
  resonator = pink * resonatorLevel * 0.05 + feedbackAudio * gate;

  resonator = LPF.ar(resonator, min(18000, freq[0] * resonatorPitchShift));

  // The hardness filter produces the illusion of hardness. Low frequencies
  // sound soft, high frequencies sound more like metal on metal.
  // Impulse is theoretically mono to save some processing power.
	Poll.kr(t_retrig, 1, label: \gate);
  m.connect(\vel, \hardness, hardnessVel);
  hardness = hardness + (m.tapTarget(\hardness, 48).at(0)).clip(-24, 48);
  impulseAudio = BPF.ar(pink * Trig.kr(gate, 0.01), min(20000, freq[0] * hardness.round(6).midiratio));

  // Extremely low hardness should also affect amplitude
  impulseAudio = impulseAudio * (hardness.clip(-24, -12) + 24 / 12);

  // Highpass both leaks out DC and removes dustiness from resonator.
	// Do it now so that the low frequencies don't resonate.
  resonator = HPF.ar(resonator, freq[0]);

  // Click last so it stays sharp
  resonator = resonator + impulseAudio;

  // Karplus-Strong algorithm generates the actual pitch.
  // Harmonics selects between a regular comb (all harmonics) and an inverted
  // one (even only). @TODO Inverse drops pitch by an octave, do I care?
	sustain = sustain * m.tapTargetExponential(\sustain, 4);
	decay = decay * m.tapTargetExponential(\decay, 4);
  harmonics = (harmonics + m.tapTarget(\harmonics, 2)).clip(0, 1);
  full = CombC.ar(resonator * gate, 0.025, (freq).reciprocal - SampleDur.ir, Select.kr(gate, [decay, sustain]));
  inverse = CombC.ar(resonator * gate, 0.025, (freq).reciprocal - SampleDur.ir, -1 * Select.kr(gate, [decay, sustain]));
  audio = LinSelectX.ar(harmonics, [full, inverse]);

  // Add in the impulse post resonation for clicky clicky.
  impulse = (impulse + m.tapTarget(\impulse, 2)).clip(0, 2);
  audio = impulse * impulseAudio * 4 + audio;

	// Add Formant
  m.connect(\env1, \formant, formantEnv);
  formant = (formant + m.tapTarget(\formant, 48)).midiratio;
  formant = (SelectX.kr(formantNote, [880, freq]) * formant).clip(30, 20000);
  formantDepth = (formantDepth + m.tapTarget(\formantDepth)).clip(0, 2);
  formantAudio = BPF.ar(audio, formant.lag(0.03), 0.2, mul: 9);
  audio = LinSelectX.ar(formantDepth, [audio, formantAudio]);

  // Saturate - clean up audio DC before driving it to prevent rectification.
  audio = LeakDC.ar(audio);
  filterDrive = filterDrive * m.tapTargetExponential(\filterDrive, 8);
  audio = (audio * max(filterDrive, 1)).softclip;

  // Add trem after saturation so it doesn't get lost in the mix.
  audio = audio * lfo1.range(1, 1 - tremoloDepth);

  //Add low pass filter (finally!)
  m.connect(\env1, \filter, filterEnv);
  m.connect(\vel, \filter, filterVel);
  filter = filter + m.tapTarget(\filter, 48);
  filter = filter.midiratio;

	// @TODO LPF or MoogFF? MoogFF sounds better, run tests to see how much
	// proccessing is saved.
	// @TODO try two LPF in serial.
  filterFreq = min(20000, filterNote.linexp(0, 1, 110, freq) * filter);
  // audio = LPF.ar(audio, filterFreq.poll, mul: 1);
  audio = MoogFF.ar(audio, filterFreq, mul: 1);

	// Feedback out before applying amplitude modulations.
	audio = LeakDC.ar(audio);
	LocalOut.ar(audio * 0.625);

	// Amplitude mod
	amp = (amp + (m.tapTarget(\amp))).clip(0, 2);
	audio = audio * amp;

	// Panning
  pan = pan + m.tapTarget(\pan).at(0);
  pan = Select.kr(panAlgo, [[-1, 1] + (pan * 2), [-1, 1] * pan]).clip(-1, 1);
  audio = Pan2.ar(audio[0], pan[0]) + Pan2.ar(audio[1], pan[1]);

  DetectSilence.ar(audio + (gate), doneAction: 2);
  Out.ar(0, audio);
}).add;


// Pink Noise Generator; this UGen is expensive so only do it once then read from a bus.
~pink = SynthDef(\pink, {
  arg chorusDepth = 0.2, chorusSpeed = 8, chorusShape = 0.5;
  var pink = PinkNoise.ar;
  var combDelay;
  combDelay = SinOsc.kr(chorusSpeed.dup, [0, pi] + LFNoise2.kr(chorusSpeed.dup * 0.25, mul: chorusShape));
  combDelay = combDelay.softclip.range(0.0, 0.005);
  pink = AllpassC.ar(pink, 0.02, combDelay, 0.1, mul: chorusDepth);
  Out.ar(~pinkBus.index, pink);
}).add;
