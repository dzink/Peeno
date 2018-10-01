var groups = [
	Event[
		\name -> "Impulse",
		\params -> [
			\sustain,
			\decay,
			\impulse,
			\impulseFilter,
			[
				\impulseVel,
				\impulseFilterVel,
			]
		],
	],
	Event[
		\name -> "Tone",
		\params -> [
			\harmonics,
			\filter,
			\filterDrive,
			\filterVel,
			\filterNote,
			\filterEnv,
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
			\envShape,
			\envTime,
		],
	],
	Event[
		\name -> "LFO",
		\params -> [
			\lfo1Speed,
			\lfo1Shape,
			\lfo1Walk,
			\lfo1Enter,
			\vibratoDepth,
			\tremoloDepth,
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
			\postGain,
		],
	],
];

// \sustain, \decay, \impulseFilter, \impulseFilterVel, \filter, \filterDrive, \filterNote, \filterVel, \filterEnv, \resonatorLevel

var w;

Window.closeAll;
w = Window.new(bounds: 450@800).front;
w.view.decorator=FlowLayout(w.view.bounds);
w.view.decorator.gap=2@2;

groups.do {
	arg group;
	var container, vc, layout;
	var columns = [];
	StaticText(w, 80@12).string_(group.name);
	// w.view.decorator.newline;
	group[\params].do {
		arg key;
		if (key.isKindOf(Array)) {
			var subcolumn = [];
			key.do {
				arg subkey;
				var widget = SS2ParamSlider();
				~paramMap[subkey].addObserver(widget);
				subcolumn = subcolumn.add(widget.asView);
			};
			columns = columns.add(subcolumn);
		} {
			var widget = SS2ParamWidget();
			~paramMap[key].addObserver(widget);
			columns = columns.add([[widget.asView, rows: 2]]);
		};
	};

	layout = GridLayout.columns(*columns).hSpacing_(0).vSpacing_(0);
	container = CompositeView(w, (54 * group[\params].size)@96).layout_(layout);
	postln(*columns);
};
