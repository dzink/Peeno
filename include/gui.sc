var groups = [
	Event[
		\name -> "Impulse",
		\params -> [
			\sustain,
			\decay,
			\impulse,
			\impulseVel,
			\impulseFilter,
			\impulseFilterVel,
		],
	],
	Event[
		\name -> "Tone",
		\params -> [
			\harmonics,
			\filter,
			\filterDrive,
			\filterNote,
			\filterVel,
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
			\formantNote,
			\formantEnv,
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
		],
	],
	Event[
		\name -> "Chorus",
		\params -> [
			\chorusDepth,
		],
	],
];

// \sustain, \decay, \impulseFilter, \impulseFilterVel, \filter, \filterDrive, \filterNote, \filterVel, \filterEnv, \resonatorLevel

var w;

Window.closeAll;
w = Window.new(bounds: 360@800).front;
w.view.decorator=FlowLayout(w.view.bounds);
w.view.decorator.gap=2@2;

groups.do {
	arg group;
	var container;
	StaticText(w, 80@12).string_(group.name);
	container = CompositeView(w, 450@84);
	container.decorator = FlowLayout(container.bounds);
	container.decorator.gap = 2@2;
	group[\params].do {
		arg key;
		~paramMap[key].addObserver(SS2ParamWidget(container));
	};
};
