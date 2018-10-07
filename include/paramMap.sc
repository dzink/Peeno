~keyParams = [
	\sustain, \decay, \impulse, \impulseVel, \impulseFilter, \impulseFilterVel,
	\harmonics, \filter, \filterDrive, \filterNote, \filterVel, \filterEnv,
	\resonatorLevel, \resonatorPitchShift, \feedback, \feedbackHiCut,
	\formant, \formantDepth, \formantEnv, \formantNote, \envTime, \envShape,
	\lfo1Speed, \lfo1Shape, \lfo1Walk, \lfo1Enter, \vibratoDepth, \tremoloDepth
];
~compressParams = [
	\chorusDepth, \chorusSpeed, \chorusShape,
	\preGain, \postGain, \reverb, \bassBoost,
];

if (~paramMap.isNil.not) {
	~presetMap = ~paramMap.asPreset();
};
~paramMap = SS2ParamMap[
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
		.value_(0),
	\impulseVel -> SS2ParamDb(-inf, 6, 2)
		.label_("Vel > Impulse")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0),
	\impulseFilter -> SS2ParamContinuous(-24, 48, 2)
		.label_("Hardness")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0),
	\impulseFilterVel -> SS2ParamContinuous(0, 1)
		.label_("Vel > Hard")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0.5),

	\harmonics -> SS2ParamListValues.fromPairs([\sine, 0, \mix, 0.5, \square, 1])
		.label_("Shape")
		.displayStrategy_(SS2ParamDisplayList())
		.value_(0.5),
	\filter -> SS2ParamContinuous(0.25, 64, 5)
		.label_("Filter Freq")
		.displayStrategy_(SS2ParamDisplaySemitone())
		.value_(4),
	\filterDrive -> SS2ParamContinuous(1, 24.dbamp, 2)
		.label_("Filter Drive")
		.displayStrategy_(SS2ParamDisplayDb())
		.value_(1),
	\filterNote -> SS2ParamMirror(1)
		.label_("Note > Filter Freq")
		.displayStrategy_(SS2ParamDisplayPercent().center())
		.value_(-0.5)
		.addObserver(SS2ParamActionObserver({
			arg p;
			[p, p.value, p.normalized, p.display];//.postln;
		})),
	\filterVel -> SS2ParamContinuous(1, 16)
		.label_("Vel > Filter")
		.displayStrategy_(SS2ParamDisplaySemitone())
		.value_(1),
	\filterEnv -> SS2ParamMirror(48, 3)
		.label_("Env > Filter")
		.displayStrategy_(SS2ParamDisplayCenterable("st").center())
		.value_(0),
	\resonatorLevel -> SS2ParamDb(-inf, 6)
		.label_("Reso Level")
		.db_(-inf),
	\resonatorPitchShift -> SS2ParamContinuous(0.25, 16)
		.label_("Reso Filter")
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
		.db_(-inf),
	\formant -> SS2ParamSemitone(-12, 48)
		.label_("Formant Freq")
		.value_(3),
	\formantNote -> SS2ParamContinuous(0, 1, 1)
		.label_("Note > Formant")
		.displayStrategy_(SS2ParamDisplay("st", scale: 48))
		.value_(0),
	\formantEnv -> SS2ParamMirror(48, 3)
		.label_("Env > Formant")
		.displayStrategy_(SS2ParamDisplayCenterable("st").center())
		.value_(0),
	\envTime -> SS2ParamContinuous(0.125, 10, 4)
		.label_("Env Time")
		.displayStrategy_(SS2ParamDisplay("sec"))
		.value_(1),
	\envShape -> SS2ParamContinuous(0, 1)
		.label_("Env Shape")
		.displayStrategy_(SS2ParamDisplayNormalized())
		.value_(0.5),

	\vibratoDepth -> SS2ParamContinuous(0, 12, 5)
		.label_("Vibrato")
		.displayStrategy_(SS2ParamDisplay("st"))
		.value_(0),
	\tremoloDepth -> SS2ParamContinuous(0, 1, 0)
		.label_("Tremolo")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0),
	\lfo1Speed -> SS2ParamContinuous(0.5, 12, 1)
		.label_("LFO Speed")
		.displayStrategy_(SS2ParamDisplay("Hz"))
		.value_(0.5),
	\lfo1Shape -> SS2ParamContinuous(1, 60, 2)
		.label_("LFO Shape")
		.displayStrategy_(SS2ParamDisplayNormalized())
		.value_(0.5),
	\lfo1Walk -> SS2ParamContinuous(0, 1, -1)
		.label_("LFO Walk")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0.5),
	\lfo1Enter -> SS2ParamContinuous(0, 12, 2)
		.label_("LFO Enter")
		.displayStrategy_(SS2ParamDisplay("sec"))
		.value_(0.5),

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
		.db_(24),
	\postGain -> SS2ParamDb(-12, 12)
		.label_("Post Gain")
		.db_(0),
	\reverb -> SS2ParamContinuous(0, 1, -1)
		.label_("Reverb")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0),
	\bassBoost -> SS2ParamContinuous(0, 12)
		.label_("Bass Boost")
		.displayStrategy_(SS2ParamDisplay("dB"))
		.value_(0),

];

if (~presetMap.isNil.not) {
	~paramMap.import(~presetMap);
};
