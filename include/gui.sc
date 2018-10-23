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
					\filterDrive,
					\filterVel,
				],
				[
					\filterNote,
					\filterEnv,
				],
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
		],
		Event[
			\name -> "Formant",
			\color -> toneColor,
			\params -> [
				\formantDepth,
				\formant,
				[
					\formantNote,
					\formantEnv,
				],
			],
		],
		Event[
			\name -> "Pan",
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
		],
		Event[
			\name -> "LFO 1 - Dynamics",
			\color -> envColor,
			\params -> [
				\lfo1Speed,
				\lfo1Shape,
				[
					\lfo1Walk,
					\lfo1Enter,
				],
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
			// \nextLine -> (i == 0),
		]);
	};

	// \sustain, \decay, \hardness, \hardnessVel, \filter, \filterDrive, \filterNote, \filterVel, \filterEnv, \resonatorLevel


	Window.closeAll;
	w = Window.new("P~e~e~n~o", 550@800).front;
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
						~paramMap[subkey].addObserver(widget);
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

				widget.decorator.margin = 4@24;
				widget.decorator.gap = 4@24;
				~paramMap[key].addObserver(widget);
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
