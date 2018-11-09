~keyParams = [
	\baseNote, \leftNote, \detune, \bendSteps, \portamento, \sustain, \decay, \impulse, \impulseVel, \hardness, \hardnessVel, \harmonics, \filter, \filterDrive, \filterNote, \filterReso, \filterVel, \filterEnv, \resonatorLevel, \resonatorPitchShift, \feedback, \feedbackHiCut, \formant, \formantDepth, \formantReso, \formantEnv, \formantNote, \envTime, \envShape, \lfo1Speed, \lfo1Algo, \lfo1Slew, \lfo1Walk, \lfo1Enter, \lfo2Speed, \lfo2Spread, \lfo2StereoSpin, \lfo3Speed, \lfo3Algo, \lfo3Slew, \lfo4Speed, \lfo4Slew, \lfo4Width, \lfo4Multi, \lfo4Algo, \eucSpeed, \eucStep, \eucLength, \eucInner, \vibratoDepth, \tremoloDepth, \amp, \pan, \panAlgo,
];
~compressParams = [
	\chorusDepth, \chorusSpeed, \chorusShape,
	\preGain, \postGain, \reverb, \bassBoost, \bias,
	\delayMix, \delayTime, \delayRegen, \delayPingPong,
];

if (~paramMap.isNil.not) {
	~presetMap = ~paramMap.asPreset();
};
~paramMap = SS2ParamMap[
	\voice -> SS2ParamList([\mono, \poly])
		.label_("Mono/Poly")
		.symbol_(\poly)
		.addObserver(SS2ParamActionObserver({
			~group.freeAll;
		})),
	\baseNote -> SS2ParamMirror(24, 2)
		.label_("Base")
		.displayStrategy_(SS2ParamDisplaySemitone().center())
		.value_(0),
	\leftNote -> SS2ParamMirror(24, 4)
		.label_("Left Note")
		.displayStrategy_(SS2ParamDisplaySemitone())
		.value_(0),
	\bendSteps -> SS2ParamSemitone(0, 12, 2, 1)
		.label_("Bend Steps")
		.value_(0),
	\detune -> SS2ParamSemitone(0, 1, 5)
		.label_("Detune")
		.value_(0),
	\osc2Note -> SS2ParamInf(0.05, 20, 2)
		.maxInf_(true)
		.label_("Left Note")
		.displayStrategy_(SS2ParamDisplay("sec"))
		.value_(5),
	\portamento -> SS2ParamContinuous(0.0, 5, 4)
		.label_("Portamento")
		.displayStrategy_(SS2ParamDisplay("sec"))
		.value_(0),
	\sustain -> SS2ParamInf(0.05, 20, 2)
		.maxInf_(true)
		.label_("Sustain")
		.displayStrategy_(SS2ParamDisplay("sec"))
		.value_(5),
	\decay -> SS2ParamContinuous(0, 5, 1)
		.label_("Decay")
		.displayStrategy_(SS2ParamDisplay("sec"))
		.value_(0),
	\impulse -> SS2ParamDb(-inf, 6)
		.label_("Impulse")
		.convertToAmps_(true)
		.value_(-inf),
	\impulseVel -> SS2ParamDb(0, 6)
		.label_("Vel > Impulse")
		.convertToAmps_(true)
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
	\filter -> SS2ParamSemitone(-12, 120, 2)
		.label_("Filter Freq")
		.displayStrategy_(SS2ParamDisplaySemitone())
		.value_(84),
	\filterDrive -> SS2ParamDb(0, 180, 2)
		.label_("Filter Drive")
		.convertToAmps_(true)
		.value_(1),
	\filterReso -> SS2ParamContinuous(1, 4, 2)
		.label_("Filter Reso")
		.value_(2),
	\filterNote -> SS2ParamContinuous(0, 1)
		.label_("Note > Filter Freq")
		.displayStrategy_(SS2ParamDisplayPercent().center())
		.value_(0),
	\filterVel -> SS2ParamContinuous(0, 1)
		.label_("Vel > Filter Freq")
		.displayStrategy_(SS2ParamDisplaySemitone(scale: 48))
		.value_(0.25),
	\filterEnv -> SS2ParamMirror(1, 2)
		.label_("Env > Filter Freq")
		.displayStrategy_(SS2ParamDisplaySemitone(scale: 48).center())
		.value_(0),
	\resonatorLevel -> SS2ParamDb(-inf, 6)
		.label_("Reso Level")
		.convertToAmps_(true)
		.db_(-inf),
	\resonatorPitchShift -> SS2ParamSemitone(-24, 48)
		.label_("Reso Filter")
		.convertToRatio_(true)
		.displayStrategy_(SS2ParamDisplaySemitone())
		.value_(48),
	\feedback -> SS2ParamDb(-inf, 6)
		.label_("Feedback Level")
		.convertToAmps_(true)
		.db_(-inf),
	\feedbackHiCut -> SS2ParamContinuous(0, 1)
		.label_("FeedHiCut")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0.5),
	\formantDepth -> SS2ParamDb(-inf, 6)
		.label_("Formant Depth")
		.convertToAmps_(true)
		.db_(-inf),
	\formant -> SS2ParamSemitone(-12, 60)
		.label_("Formant Freq")
		.value_(60),
	\formantReso -> SS2ParamContinuous(0.8, 0.005, \exp)
		.label_("Formant Reso")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0.2),
	\formantNote -> SS2ParamContinuous(0, 1, 1)
		.label_("Note > Formant Freq")
		.displayStrategy_(SS2ParamDisplaySemitone(scale: 48))
		.value_(0),
	\formantEnv -> SS2ParamMirror(1, 3)
		.label_("Env > Formant Freq")
		.displayStrategy_(SS2ParamDisplaySemitone(scale: 48).center())
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
	\lfo1Algo -> SS2ParamList([\sine, \square, \pulse,])
		.label_("Lfo1 Shape")
		.displayStrategy_(SS2ParamDisplayList())
		.value_(0),
	\lfo1Slew -> SS2ParamContinuous(0, 1, 4)
		.label_("Lfo1 Slew")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0),
	\lfo1Walk -> SS2ParamContinuous(0, 1, 3)
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
	\lfo2Spread -> SS2ParamMirror(1, 0)
		.label_("Lfo2 Spread")
		.displayStrategy_(SS2ParamDisplayPercent(scale: 50).center())
		.value_(0),
	\lfo2StereoSpin -> SS2ParamList([\phase, \spin])
		.label_("Lfo2 SpreadType")
		.value_(0),
	\lfo3Speed -> SS2ParamContinuous(0.25, 20, \exp)
		.label_("Lfo3 Speed")
		.displayStrategy_(SS2ParamDisplay("Hz"))
		.value_(5),
	\lfo3Slew -> SS2ParamContinuous(0, 1, 4)
		.label_("Lfo3 Slew")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0),
	\lfo3Algo -> SS2ParamList([\steady, \stereoSteady, \dust, \stereoDust])
		.label_("Lfo3 Trigger")
		.displayStrategy_(SS2ParamDisplayList())
		.value_(0),
	\lfo4Speed -> SS2ParamContinuous(0.05, 50, \exp)
		.label_("Lfo4 Speed")
		.displayStrategy_(SS2ParamDisplay("Hz"))
		.value_(0),
	\lfo4Slew -> SS2ParamContinuous(0.0, 1, 4)
		.label_("Lfo4 Slew")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0),
	\lfo4Width -> SS2ParamContinuous(0.01, 0.99, 0)
		.label_("Lfo4 Width")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(1),
	\lfo4Multi -> SS2ParamContinuous(1, 8, 0, 1)
		.label_("Lfo4 Multi")
		.displayStrategy_(SS2ParamDisplay("x"))
		.value_(1),
	\lfo4Algo -> SS2ParamList([\saw, \square, \sine, \cosine, \triangle, \random, \stereoRandom,])
		.label_("Lfo4 Shape")
		.displayStrategy_(SS2ParamDisplayList())
		.value_(0),
	\eucSpeed -> SS2ParamContinuous(0.2, 20, \exp)
		.label_("Euc Speed")
		.displayStrategy_(SS2ParamDisplay("Hz"))
		.value_(0),
	\eucLength -> SS2ParamContinuous(1, 16, 0, 1)
		.label_("Euc Length")
		.displayStrategy_(SS2ParamDisplay(" steps"))
		.value_(8),
	\eucStep -> SS2ParamContinuous(1, 8, 0, 1)
		.label_("Euc Steps")
		.displayStrategy_(SS2ParamDisplay(" steps"))
		.value_(1),
	\eucInner -> SS2ParamContinuous(1, 16, 0, 1)
		.label_("Euc Inner")
		.displayStrategy_(SS2ParamDisplay(" steps"))
		.value_(3),

	\amp -> SS2ParamDb(-inf, 0)
		.label_("Volume")
		.convertToAmps_()
		.value_(0),
	\pan -> SS2ParamMirror(1)
		.label_("Pan")
		.displayStrategy_(SS2ParamDisplayPercent().center())
		.value_(0),
	\panAlgo -> SS2ParamList([\directional, \rotate])
		.label_("Pan Algo")
		.displayStrategy_(SS2ParamDisplayList())
		.value_(0),

	\chorusDepth -> SS2ParamContinuous(0, 1, 3)
		.label_("Chorus Depth")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0),
	\chorusSpeed -> SS2ParamContinuous(0.1, 15, 2)
		.label_("Chorus Speed")
		.displayStrategy_(SS2ParamDisplay("Hz"))
		.value_(6),
	\chorusShape -> SS2ParamContinuous(0, 1, 2)
		.label_("Chorus Walk")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0),
	\preGain -> SS2ParamDb(0, 48, 0)
		.label_("Pre Gain")
		.convertToAmps_(true)
		.db_(0),
	\postGain -> SS2ParamDb(-24, 24, 0)
		.label_("Post Gain")
		.convertToAmps_(true)
		.displayStrategy_(SS2ParamDisplayDb().centered_(true))
		.db_(0),
	\reverb -> SS2ParamContinuous(0, 1, -1)
		.label_("Reverb")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0),
	\bassBoost -> SS2ParamDb(0, 12)
		.label_("Bass Boost")
		.value_(1),
	\bias -> SS2ParamContinuous(50, 110, 1)
		.label_("Bias")
		.displayStrategy_(SS2ParamDisplaySemitone())
		.value_(70),

	\delayMix -> SS2ParamDb(-inf, 0)
		.label_("Delay Mix")
		.convertToAmps_(true)
		.db_(-12),
	\delayTime -> SS2ParamContinuous(0.01, 1, \exp)
		.label_("Delay Time")
		.displayStrategy_(SS2ParamDisplay("sec"))
		.value_(0.25),
	\delayRegen -> SS2ParamContinuous(0, 1, -3)
		.label_("Delay Regen")
		.displayStrategy_(SS2ParamDisplayPercent())
		.value_(0.25),
	\delayPingPong -> SS2ParamList([\straight, \pingPong])
		.label_("Ping Pong")
		.value_(0),


];

~modcount.do {
	arg i;
	var sourceId = (\modSource ++ i).asSymbol;
	var targetId = (\modTarget ++ i).asSymbol;
	var amountId = (\modAmount ++ i).asSymbol;
	~paramMap[sourceId] = SS2ParamList(~keySources)
		.label_("Mod Source " ++ i)
		.displayStrategy_(SS2ParamDisplayList())
		.value_(0);
	~paramMap[targetId] = SS2ParamList(~keyTargets)
		.label_("Mod Target " ++ i)
		.displayStrategy_(SS2ParamDisplayList())
		.value_(0);
	~paramMap[amountId] = SS2ParamMirror(1, 3)
		.label_("Mod Amount " ++ i)
		.displayStrategy_(SS2ParamDisplayPercent().center)
		.value_(0);
	~keyParams = ~keyParams.addAll([sourceId, targetId, amountId]);
};

if (~presetMap.isNil.not) {
	~paramMap.import(~presetMap);
};
