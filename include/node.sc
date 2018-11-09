
// Generate nodes, enable watcher.
~compress = Synth(\compress, ~paramMap.asArray(~compressParams));
~group = Group.new();
~pink = Synth(\pink, [\chorusDepth, 4, \chorusSpeed, 1, \chorusShape, 0.5]);
~bend = Synth(\bend, [\out, ~bendBus.index]);
~mod = Synth(\bend, [\out, ~modBus.index]);
~nodeWatcher = NodeWatcher.newFrom(s).clear;
~nodeWatcher.register(RootNode(s));
~nodeWatcher.register(~group);

s.sync;

~paramMap.linkToNode(~group, ~keyParams);
~paramMap.linkToNode(~compress, ~compressParams);
