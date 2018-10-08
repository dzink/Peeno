
// Generate nodes, enable watcher.
~compress = Synth(\compress, ~paramMap.asArray(~compressParams));
~group = Group.new();
~pink = Synth(\pink, [\chorusDepth, 4, \chorusSpeed, 1, \chorusShape, 0.5]);
~bend = Synth(\bend, [\out, ~bendBus.index]);
~mod = Synth(\bend, [\out, ~modBus.index]);
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
