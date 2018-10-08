~keySources = [
  \note,
  \vel,
  \bend,
  \mod,
  \lfo1,
  \env1,
];

~keyTargets = [
  \note,
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
  \vibratoDepth,
  \tremoloDepth,
  \envTime,
];

SynthDef(\bend, {
  arg bend = 0, out = 0;
  bend = bend.lag2(0.02);
  Out.kr(out, bend);
}).add;

SynthDef(\compress, {
  arg in = 0, out = 0, preGain = 1, postGain = 0.21, parallel = 0, chorusDepth = 0.2, chorusSpeed = 8, chorusShape = 0.5, reverb = 0.2, bassBoost = 10;
  var audioIn, audio;
  var combDelay;

	// Save audioIn for later in parallel processing.
	audioIn = In.ar(in + [0, 1]);
  audio = audioIn;
  audio = Limiter.ar(audio * 0.25, 0.75, 0.005);
	audio = (audio * preGain).softclip;

  // Bass boost
  audio = BLowShelf.ar(audio, 300, 5, bassBoost);

  // compress
  //audio = Compander.ar(audio, audio, 0.5, 1, 0.2, 0.02, 0.02);
  //audio = Limiter.ar(audio, 0.75, 0.01);

	// Chorus
  chorusShape = [0, pi] + LFNoise2.kr(chorusSpeed * 0.25, mul: chorusShape * pi);
  combDelay = SinOsc.kr(chorusSpeed.dup, chorusShape);
  combDelay = (combDelay * chorusDepth).linexp(-1, 1, 0.005, 0.015);

  audio = LinSelectX.ar(chorusDepth, [audio, CombC.ar(audio.neg, 0.02, combDelay, decaytime: 0.05, mul: 1)]);

  // Apply a nice subtle room effect, and then delay the incoming audio.
  audio = DelayN.ar(audio, 0.03, 0.01) + FreeVerb2.ar(audio[0], audio[1], reverb.sqrt, reverb);

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
		dummy = 0;
  var freq, resonator, resonatorDecay, audio, feedbackAudio, formantAudio, filterFreq, pink, peak, env, gateEnv;
  var lfo1, driveMakeUp;
  var impulseAudio, impulseFreq;
  var full, inverse;
  var notePos = note - 63.5;
  var velScalar = vel / 127;
  var bend, mod;
  var m = Mordule(~keySources, ~keyTargets, 2);

  bend = In.kr(~bendBus);
  mod = In.kr(~modBus);
  m.write(\bend, bend);
  m.write(\mod, mod);
  m.write(\note, note.linlin(44, 108, 0, 1));
  m.write(\vel, vel.linlin(0, 127, 0, 1));

  // Envelopes
  envTime = envTime * pow(2, m.tap(\envTime));
  env = EnvGen.kr(Env.new(
      [-1, 1, 0, -1],
      [(1 - envShape), envShape, 0.5 * envShape],
      4, 2 // curve of 4, loop at 2.
   ), gate, timeScale: envTime);
  gateEnv = env.range(0, 1);
  m.write(\env1, env);

  // Applies lfo1 with subtle random variations.
  lfo1Speed = lfo1Speed * pow(4, m.tap(\lfo1Speed));
  lfo1 = (SinOsc.kr(lfo1Speed, lfo1Walk * LFNoise2.kr((lfo1Walk + 0.2) * lfo1Speed, mul: 5) + [0, pi] + 0.1) * lfo1Shape).softclip;
  lfo1 = lfo1 * Line.kr(0, 1, lfo1Enter);

  // m.insertFromSource(\note, \filter, 1);
  m.insertFromSource(\note, \filter, filterNote);
  m.insertFromSource(\env, \filter, filterEnv);

  pink = In.ar(~pinkBus.index) * 3;
  filter = filter * vel.linlin(0, 127, 1, filterVel);
  filter = filter * pow(2, m.tap(\filter).at(0));
  filter = filter * ((notePos * filterNote) + filterEnv).midiratio;
  filter.poll;

  // If the gate is active, and so is hold, hold forever
  gate = (gate + Latch.kr(gate, Changed.kr(hold))).clip(0, 1);



	// Note/frequency calculation.
  note = note + (bend * bendSteps);
  note = note + (12 * m.tap(\note));
  note = note + (lfo1[0] * vibratoDepth);
  note = note + TRand.kr(detune.neg.dup, detune.dup, gate).lag(0.1);
  freq = note.midicps;

	// Store this for later attenuation.
  driveMakeUp = 1;//filterDrive.sqrt;

  m.insertFromSource(\note, \feedback, feedbackHiCut.neg);
  feedback = feedback + (12 * m.tap(\feedback)).dbamp;
  feedbackAudio = (LocalIn.ar(2) * feedback * 0.018;

  // The resonator is the sum of a white noise source and feedback, which
  // then excites the karplus strong comb.
  // Resonator is theoretically mono to save some processing power.
  resonator = (resonator + (m.tap(\resonator).at(0))).clip(0, 2).dbamp;
  m.insertFromSource(\vel, \resonatorPitchShift, 1);
  resonatorPitchShift = resonatorPitchShift * pow(2, m.tap(\resonatorPitchShift).at(0));
  resonator = pink * resonatorLevel * 0.05 + feedbackAudio * gate;

  // Compand and clip to keep feedback from exploding. @TODO use limiter
  // instead?
  resonator = Compander.ar(resonator, resonator, 0.25, 1, 0.01).softclip;
  resonator = LPF.ar(resonator, min(18000, freq[0] * resonatorPitchShift[0]));

  // The hardness filter produces the illusion of hardness. Low frequencies
  // sound soft, high frequencies sound more like metal on metal.
  // Impulse is theoretically mono to save some processing power.
  m.insertFromSource(\vel, \hardness, hardnessVel);
  hardness = hardness + (24 * m.tap(\hardness).at(0));
  impulseAudio = BPF.ar(pink * Trig.kr(gate, 0.01), min(20000, freq[0] * impulseFreq.round(6).midiratio)) * (vel / 256 + 0.5) * 2;

  // Extremely low hardness should also affect amplitude
  impulseAudio = impulseAudio * (impulseFreq.clip(-24, -12) + 24 / 12);

  // Highpass both leaks out DC and removes dustiness from resonator.
	// Do it now so that the low frequencies don't resonate.
  resonator = HPF.ar(resonator, freq[0]);

  // Click last so it stays sharp
  resonator = resonator + impulseAudio;

  // Karplus-Strong algorithm generates the actual pitch.
  // Harmonics selects between a regular comb (all harmonics) and an inverted
  // one (even only). @TODO Inverse drops pitch by an octave, do I care?
  harmonics = harmonics + m.tap(\harmonics);
  full = CombC.ar(resonator * gate, 0.025, (freq).reciprocal - SampleDur.ir, Select.kr(gate, [decay, sustain]));
  inverse = CombC.ar(resonator * gate, 0.025, (freq).reciprocal - SampleDur.ir, -1 * Select.kr(gate, [decay, sustain]));
  audio = LinSelectX.ar(harmonics, [full, inverse]);

  // Add in the impulse post resonation
  impulse = (impulse + m.tap(\impulse, 2)).clip(0, 2);
  audio = impulse * impulseAudio * 4 + audio;

  // Saturate - clean up audio DC before driving it to prevent rectification.
  audio = LeakDC.ar(audio);
  audio = (audio * filterDrive).softclip;

  // Add trem after saturation so it doesn't get lost in the mix.
  audio = audio * lfo1.range(1, 1 - tremoloDepth);

  // Add Formant
  formant = (formant + (formantNote * notePos) + (formantEnv * env)).midiratio;
  formantAudio = BPF.ar(audio, (880 * formant).clip(30, 20000), 0.2, mul: 9);
  audio = LinSelectX.ar(formantDepth, [audio, formantAudio]);

  //Add low pass
  filterFreq = min(20000, 440 * filter);
  audio = LPF.ar(audio, filterFreq, mul: 1);

  // Make up attenuation for the gain
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
