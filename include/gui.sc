AppClock.play(Routine({
	var w;
	var toneColor = Color.hsv(0.15, 0.5, 1, 1);
	var envColor = Color.hsv(0.075, 0.35, 1, 1);
	var postColor = Color.hsv(0.9, 0.3, 1, 1);
	var modColor = Color.hsv(0.8, 0.35, 1, 1);
	var groups = [
		Event[
			\name -> "Voice",
			\color -> toneColor,
			\params -> [
				[
					\triggerButton,
					\voice,
				],
			],
		],
		Event[
			\name -> "Freq",
			\color -> toneColor,
			\params -> [
				\baseNote,
				\portamento,
				[
					\leftNote,
					\detune,
				],
				[
					\bendSteps,
					\vibratoDepth,
				],
			],
		],
		Event[
			\name -> "Impulse",
			\color -> toneColor,
			\params -> [
				\sustain,
				\decay,
				[
					\impulse,
					\impulseVel,
				],
				[
					\hardness,
					\hardnessVel,
				],
			],
		],
		Event[
			\name -> "Tone",
			\color -> toneColor,
			\params -> [
				\harmonics,
				\filter,
				[
					\filterReso,
					\filterVel,
				],
				[
					\filterNote,
					\filterEnv,
				],
			],
			\labelCut -> Event[
				"Filter " -> "",
			],
		],
		Event[
			\name -> "Sustain",
			\color -> toneColor,
			\params -> [
				\resonatorLevel,
				\resonatorPitchShift,
				\feedback,
				\feedbackHiCut,
			],
			\labelCut -> Event[
				"Reso " -> "Sustain ",
			],
		],
		// Event[
		// 	\name -> "Formant",
		// 	\color -> toneColor,
		// 	\params -> [
		// 		\formantDepth,
		// 		\formant,
		// 		\formantReso,
		// 		[
		// 			\formantNote,
		// 			\formantEnv,
		// 		],
		// 	],
		// 	\labelCut -> Event[
		// 		"Formant " -> "",
		// 	],
		// ],
		Event[
			\name -> "Volume/Pan",
			\color -> toneColor,
			\params -> [
				\amp,
				\pan,
				[
					\tremoloDepth,
					\panAlgo,
				],
			],
		],
		Event[
			\name -> "Envelope",
			\color -> envColor,
			\params -> [
				\envShape,
				\envTime,
			],
			\labelCut -> Event[
				"Env " -> "",
			],
		],
		Event[
			\name -> "LFO 1 - Dynamics",
			\color -> envColor,
			\params -> [
				\lfo1Speed,
				[
					\lfo1Slew,
					\lfo1Algo,
				],
				[
					\lfo1Walk,
					\lfo1Enter,
				],
			],
			\labelCut -> Event[
				"Lfo1 " -> "",
			],
		],
		Event[
			\name -> "LFO 2 - Stereo",
			\color -> envColor,
			\params -> [
				\lfo2Speed,
				[
					\lfo2Spread,
					\lfo2StereoSpin,
				],
			],
			\labelCut -> Event[
				"Lfo2 " -> "",
			],
		],
		Event[
			\name -> "LFO 3 - Randomize",
			\color -> envColor,
			\params -> [
				\lfo3Speed,
				[
					\lfo3Slew,
					\lfo3Algo,
				],
			],
			\labelCut -> Event[
				"Lfo3 " -> "",
			],
		],
		Event[
			\name -> "LFO 4 - Multiwave",
			\color -> envColor,
			\params -> [
				\lfo4Speed,
				[
					\lfo4Slew,
					\lfo4Algo,
				],
				[
					\lfo4Width,
					\lfo4Multi,
				],
			],
			\labelCut -> Event[
				"Lfo4 " -> "",
			],
		],
		Event[
			\name -> "Euclidean Env",
			\color -> envColor,
			\params -> [
				\eucSpeed,
				\eucLength,
				\eucInner,
				\eucStep,
			],
			\labelCut -> Event[
				"Euc " -> "",
			],
		],
		Event[
			\name -> "Chorus",
			\color -> postColor,
			\params -> [
				\chorusDepth,
				[
					\chorusSpeed,
					\chorusShape,
				],
			],
			\labelCut -> Event[
				"Chorus " -> "",
			],
		],
		Event[
			\name -> "Postprocessing",
			\color -> postColor,
			\params -> [
				\preGain,
				[
					\bassBoost,
					\bias,
				],
				\postGain,
			],
		],
		Event[
			\name -> "Delay",
			\color -> postColor,
			\params -> [
				\delayMix,
				\delayTime,
				[
					\delayRegen,
					\delayPingPong,
				]
			],
			\labelCut -> Event[
				"Delay " -> "",
			],
		],
	];

	~modcount.do {
		arg i;
		var source = (\modSource ++ i).asSymbol;
		var target = (\modTarget ++ i).asSymbol;
		var amount = (\modAmount ++ i).asSymbol;
		groups = groups.add(Event[
			\name -> ("Mod " ++ i),
			\color -> modColor,
			\params -> [
				[
					source,
					target,
				],
				amount,
			],
			\labelCut -> Event[
				"Mod " -> "",
				(" " ++ i) -> "",
			],
			// \nextLine -> (i == 0),
		]);
	};

	// \sustain, \decay, \hardness, \hardnessVel, \filter, \filterDrive, \filterNote, \filterVel, \filterEnv, \resonatorLevel


	Window.closeAll;
	w = Window.new("P~e~e~n~o", 650@800).front;
	w.view.decorator=FlowLayout(w.view.bounds);
	w.view.decorator.gap=2@2;

	groups.do {
		arg group;
		var container, vc, layout, subContainer, color;
		var columns = [];
		if (group[\nextLine].asBoolean) {
			w.view.decorator.nextLine;
		};
		group[\params].do {
			arg key;
			if (key.isKindOf(Array)) {
				var subcolumn = [];
				key.do {
					arg subkey;
					var widget;
					var label;
					widget = if (~paramMap[subkey].isKindOf(SS2ParamList)) {
						SS2ParamSelect();
					} {
						if (subkey == \triggerButton) {
							\triggerButton.postln;
							Button(bounds: 32@32)
									.action_({
									~group.set(\t_retrig, 1);
								});
						} {
							SS2ParamSlider();
						};
					};
					if (widget.isKindOf(SS2ParamWidget)) {
						var label;
						~paramMap[subkey].addObserver(widget);
						label = widget.label().asString();
						if (group[\labelCut].isNil.not) {
							group[\labelCut].keysValuesDo {
								arg find, replacement;
								label = label.replace(find, replacement);
							};
							widget.label = label;
						};
						widget.label = label;
					};
					subcolumn = subcolumn.add(widget.asView);
				};
				columns = columns.add(subcolumn);
			} {
				var widget = if (~paramMap[key].isKindOf(SS2ParamList)) {
					SS2ParamOptions();
				} {
					var knob = SS2ParamKnob();
					knob;
				};

				// widget.decorator.margin = 4@24;
				// widget.decorator.gap = 4@24;
				if (widget.isKindOf(SS2ParamWidget)) {
					var label;
					~paramMap[key].addObserver(widget);
					label = widget.label().asString();
					if (group[\labelCut].isNil.not) {
						group[\labelCut].keysValuesDo {
							arg find, replacement;
							label = label.replace(find, replacement);
						};
					};
					widget.label = label;
				};
				columns = columns.add([[widget.asView, rows: 2]]);
			};
		};

		color = group[\color].defaultWhenNil(Color(0, 0, 0, 0.1));
		subContainer = VLayoutView(w, (64 * group[\params].size)@96)
			.backColor_(color);
		StaticText(subContainer, 80@12).string_(group.name);
		layout = GridLayout.columns(*columns).hSpacing_(0).vSpacing_(0);
		container = CompositeView(subContainer, (64 * group[\params].size)@72).layout_(layout);
		layout.children.do {
			arg child;
			layout.setAlignment(child, \bottom);
		}
	};
}));
