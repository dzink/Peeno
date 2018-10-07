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
			],
			[	\harmonics, \sustain],
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
			[
				\lfo1Walk,
				\lfo1Enter,
			],
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
w = Window.new("P~e~e~n~o", 550@800).front;
w.view.decorator=FlowLayout(w.view.bounds);
w.view.decorator.gap=2@2;

groups.do {
	arg group;
	var container, vc, layout;
	var columns = [];

	// StaticText(w, 80@12).string_(group.name);

	// w.view.decorator.newline;
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

	layout = GridLayout.columns(*columns).hSpacing_(0).vSpacing_(0);
	container = CompositeView(w, (64 * group[\params].size)@72).layout_(layout);
	layout.children.do {
		arg child;
		layout.setAlignment(child, \bottom);
	}
};
