SynthDef(\bend, {
  arg bend = 0, out = ~bendBus.index;
  bend = bend.lag2(0.02);
  bend.poll;
  Out.kr(out, bend);
}).add;

SynthDef(\compress, {
  arg in = 0, out = 0, preGain = 1, postGain = 0.21, parallel = 0, chorusDepth = 0.2, chorusSpeed = 8, chorusShape = 0.5, reverb = 0.2, bassBoost = 10;
  var audioIn, audio;
  var combDelay;

	// Save audioIn for later in parallel processing.
	audioIn = In.ar(in + [0, 1]);
	audio = audioIn * preGain;

	// Bass boost
  audio = BLowShelf.ar(audio, 200, 5, bassBoost * 4);

	// Compress and post gain.
  audio = Compander.ar(audio, audio, 0.5, 1, 0.2, 0.05, 0.05) * postGain;

	// Chorus
  chorusShape = [0, pi] + LFNoise2.kr(chorusSpeed * 0.25, mul: chorusShape * pi);
  combDelay = SinOsc.kr(chorusSpeed.dup, chorusShape);
  combDelay = (combDelay * chorusDepth).linexp(-1, 1, 0.005, 0.015);
  audio = LinSelectX.ar(chorusDepth, [audio, AllpassC.ar(audio.neg, 0.02, combDelay, decaytime: 0.05, mul: 1)]);

  // Apply a nice subtle room effect, and then delay the incoming audio.
  audio = DelayN.ar(audio, 0.03, 0.01) + FreeVerb2.ar(audio[0], audio[1], reverb.sqrt, reverb);

	// Final processing - add in original signal in parallel if wanted.
	audio = LinSelectX.ar(parallel, [audio, audioIn]);
  audio = audio.softclip;
  ReplaceOut.ar(out, (audio));
}).add;

