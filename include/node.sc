
// Generate nodes, enable watcher.
//~compress = Synth(\compress, [\parallel, 0, \postGain, 1.0, \preGain, 0.5, \chorusDepth, 0, \chorusSpeed, 8, \chorusShape, 1, \reverb, 0.8, \bassBoost, 13]);
~group = Group.new();
~pink = Synth(\pink, [\chorusDepth, 4, \chorusSpeed, 1, \chorusShape, 0.5]);
~bend = Synth(\bend, [\out, ~bendBus]);
~nodeWatcher = NodeWatcher.newFrom(s).clear;
~nodeWatcher.register(RootNode(s));
~nodeWatcher.register(~group);

// Add to groups
~keyParams.do {
	arg key;
	~paramMap[key].addObserver(SS2ParamNodeObserver(~group, key));
};

// Add to controls
~compressParams.do {
	arg key;
	~paramMap[key].addObserver(SS2ParamNodeObserver(~compress, key));
	~group.set(key, ~paramMap[key].value);
};
