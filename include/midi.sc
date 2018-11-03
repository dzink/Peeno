~keys = NodeDictionary[];
~keyPressList = List[];

MIDIFunc.noteOn({
	arg vel, note ... msg;
	var nodeArgs, retrigArgs, mapSymbol;
	var newNote;
	~keyPressList.add(note);
	// Generate new node args/retrigger node args
	nodeArgs = ~paramMap.asArray(~keyParams).addAll([\note, note, \gate, 1, \vel, vel]);
	[\vel, vel].postln;
	retrigArgs = Array[\gate, 1, \vel, vel, \note, note, \t_retrig, 1];

	// Choose mono or note, based on if this is mono or poly.
	if (~paramMap[\voice].symbol == \mono) {
		mapSymbol = \mono;
	} {
		mapSymbol = note
	};
	~keys.playOrRetrigger(mapSymbol, \key, nodeArgs, retrigArgs, ~group);
	~nodeWatcher.register(~keys[mapSymbol].node, true);
}, chan: [0,1]);

MIDIFunc.noteOff({
	arg vel, note;
	if (~paramMap[\voice].symbol == \mono) {
		var last = ~keyPressList.indexOf(note) == (~keyPressList.size - 1);
		if (last) {
			if (~keyPressList.size > 1) {
				var last = ~keyPressList.at(~keyPressList.size - 2);
				~keys.set(\mono, [\gate, 1, \note, last]);
			} {
				~keys.set(\mono, [\gate, 0]);
			};
		};
		~keyPressList.removeEvery([note]);
	} {
		~keys.set(note, [\gate, 0]);
	};
}, chan: [0,1]);

Event[
	\sustain -> SS2MidiFunc.cc(13),
].keysValuesDo {
	arg key, event;
	~paramMap[key].addEvent(event)
};

MIDIFunc.bend({
	arg val;
	val.postln;
	~bend.set(\bend, val.linlin(0, 16383, -1, 1));
});

MIDIFunc.cc({
	arg val, num;
	~mod.set(\bend, val.linlin(0, 127, 0, 1));
}, 1);
