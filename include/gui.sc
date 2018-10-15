var w;
var groups = [
	Event[
		\name -> "Freq",
		\params -> [
			\baseNote,
			[
				\leftNote,
				\detune,
			],
		],
	],
	Event[
		\name -> "Impulse",
		\params -> [
			\sustain,
			\decay,
			\impulse,
			\hardness,
			[
				\impulseVel,
				\hardnessVel,
			],
		],
	],
	Event[
		\name -> "Tone",
		\params -> [
			\harmonics,
			\filter,
			\filterDrive,
			\filterVel,
			[
				\filterNote,
				\filterEnv,
			],
		],
	],
	Event[
		\name -> "Sustain",
		\params -> [
			\resonatorLevel,
			\resonatorPitchShift,
			\feedback,
			\feedbackHiCut,
		],
	],
	Event[
		\name -> "Formant",
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
		\name -> "Movement",
		\params -> [
			\vibratoDepth,
			\tremoloDepth,
		],
	],
	Event[
		\name -> "Envelope",
		\params -> [
			\envShape,
			\envTime,
		],
	],
	Event[
		\name -> "LFO 1",
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
		\name -> "LFO 2",
		\params -> [
			\lfo2Speed,
			[
				\lfo2Spread,
				\lfo2StereoSpin,
			],
		],
	],
	Event[
		\name -> "Pan",
		\params -> [
			[
				\pan,
				\panAlgo,
			],
		],
	],
	Event[
		\name -> "Chorus",
		\params -> [
			\chorusDepth,
			\chorusSpeed,
			\chorusShape,
		],
	],
	Event[
		\name -> "Postprocessing",
		\params -> [
			\preGain,
			\reverb,
			\bassBoost,
			\bias,
			// \postGain,
		],
	],

];

4.do {
	arg i;
	var source = (\modSource ++ i).asSymbol;
	var target = (\modTarget ++ i).asSymbol;
	var amount = (\modAmount ++ i).asSymbol;
	groups = groups.add(Event[
		\name -> ("Mod " ++ i),
		\params -> [
			[
				source,
				target,
			],
			amount,
		],
		\nextLine -> (i == 0),
	]);
};

// \sustain, \decay, \hardness, \hardnessVel, \filter, \filterDrive, \filterNote, \filterVel, \filterEnv, \resonatorLevel


Window.closeAll;
w = Window.new("P~e~e~n~o", 550@800).front;
w.view.decorator=FlowLayout(w.view.bounds);
w.view.decorator.gap=2@2;
b = Button(w, 32@32)
	.action_({
		~group.set(\t_retrig, 1);
		\meoeoe.postln;
	});

groups.do {
	arg group;
	var container, vc, layout, subContainer;
	var columns = [];

	// StaticText(w, 80@12).string_(group.name);

	if (group[\nextLine].asBoolean) {
		w.view.decorator.nextLine;
	};
	group[\params].do {
		arg key;
		if (key.isKindOf(Array)) {
			var subcolumn = [];
			key.do {
				arg subkey;
				var widget = if (~paramMap[subkey].isKindOf(SS2ParamList)) {
					SS2ParamSelect(bounds: 64@32);
				} {
					SS2ParamSlider(bounds: 64@32);
				};
				~paramMap[subkey].addObserver(widget);
				subcolumn = subcolumn.add(widget.asView);
			};
			columns = columns.add(subcolumn);
		} {
			var widget = if (~paramMap[key].isKindOf(SS2ParamList)) {
				SS2ParamOptions();
			} {
				SS2ParamKnob();
			};

			widget.decorator.margin = 4@24;
			widget.decorator.gap = 4@24;
			~paramMap[key].addObserver(widget);
			columns = columns.add([[widget.asView, rows: 2]]);
		};
	};

	subContainer = VLayoutView(w, (64 * group[\params].size)@96)
		.backColor_(Color(0, 0, 0, 0.1));
	StaticText(subContainer, 80@12).string_(group.name);
	layout = GridLayout.columns(*columns).hSpacing_(0).vSpacing_(0);
	container = CompositeView(subContainer, (64 * group[\params].size)@72).layout_(layout);
	layout.children.do {
		arg child;
		layout.setAlignment(child, \bottom);
	}
};
