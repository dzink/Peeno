~keySources = [
  \note,
  \vel,
  \bend,
  \mod,
  \lfo1,
  \lfo2,
  \env1,
];

~keyTargets = [
  \freq,
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
  \lfo1Speed,
  \lfo1Shape,
  \lfo2Speed,
  \lfo2Spread,
  \vibratoDepth,
  \tremoloDepth,
  \envTime,
  \pan,
  \retrigger,
];

SynthDef(\bend, {
  arg bend = 0, out = 0;
  bend = bend.lag2(0.02);
  Out.kr(out, bend);
}).add;

SynthDef(\compress, {
  arg in = 0, out = 0, preGain = 1, postGain = 0.21, parallel = 0, chorusDepth = 0.2, chorusSpeed = 8, chorusShape = 0.5, reverb = 0.2, bassBoost = 10, bias = 70;
  var audioIn, audio;
  var combDelay;

	// Save audioIn for later in parallel processing.
	audioIn = In.ar(in + [0, 1]);
  audio = audioIn;
  audio = Limiter.ar(audio * 0.25, 0.75, 0.005);
	audio = (audio * preGain).softclip;

  // Bass boost
  audio = BLowShelf.ar(audio, 300, 5, bassBoost);

  audio = BPeakEQ.ar(audio, bias.midicps, 3, 24);


	// Chorus
  chorusShape = [0, pi] + LFNoise2.kr(chorusSpeed * 0.25, mul: chorusShape * pi);
  combDelay = SinOsc.kr(chorusSpeed.dup, chorusShape);
  combDelay = (combDelay * chorusDepth).linexp(-1, 1, 0.005, 0.015);

  audio = LinSelectX.ar(chorusDepth, [audio, CombC.ar(audio.neg, 0.02, combDelay, decaytime: 0.05, mul: 1)]);

  // Apply a nice subtle room effect, and then delay the incoming audio.
  // audio = audio + FreeVerb2.ar(audio[0], audio[1], reverb.sqrt, reverb);

  audio = LeakDC.ar(audio);

  // Post gain.
  audio = audio * postGain;


	// Final processing - add in original signal in parallel if wanted.
	audio = LinSelectX.ar(parallel, [audio, audioIn]);
  audio = Compander.ar(audio, audio, 0.75, 1, 0.05, 1, 1);
  audio = Limiter.ar(audio, 1, 0.005);
  ReplaceOut.ar(out, (audio));
}).add;