SynthDef(\key, {
  arg
		note= 44,
		gate = 1,
		vel = 64,
		impulse = 0,
		impulseFilter = 0,
		impulseFilterVel = 1,
		resonatorLevel = 1,
		resonatorPitchShift = 0,
		feedback = 0, // The amount of feedback fed back into the audio
		feedbackHiCut = 1100, // The LPF on feedback
		filterDrive = 10, // The drive in the filter section
		filter = 10, // The MIDI note of the LPF freq
		filterVel = 4, //
		filterNote = -0.5,
		filterEnv = 1,
		lfo1Speed = 0,
		lfo1Walk = 0,
		lfo1Shape = 40,
		lfo1Enter = 10,
		vibratoDepth = 0,
		tremoloDepth = 1,
		panDepth = 0,
		detune = 0.00,
		hold = 0,
		sustain = 10.5,
		decay = 0.075,
		harmonics = 0.5,
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
  var bend;

  pink = In.ar(~pinkBus.index) * 3;
  pink = DelayN.ar(pink, 0.2, 0.2.rand, add: pink);
  filter = filter * vel.linlin(0, 127, 1, filterVel);
  filter = filter * ((notePos * filterNote) + filterEnv).midiratio;

  bend = In.kr(~bendBus) * 2;

  // If the gate is active, and so is hold, hold forever
  gate = (gate + Latch.kr(gate, Changed.kr(hold))).clip(0, 1);

  // Applies lfo1 with subtle random variations.
  lfo1 = (SinOsc.kr(lfo1Speed, lfo1Walk * LFNoise2.kr((lfo1Walk + 0.2) * lfo1Speed, mul: 5) + [0, pi] + 0.1) * lfo1Shape).softclip;
  lfo1 = lfo1 * Line.kr(0, 1, lfo1Enter);

	// Note/frequency calculation.
  note = note + bend;
  note = note + (lfo1[0] * vibratoDepth);
  note = note + TRand.kr(detune.neg.dup, detune.dup, gate).lag(0.1);
  freq = note.midicps;

	// Envelopes
  env = EnvGen.kr(Env.new(
      [-1, 1, 0, -1],
		  [0.5 * (1 - envShape), 1.25 * envShape, 0.5 * envShape],
		  4, 2 // curve of 4, loop at 2.
	 ), gate, timeScale: envTime);
  gateEnv = env.range(0, 1);

	// Store this for later attenuation.
  driveMakeUp = filterDrive.sqrt;

  feedbackAudio = (LocalIn.ar(2) * feedback * 0.018 * note.linlin(40, 79, 1, 1 - feedbackHiCut)).mean;

  //resonator = pink * resonatorLevel * 0.025 * SinOsc.kr(9 * LFNoise1.kr([0.25, 0.25]).range(0.8, 1.25)).range(0.5, 1) + feedbackAudio;
  resonator = pink.dup * resonatorLevel * 0.05 + feedbackAudio * gateEnv;
  resonator = Compander.ar(resonator, resonator, 0.25, 1, 0.01).softclip;

  // Click & resonator don't need a stereo filter.
  impulseFreq = (impulseFilter + (impulseFilterVel * 36 * velScalar));
  impulseAudio = BPF.ar(pink * Trig.kr(gate, 0.01), min(20000, freq[0] * impulseFreq.round(6).midiratio)) * (vel / 256 + 0.5) * 2;
  impulseAudio = impulseAudio * (impulseFreq.clip(-24, -12) + 24 / 12);
  resonator = BPF.ar(resonator, min(18000, freq[0] * resonatorPitchShift));

  // Highpass both leaks out DC and removes dustiness from resonator.
	// Do it now so that the low frequencies don't resonate.
  resonator = HPF.ar(resonator, freq[0]);

  // Click last so it stays sharp
  resonator = resonator + impulseAudio;

  // Karplus-Strong algorithm generates the actual pitch
  full = CombC.ar(resonator * gate, 0.025, (freq).reciprocal - SampleDur.ir, Select.kr(gate, [decay, sustain]));
  inverse = CombC.ar(resonator * gate, 0.025, (freq * Select.kr(invOctave, [0.5, 1, 2, 4])).reciprocal - SampleDur.ir, -1 * Select.kr(gate, [decay, sustain]));
  audio = LinSelectX.ar(harmonics, [full, inverse]);

  // Add in the impulse post resonation
  audio = impulse * impulseAudio * 4 + audio;

  // Add trem before saturation to get a realistic sound.
  audio = audio * lfo1.range(1, 1 - tremoloDepth);

  // Saturate - clean up audio DC before driving it to prevent rectification.
  audio = LeakDC.ar(audio);
  audio = (audio * filterDrive).softclip;

  // Add Formant
  formant = (formant + (formantNote * notePos) + (formantEnv * env)).midiratio;
  formantAudio = BPF.ar(audio, min(20000, 880 * formant), 0.2, mul: 9);
  audio = LinSelectX.ar(formantDepth, [audio, formantAudio]);

  //Add low pass
  filterFreq = min(20000, 440 * filter);
  audio = LPF.ar(audio, filterFreq, mul: 1);

  // Make up attenuation for the gain
  audio = audio / driveMakeUp;
  LocalOut.ar(audio * 0.625);
  DetectSilence.ar(audio + (gate), doneAction: 2);
  Out.ar(0, audio);
}).add;


// Pink Noise Generator; this UGen is expensive so only do it once then read from a bus.
SynthDef(\pink, {
  arg chorusDepth = 0.2, chorusSpeed = 8, chorusShape = 0.5;
  var pink = PinkNoise.ar;
  var combDelay;
  combDelay = SinOsc.kr(chorusSpeed.dup, [0, pi] + LFNoise2.kr(chorusSpeed.dup * 0.25, mul: chorusShape));  combDelay = combDelay.softclip.range(0.0, 0.005);
  pink = AllpassC.ar(pink, 0.02, combDelay, 0.1, mul: chorusDepth);
  Out.ar(~pinkBus.index, PinkNoise.ar);
}).add;
