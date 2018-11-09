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
~paramMap = NopaDictionary[
	\voice -> NopaListParam([\mono, \poly])
		.label_("Mono/Poly")
		.symbol_(\poly)
		.addObserver(NopaActionTarget({
			~group.freeAll;
		})),
	\baseNote -> NopaMirrorParam(24, 2)
		.label_("Base")
		.displayStrategy_(NopaSemitoneString().center())
		.value_(0),
	\leftNote -> NopaMirrorParam(24, 4)
		.label_("Left Note")
		.displayStrategy_(NopaSemitoneString())
		.value_(0),
	\bendSteps -> NopaSemitoneParam(0, 12, 2, 1)
		.label_("Bend Steps")
		.value_(0),
	\detune -> NopaSemitoneParam(0, 1, 5)
		.label_("Detune")
		.value_(0),
	\osc2Note -> NopaInfParam(0.05, 20, 2)
		.maxInf_(true)
		.label_("Left Note")
		.displayStrategy_(NopaString("sec"))
		.value_(5),
	\portamento -> NopaContinuousParam(0.0, 5, 4)
		.label_("Portamento")
		.displayStrategy_(NopaString("sec"))
		.value_(0),
	\sustain -> NopaInfParam(0.05, 20, 2)
		.maxInf_(true)
		.label_("Sustain")
		.displayStrategy_(NopaString("sec"))
		.value_(5),
	\decay -> NopaContinuousParam(0, 5, 1)
		.label_("Decay")
		.displayStrategy_(NopaString("sec"))
		.value_(0),
	\impulse -> NopaDbParam(-inf, 6)
		.label_("Impulse")
		.convertToAmps_(true)
		.value_(-inf),
	\impulseVel -> NopaDbParam(0, 6)
		.label_("Vel > Impulse")
		.convertToAmps_(true)
		.value_(0),
	\hardness -> NopaContinuousParam(-24, 48, 2)
		.label_("Hardness")
		.displayStrategy_(NopaPercentString())
		.value_(0),
	\hardnessVel -> NopaContinuousParam(0, 1)
		.label_("Vel > Hard")
		.displayStrategy_(NopaPercentString())
		.value_(0.5),

	\harmonics -> NopaContinuousParam(0, 1)
		.label_("Shape")
		.displayStrategy_(NopaPercentString().center())
		.value_(0.5),
	\filter -> NopaSemitoneParam(-12, 120, 2)
		.label_("Filter Freq")
		.displayStrategy_(NopaSemitoneString())
		.value_(84),
	\filterDrive -> NopaDbParam(0, 180, 2)
		.label_("Filter Drive")
		.convertToAmps_(true)
		.value_(1),
	\filterReso -> NopaContinuousParam(1, 4, 2)
		.label_("Filter Reso")
		.value_(2),
	\filterNote -> NopaContinuousParam(0, 1)
		.label_("Note > Filter Freq")
		.displayStrategy_(NopaPercentString().center())
		.value_(0),
	\filterVel -> NopaContinuousParam(0, 1)
		.label_("Vel > Filter Freq")
		.displayStrategy_(NopaSemitoneString(scale: 48))
		.value_(0.25),
	\filterEnv -> NopaMirrorParam(1, 2)
		.label_("Env > Filter Freq")
		.displayStrategy_(NopaSemitoneString(scale: 48).center())
		.value_(0),
	\resonatorLevel -> NopaDbParam(-inf, 6)
		.label_("Reso Level")
		.convertToAmps_(true)
		.db_(-inf),
	\resonatorPitchShift -> NopaSemitoneParam(-24, 48)
		.label_("Reso Filter")
		.convertToRatio_(true)
		.displayStrategy_(NopaSemitoneString())
		.value_(48),
	\feedback -> NopaDbParam(-inf, 6)
		.label_("Feedback Level")
		.convertToAmps_(true)
		.db_(-inf),
	\feedbackHiCut -> NopaContinuousParam(0, 1)
		.label_("FeedHiCut")
		.displayStrategy_(NopaPercentString())
		.value_(0.5),
	\formantDepth -> NopaDbParam(-inf, 6)
		.label_("Formant Depth")
		.convertToAmps_(true)
		.db_(-inf),
	\formant -> NopaSemitoneParam(-12, 60)
		.label_("Formant Freq")
		.value_(60),
	\formantReso -> NopaContinuousParam(0.8, 0.005, \exp)
		.label_("Formant Reso")
		.displayStrategy_(NopaPercentString())
		.value_(0.2),
	\formantNote -> NopaContinuousParam(0, 1, 1)
		.label_("Note > Formant Freq")
		.displayStrategy_(NopaSemitoneString(scale: 48))
		.value_(0),
	\formantEnv -> NopaMirrorParam(1, 3)
		.label_("Env > Formant Freq")
		.displayStrategy_(NopaSemitoneString(scale: 48).center())
		.value_(0),
	\envTime -> NopaContinuousParam(0.01, 10, \exp)
		.label_("Env Time")
		.displayStrategy_(NopaString("sec"))
		.value_(1),
	\envShape -> NopaContinuousParam(0, 1)
		.label_("Env Shape")
		.displayStrategy_(NopaNormalizedString())
		.value_(0.5),

	\vibratoDepth -> NopaSemitoneParam(0, 12, 5)
		.label_("Vibrato")
		.value_(0),
	\tremoloDepth -> NopaContinuousParam(0, 1, 0)
		.label_("Tremolo")
		.displayStrategy_(NopaPercentString())
		.value_(0),
	\lfo1Speed -> NopaContinuousParam(0.25, 20, \exp)
		.label_("Lfo1 Speed")
		.displayStrategy_(NopaString("Hz"))
		.value_(5),
	\lfo1Algo -> NopaListParam([\sine, \square, \pulse,])
		.label_("Lfo1 Shape")
		.displayStrategy_(NopaListString())
		.value_(0),
	\lfo1Slew -> NopaContinuousParam(0, 1, 4)
		.label_("Lfo1 Slew")
		.displayStrategy_(NopaPercentString())
		.value_(0),
	\lfo1Walk -> NopaContinuousParam(0, 1, 3)
		.label_("Lfo1 Walk")
		.displayStrategy_(NopaPercentString())
		.value_(0),
	\lfo1Enter -> NopaContinuousParam(0, 12, 2)
		.label_("Lfo1 Enter")
		.displayStrategy_(NopaString("sec"))
		.value_(0.5),
	\lfo2Speed -> NopaContinuousParam(0.05, 200, \exp)
		.label_("Lfo2 Speed")
		.displayStrategy_(NopaString("Hz"))
		.value_(0.5),
	\lfo2Spread -> NopaMirrorParam(1, 0)
		.label_("Lfo2 Spread")
		.displayStrategy_(NopaPercentString(scale: 50).center())
		.value_(0),
	\lfo2StereoSpin -> NopaListParam([\phase, \spin])
		.label_("Lfo2 SpreadType")
		.value_(0),
	\lfo3Speed -> NopaContinuousParam(0.25, 20, \exp)
		.label_("Lfo3 Speed")
		.displayStrategy_(NopaString("Hz"))
		.value_(5),
	\lfo3Slew -> NopaContinuousParam(0, 1, 4)
		.label_("Lfo3 Slew")
		.displayStrategy_(NopaPercentString())
		.value_(0),
	\lfo3Algo -> NopaListParam([\steady, \stereoSteady, \dust, \stereoDust])
		.label_("Lfo3 Trigger")
		.displayStrategy_(NopaListString())
		.value_(0),
	\lfo4Speed -> NopaContinuousParam(0.05, 50, \exp)
		.label_("Lfo4 Speed")
		.displayStrategy_(NopaString("Hz"))
		.value_(0),
	\lfo4Slew -> NopaContinuousParam(0.0, 1, 4)
		.label_("Lfo4 Slew")
		.displayStrategy_(NopaPercentString())
		.value_(0),
	\lfo4Width -> NopaContinuousParam(0.01, 0.99, 0)
		.label_("Lfo4 Width")
		.displayStrategy_(NopaPercentString())
		.value_(1),
	\lfo4Multi -> NopaContinuousParam(1, 8, 0, 1)
		.label_("Lfo4 Multi")
		.displayStrategy_(NopaString("x"))
		.value_(1),
	\lfo4Algo -> NopaListParam([\saw, \square, \sine, \cosine, \triangle, \random, \stereoRandom,])
		.label_("Lfo4 Shape")
		.displayStrategy_(NopaListString())
		.value_(0),
	\eucSpeed -> NopaContinuousParam(0.2, 20, \exp)
		.label_("Euc Speed")
		.displayStrategy_(NopaString("Hz"))
		.value_(0),
	\eucLength -> NopaContinuousParam(1, 16, 0, 1)
		.label_("Euc Length")
		.displayStrategy_(NopaString(" steps"))
		.value_(8),
	\eucStep -> NopaContinuousParam(1, 8, 0, 1)
		.label_("Euc Steps")
		.displayStrategy_(NopaString(" steps"))
		.value_(1),
	\eucInner -> NopaContinuousParam(1, 16, 0, 1)
		.label_("Euc Inner")
		.displayStrategy_(NopaString(" steps"))
		.value_(3),

	\amp -> NopaDbParam(-inf, 0)
		.label_("Volume")
		.convertToAmps_()
		.value_(0),
	\pan -> NopaMirrorParam(1)
		.label_("Pan")
		.displayStrategy_(NopaPercentString().center())
		.value_(0),
	\panAlgo -> NopaListParam([\directional, \rotate])
		.label_("Pan Algo")
		.displayStrategy_(NopaListString())
		.value_(0),

	\chorusDepth -> NopaContinuousParam(0, 1, 3)
		.label_("Chorus Depth")
		.displayStrategy_(NopaPercentString())
		.value_(0),
	\chorusSpeed -> NopaContinuousParam(0.1, 15, 2)
		.label_("Chorus Speed")
		.displayStrategy_(NopaString("Hz"))
		.value_(6),
	\chorusShape -> NopaContinuousParam(0, 1, 2)
		.label_("Chorus Walk")
		.displayStrategy_(NopaPercentString())
		.value_(0),
	\preGain -> NopaDbParam(0, 48, 0)
		.label_("Pre Gain")
		.convertToAmps_(true)
		.db_(0),
	\postGain -> NopaDbParam(-24, 24, 0)
		.label_("Post Gain")
		.convertToAmps_(true)
		.displayStrategy_(NopaDbString().centered_(true))
		.db_(0),
	\reverb -> NopaContinuousParam(0, 1, -1)
		.label_("Reverb")
		.displayStrategy_(NopaPercentString())
		.value_(0),
	\bassBoost -> NopaDbParam(0, 12)
		.label_("Bass Boost")
		.value_(1),
	\bias -> NopaContinuousParam(50, 110, 1)
		.label_("Bias")
		.displayStrategy_(NopaSemitoneString())
		.value_(70),

	\delayMix -> NopaDbParam(-inf, 0)
		.label_("Delay Mix")
		.convertToAmps_(true)
		.db_(-12),
	\delayTime -> NopaContinuousParam(0.01, 1, \exp)
		.label_("Delay Time")
		.displayStrategy_(NopaString("sec"))
		.value_(0.25),
	\delayRegen -> NopaContinuousParam(0, 1, -3)
		.label_("Delay Regen")
		.displayStrategy_(NopaPercentString())
		.value_(0.25),
	\delayPingPong -> NopaListParam([\straight, \pingPong])
		.label_("Ping Pong")
		.value_(0),


];

~modcount.do {
	arg i;
	var sourceId = (\modSource ++ i).asSymbol;
	var targetId = (\modTarget ++ i).asSymbol;
	var amountId = (\modAmount ++ i).asSymbol;
	~paramMap[sourceId] = NopaListParam(~keySources)
		.label_("Mod Source " ++ i)
		.displayStrategy_(NopaListString())
		.value_(0);
	~paramMap[targetId] = NopaListParam(~keyTargets)
		.label_("Mod Target " ++ i)
		.displayStrategy_(NopaListString())
		.value_(0);
	~paramMap[amountId] = NopaMirrorParam(1, 3)
		.label_("Mod Amount " ++ i)
		.displayStrategy_(NopaPercentString().center)
		.value_(0);
	~keyParams = ~keyParams.addAll([sourceId, targetId, amountId]);
};

if (~presetMap.isNil.not) {
	~paramMap.import(~presetMap);
};
