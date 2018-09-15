(
	CmdPeriod.run;
	// Groups and support synths

	// Compressor/effects

	// Global parameters/feedback

	// Build window and groups

	if (~paramMap.isNil.not) {
		~presetMap = SS2ParamPreset(~paramMap);
	};

	~paramMap = SS2ParamMap[
		\dummy -> SS2ParamContinuous(0, 1, 3)
			.displayStrategy_(SS2ParamDisplaySemitone())
			.addObserver(SS2ParamWidget(~w.dummyGroup)),
	];

	~presetMap.export(~paramMap);
)
