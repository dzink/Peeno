~keyParams = [
	\sustain, \decay, \impulse, \impulseVel, \impulseFilter, \impulseFilterVel,
	\harmonics, \filter, \filterDrive, \filterNote, \filterVel, \filterEnv,
	\resonatorLevel, \resonatorPitchShift, \feedback, \feedbackHiCut,
	\formant, \formantDepth, \formantEnv, \formantNote,
	\lfo1Speed, \lfo1Shape, \lfo1Walk, \lfo1Enter, \vibratoDepth,
];
~compressParams = [
	\chorusDepth,
];

if (~paramMap.isNil.not) {
	~presetMap = ~paramMap.asPreset();
};
~paramMap = SS2ParamMap[
	\sustain -> SS2ParamContinuous(0.05, 20, 2)
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
		.label_("Vel>Impulse")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0),
	\impulseFilter -> SS2ParamContinuous(-24, 48, 2)
		.label_("Hardness")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0),
	\impulseFilterVel -> SS2ParamContinuous(0, 1)
		.label_("Vel>Hard")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0.5),
	\harmonics -> SS2ParamContinuous(0, 1)
		.label_("Shape")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0.25),
	\filter -> SS2ParamContinuous(0.25, 64, 5)
		.label_("Filter Freq")
		.displayStrategy_(SS2ParamDisplaySemitone())
		.value_(4),
	\filterDrive -> SS2ParamContinuous(1, 6.dbamp)
		.label_("Filter Drive")
		.displayStrategy_(SS2ParamDisplayDb())
		.value_(1),
	\filterNote -> SS2ParamMirror(1, 2, 0.125)
		.label_("Note>Filter")
		.displayStrategy_(SS2ParamDisplayPercent().center())
		.value_(-0.5),
	\filterVel -> SS2ParamContinuous(1, 4)
		.label_("Vel>Filter")
		.displayStrategy_(SS2ParamDisplaySemitone())
		.value_(1),
	\filterEnv -> SS2ParamMirror(48, 3)
		.label_("Env>Filter")
		.displayStrategy_(SS2ParamDisplayCenterable("st").center())
		.value_(0),
	\resonatorLevel -> SS2ParamDb(-inf, 6)
		.label_("Reso Level")
		.db_(-inf),
	\resonatorPitchShift -> SS2ParamContinuous(0.25, 16)
		.label_("Reso Filter")
		.displayStrategy_(SS2ParamDisplaySemitone())
		.value_(1),
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
	\formantNote -> SS2ParamMirror(1, 1)
		.label_("Note>Formant")
		.displayStrategy_(SS2ParamDisplayCenterable("st", scale: 48).center())
		.value_(0),
	\formantEnv -> SS2ParamMirror(48, 3)
		.label_("Env>Formant")
		.displayStrategy_(SS2ParamDisplayCenterable("st").center())
		.value_(0),
	\vibratoDepth -> SS2ParamContinuous(0, 12)
		.label_("Vibrato Depth")
		.displayStrategy_(SS2ParamDisplay("st"))
		.value_(0),
	\tremoloDepth -> SS2ParamContinuous(0, 1, 0)
		.label_("Vibrato Depth")
		.displayStrategy_(SS2ParamDisplay("st"))
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
];

~paramMap.asArray.postln;
if (~presetMap.isNil.not) {
	~paramMap.import(~presetMap);
};
~paramMap.asArray.postln;
