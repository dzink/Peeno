~keys = NodeDictionary[];

MIDIFunc.noteOn({
	arg vel, note ... msg;
	var nodeArgs, retrigArgs;
	var newNote;

	// Generate new node args/retrigger node args
	nodeArgs = ~paramMap.asArray(~keyParams).addAll([\note, note, \gate, 1, \vel, vel]);
	[\vel, vel].postln;
	retrigArgs = Array[\gate, 1, \vel, vel];
	~keys.playOrRetrigger(note, \key, nodeArgs, retrigArgs, ~group);
	~nodeWatcher.register(~keys[note], true);
}, chan: 0);

MIDIFunc.noteOff({
	arg vel, note;
	~keys.set(note, [\gate, 0]);
}, chan: 0);

Event[
	\sustain -> SS2MidiFunc.cc(13),
].keysValuesDo {
	arg key, event;
	~paramMap[key].addEvent(event)
};