SynthDef(\key, {
  arg
		note= 44,
		gate = 1,
		vel = 64,
    bendSteps = 2,
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
    panAlgo = 1,
    pan = 0,
		dummy = 0;
  var freq, resonator, resonatorDecay, audio, feedbackAudio, formantAudio, filterFreq, pink, peak, env, gateEnv, retrigger;
  var lfo1, lfo2, lfo2Phase, driveMakeUp;
  var impulseAudio, impulseFreq;
  var full, inverse;
  var notePos = note - 63.5;
  var velScalar = vel / 127;
  var bend, mod;
  var m = Mordule(~keySources, ~keyTargets, 2);
  retrigger = Trig1.kr(m.tapTarget(\retrigger), ControlDur.ir) + t_retrig;
  audio = SinOsc.ar(440) * 0.1;
  bend = In.kr(~bendBus);
  mod = In.kr(~modBus);
  m.writeSource(\bend, bend);
  m.writeSource(\mod, mod);
  m.writeSource(\note, note.linlin(44, 108, 0, 1));
  m.writeSource(\vel, vel.linlin(0, 127, 0, 1));
  gate = gate * (1 - retrigger);

  4.do {
    arg i;
    var source = (\modSource ++ i).asSymbol.kr(0);
    var target = (\modTarget ++ i).asSymbol.kr(0);
    var amount = (\modAmount ++ i).asSymbol.kr(0);
    m.connectDoubleSelect(source, target, mul: amount);
  };

  // Envelopes
  envTime = envTime * m.tapTargetExponential(\envTime, 16);
  env = EnvGen.kr(Env.new(
      [-1, 1, 0, -1],
      [(1 - envShape), envShape, 0.5 * envShape],
      -4, 2 // curve of 4, loop at 2.
   ), gate, timeScale: envTime);
  gateEnv = env.range(0, 1);
  m.writeSource(\env1, env);

  // Applies lfo1 with subtle random variations.
  lfo1Speed = lfo1Speed * m.tapTargetExponential(\lfo1Speed, 4);
  lfo1 = (SinOsc.kr(lfo1Speed, lfo1Walk * LFNoise2.kr((lfo1Walk + 0.2) * lfo1Speed, mul: 5) + [0, pi] + 0.1) * lfo1Shape).softclip;
  lfo1 = lfo1 * Line.kr(0, 1, lfo1Enter);
  m.writeSource(\lfo1, lfo1);

  // Applies lfo2 just a basic sine. The sine can have stereo width (0) or
  // stereo spin (1) based on the value of lfo2StereoSpin.
  lfo2Speed = lfo2Speed * m.tapTargetExponential(\lfo2Speed, 4);
  lfo2Spread = (lfo2Spread * m.tapTarget(\lfo2Spread).at(0)).clip(-1, 1);
  lfo2Speed = lfo2Speed * Select.kr(lfo2StereoSpin, [[1, 1], [1, pow(8, lfo2Spread)]]);
  lfo2Phase = Select.kr(lfo2StereoSpin, [[0, lfo2Spread * pi], [0, pi]]);
  lfo2 = SinOsc.kr(lfo2Speed, lfo2Phase);
  m.writeSource(\lfo2, lfo2);
  // lfo2Phase.poll;
  // lfo2Spread.poll;

  pink = In.ar(~pinkBus.index) * 3;

  // If the gate is active, and so is hold, hold forever
  gate = (gate + Latch.kr(gate, Changed.kr(hold))).clip(0, 1);

	// Note/frequency calculation.
  vibratoDepth = vibratoDepth *  m.tapTargetExponential(\vibratoDepth, 2);
  note = note + m.tapTarget(\freq, 24);
  note = note + (bend * bendSteps);

  // vibrato is mono
  note = note + (lfo1[0] * vibratoDepth);
  note = note + TRand.kr(detune.neg.dup, detune.dup, gate).lag(0.1);
  note = note + [0, 12];
  freq = note.midicps;

	// Store this for later attenuation.
  driveMakeUp = 1;//filterDrive.sqrt;

  m.connect(\note, \feedback, feedbackHiCut.neg);
  feedback = (feedback + m.tapTarget(\feedback).at(0)).clip(0, 2);
  feedbackAudio = (LocalIn.ar(2)) * feedback * 0.018;

  // The resonator is the sum of a white noise source and feedback, which
  // then excites the karplus strong comb.
  // Resonator is theoretically mono to save some processing power.
  resonatorLevel = (resonatorLevel + m.tapTarget(\resonator).at(0)).clip(0, 2);
  resonatorPitchShift = resonatorPitchShift *  m.tapTargetExponential(\resonatorPitchShift, 4).at(0);
  resonator = pink * resonatorLevel * 0.05 + feedbackAudio * gate;

  // Compand and clip to keep feedback from exploding. @TODO use limiter
  // instead?
  resonator = Compander.ar(resonator, resonator, 0.25, 1, 0.01).softclip;
  resonator = LPF.ar(resonator, min(18000, freq[0] * resonatorPitchShift));

  // The hardness filter produces the illusion of hardness. Low frequencies
  // sound soft, high frequencies sound more like metal on metal.
  // Impulse is theoretically mono to save some processing power.
  m.connect(\vel, \hardness, hardnessVel);
  hardness = hardness + (m.tapTarget(\hardness, 48).at(0)).clip(-24, 48);
  impulseAudio = BPF.ar(pink * Trig.kr(gate, 0.01), min(20000, freq[0] * hardness.round(6).midiratio));
  // impulseAudio = CombN.ar(pink * Trig.kr(gate, 0.01), 0.05, (min(20000, Latch.kr(freq[0], 1) * hardness.round(6).midiratio)).reciprocal, 0.1);
  // impulseAudio = LeakDC.ar(impulseAudio);
  // impulseAudio = pink * Trig.kr(gate, 0.01);

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
  harmonics = (harmonics + m.tapTarget(\harmonics, 2)).clip(0, 1);
  full = CombC.ar(resonator * gate, 0.025, (freq).reciprocal - SampleDur.ir, Select.kr(gate, [decay, sustain]));

  inverse = CombC.ar(resonator * gate, 0.025, (freq).reciprocal - SampleDur.ir, -1 * Select.kr(gate, [decay, sustain]));
  audio = LinSelectX.ar(harmonics, [full, inverse]);
  freq[0];

  // Add in the impulse post resonation
  impulse = (impulse + m.tapTarget(\impulse, 2)).clip(0, 2);
  audio = impulse * impulseAudio * 4 + audio;

  // Add Formant
  m.connect(\env1, \formant, formantEnv);
  formant = (formant + m.tapTarget(\formant, 48)).midiratio;
  formant = SelectX.kr(formantNote, [880, freq] * formant).clip(30, 20000);
  formantDepth = (formantDepth + m.tapTarget(\formantDepth)).clip(0, 2);
  formantAudio = BPF.ar(audio, formant, 0.2, mul: 9);
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

  filterFreq = min(20000, filterNote.linexp(0, 1, 110, freq) * filter);
  // audio = LPF.ar(audio, filterFreq.poll, mul: 1);
  audio = MoogFF.ar(audio, filterFreq, mul: 1);

  pan = pan + m.tapTarget(\pan).at(0);
  pan = Select.kr(panAlgo, [[-1, 1] + (pan * 2), [-1, 1] * pan]).clip(-1, 1);
  audio.size.postln;
  audio = Pan2.ar(audio[0], pan[0]) + Pan2.ar(audio[1], pan[1]);

  audio = LeakDC.ar(audio);
  LocalOut.ar(audio * 0.625);
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
