~keyParams = [
	\baseNote, \leftNote, \detune, \sustain, \decay, \impulse, \impulseVel, \hardness, \hardnessVel, \harmonics, \filter, \filterDrive, \filterNote, \filterVel, \filterEnv, \resonatorLevel, \resonatorPitchShift, \feedback, \feedbackHiCut, \formant, \formantDepth, \formantEnv, \formantNote, \envTime, \envShape, \lfo1Speed, \lfo1Shape, \lfo1Walk, \lfo1Enter, \lfo2Speed, \lfo2Spread, \lfo2StereoSpin, \vibratoDepth, \tremoloDepth, \pan, \panAlgo,
];
~compressParams = [
	\chorusDepth, \chorusSpeed, \chorusShape,
	\preGain, \postGain, \reverb, \bassBoost, \bias,
];

if (~keySources.isNil) {
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
};

if (~paramMap.isNil.not) {
	~presetMap = ~paramMap.asPreset();
};
~paramMap = SS2ParamMap[
	\baseNote -> SS2ParamMirror(24, 2)
		.label_("Base")
		.displayStrategy_(SS2ParamDisplaySemitone())
		.value_(0),
	\leftNote -> SS2ParamMirror(24, 2)
		.label_("Left Note")
		.displayStrategy_(SS2ParamDisplaySemitone())
		.value_(0),
	\detune -> SS2ParamSemitone(0, 1, 2)
		.label_("Detune")
		.convertFromRatio_(true)
		.value_(0),
	\osc2Note -> SS2ParamInf(0.05, 20, 2)
		.maxInf_(true)
		.label_("Left Note")
		.displayStrategy_(SS2ParamDisplay("sec"))
		.value_(5),
	\sustain -> SS2ParamInf(0.05, 20, 2)
		.maxInf_(true)
		.label_("Sustain")
		.displayStrategy_(SS2ParamDisplay("sec"))
		.value_(5),
	\decay -> SS2ParamContinuous(0, 5, 1)
		.label_("Decay")
		.displayStrategy_(SS2ParamDisplay("sec"))
		.value_(0),
	\impulse -> SS2ParamDb(-inf, 6, 2)
		.label_("Impulse")
		.convertFromAmps_(true)
		.value_(0),
	\impulseVel -> SS2ParamDb(0, 6, 2)
		.label_("Vel > Impulse")
		.convertFromAmps_(true)
		.value_(0),
	\hardness -> SS2ParamContinuous(-24, 48, 2)
		.label_("Hardness")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0),
	\hardnessVel -> SS2ParamContinuous(0, 1)
		.label_("Vel > Hard")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0.5),

	\harmonics -> SS2ParamContinuous(0, 1)
		.label_("Shape")
		.displayStrategy_(SS2ParamDisplayPercent().center())
		.value_(0.5),
	\filter -> SS2ParamSemitone(-12, 84, 0, 5)
		.label_("Filter Freq")
		.displayStrategy_(SS2ParamDisplaySemitone())
		.value_(4),
	\filterDrive -> SS2ParamDb(0, 24, 2)
		.label_("Filter Drive")
		.convertFromAmps_(true)
		.value_(1),
	\filterNote -> SS2ParamContinuous(0, 1)
		.label_("Note > Filter Freq")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0),
	\filterVel -> SS2ParamContinuous(0, 1)
		.label_("Vel > Filter")
		.displayStrategy_(SS2ParamDisplaySemitone(scale: 48))
		.value_(0.25),
	\filterEnv -> SS2ParamMirror(1, 2)
		.label_("Env > Filter")
		.displayStrategy_(SS2ParamDisplaySemitone(scale: 48).center())
		.value_(0),
	\resonatorLevel -> SS2ParamDb(-inf, 6)
		.label_("Reso Level")
		.convertFromAmps_(true)
		.db_(-inf),
	\resonatorPitchShift -> SS2ParamSemitone(-24, 48)
		.label_("Reso Filter")
		.convertFromRatio_(true)
		.displayStrategy_(SS2ParamDisplaySemitone())
		.value_(8),
	\feedback -> SS2ParamDb(-inf, 6)
		.label_("Feedback Level")
		.db_(-inf),
	\feedbackHiCut -> SS2ParamContinuous(0, 1)
		.label_("FeedHiCut")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0.5),
	\formantDepth -> SS2ParamDb(-inf, 6, 1)
		.label_("Formant Depth")
		.convertFromAmps_(true)
		.db_(-inf),
	\formant -> SS2ParamSemitone(-12, 60)
		.label_("Formant Freq")
		.value_(3),
	\formantNote -> SS2ParamContinuous(0, 1, 1)
		.label_("Note > Formant")
		.displayStrategy_(SS2ParamDisplaySemitone(scale: 48))
		.value_(0),
	\formantEnv -> SS2ParamMirror(48, 3)
		.label_("Env > Formant")
		.displayStrategy_(SS2ParamDisplaySemitone().center())
		.value_(0),
	\envTime -> SS2ParamContinuous(0.01, 10, \exp)
		.label_("Env Time")
		.displayStrategy_(SS2ParamDisplay("sec"))
		.value_(1),
	\envShape -> SS2ParamContinuous(0, 1)
		.label_("Env Shape")
		.displayStrategy_(SS2ParamDisplayNormalized())
		.value_(0.5),

	\vibratoDepth -> SS2ParamSemitone(0, 12, 5)
		.label_("Vibrato")
		.value_(0),
	\tremoloDepth -> SS2ParamContinuous(0, 1, 0)
		.label_("Tremolo")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0),
	\lfo1Speed -> SS2ParamContinuous(0.25, 20, \exp)
		.label_("Lfo1 Speed")
		.displayStrategy_(SS2ParamDisplay("Hz"))
		.value_(5),
	\lfo1Shape -> SS2ParamContinuous(1, 60, 2)
		.label_("Lfo1 Shape")
		.displayStrategy_(SS2ParamDisplayNormalized())
		.value_(0.5),
	\lfo1Walk -> SS2ParamContinuous(0, 1, -1)
		.label_("Lfo1 Walk")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0),
	\lfo1Enter -> SS2ParamContinuous(0, 12, 2)
		.label_("Lfo1 Enter")
		.displayStrategy_(SS2ParamDisplay("sec"))
		.value_(0.5),
	\lfo2Speed -> SS2ParamContinuous(0.05, 200, \exp)
		.label_("Lfo2 Speed")
		.displayStrategy_(SS2ParamDisplay("Hz"))
		.value_(0.5),
	\lfo2Spread -> SS2ParamContinuous(-1, 1)
		.label_("Lfo2 Spread")
		.displayStrategy_(SS2ParamDisplayCenterable().center())
		.value_(0)
		.addObserver(SS2ParamActionObserver({
			arg param;
			[param.normalized, param.value].postln;
		})),
	\lfo2StereoSpin -> SS2ParamList([\phase, \spin])
		.label_("Lfo2 Algo")
		.displayStrategy_(SS2ParamDisplayList())
		.value_(0),

	\pan -> SS2ParamMirror(1)
		.label_("Pan")
		.displayStrategy_(SS2ParamDisplayPercent().center())
		.value_(0),
	\panAlgo -> SS2ParamList([\directional, \rotate])
		.label_("Pan Algo")
		.displayStrategy_(SS2ParamDisplayList())
		.value_(0),

	\chorusDepth -> SS2ParamContinuous(0, 1, 2)
		.label_("Chorus Depth")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0),
	\chorusSpeed -> SS2ParamContinuous(0.1, 15, 2)
		.label_("Chorus Speed")
		.displayStrategy_(SS2ParamDisplay("Hz"))
		.value_(6),
	\chorusShape -> SS2ParamContinuous(0, 1, 2)
		.label_("Chorus Shape")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0),
	\preGain -> SS2ParamDb(0, 48)
		.label_("Pre Gain")
		.convertFromAmps_(true)
		.db_(24),
	// \postGain -> SS2ParamDb(-24, 24)
	// 	.label_("Post Gain")
	// 	.convertFromAmps_(true)
	// 	.db_(0),
	\reverb -> SS2ParamContinuous(0, 1, -1)
		.label_("Reverb")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0),
	\bassBoost -> SS2ParamDb(0, 12)
		.label_("Bass Boost")
		.value_(0),
	\bias -> SS2ParamContinuous(20, 100)
		.label_("Bias")
		.displayStrategy_(SS2ParamDisplaySemitone())
		.value_(70),

];

4.do {
	arg i;
	var sourceId = (\modSource ++ i).asSymbol;
	var targetId = (\modTarget ++ i).asSymbol;
	var amountId = (\modAmount ++ i).asSymbol;
	~paramMap[sourceId] = SS2ParamList(~keySources)
		.label_("Mod Source " ++ (i + 1))
		.displayStrategy_(SS2ParamDisplayList())
		.value_(0);
	~paramMap[targetId] = SS2ParamList(~keyTargets)
		.label_("Mod Target " ++ (i + 1))
		.displayStrategy_(SS2ParamDisplayList())
		.value_(0);
	~paramMap[amountId] = SS2ParamMirror(1, 2)
		.label_("Mod Amount " ++ (i + 1))
		.displayStrategy_(SS2ParamDisplayPercent().center)
		.value_(0);
	~keyParams = ~keyParams.addAll([sourceId, targetId, amountId]);
};

if (~presetMap.isNil.not) {
	~paramMap.import(~presetMap);
};
