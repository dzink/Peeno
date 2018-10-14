(
// @TODO: I'd love to make this work but it doesn't seem to on any platform.
var currentDir = "/Users/danzinkevich/Peeno/";

Routine {
	var note = 64;

	// Initialize server and buses.
	if (s.serverRunning.not, {
		var o = Server.local.options;
		o.numAudioBusChannels = 64;
		o.numControlBusChannels = 128;
		o.memSize = 2**20;
		o.numBuffers = 1024 * 16;
		o.numInputBusChannels = 6;
		o.numOutputBusChannels = 4;
		MIDIIn.connectAll;
		s.bootSync();
		~keyBus = Bus.audio(s, 2);
		~pinkBus = Bus.audio(s, 1);
		~bendBus = Bus.control(s, 1);
		~modBus = Bus.control(s, 1);
	});

	(currentDir +/+ "include/synthdefs.sc").load();
	(currentDir +/+ "include/paramMap.sc").load();
	(currentDir +/+ "include/gui.sc").load();

	s.sync;//0.5.wait;

	(currentDir +/+ "include/node.sc").load();
	(currentDir +/+ "include/midi.sc").load();



		//var note = 64;
	// ~keys.killAll;
	// ~keys.play(note, \key, ~paramMap.asArray(~keyParams).addAll([\note, note, \gate, 1, \vel, 1]);, ~group);
	note = note + 7;
	// ~keys.play(note, \key, ~paramMap.asArray(~keyParams).addAll([\note, note, \gate, 1, \vel, 1]);, ~group);
	note = note + 7;
	// ~keys.play(note, \key, ~paramMap.asArray(~keyParams).addAll([\note, note, \gate, 1, \vel, 1]);, ~group);
	// ~nodeWatcher.register(~keys[note], true);
	~paramMap
	// .setSymbol(\modSource0, \lfo2)
	// .setSymbol(\modTarget0, \resonance)
	// .setValue(\modAmount0, 0)
	// .setValue(\lfo2Speed, 50)
	// .setValue(\resonatorLevel, -inf)
	;
}.run;

)

(
~paramMap[\modSource0].symbol_(\lfo1).value.postln;
~paramMap[\modTarget0].symbol_(\note).value.postln;
~paramMap.asArray(~keyParams).asCompileString.postln;
)

(
s.queryAllNodes;
a = File.readAllString("/Users/danzinkevich/Peeno/migrations/201810081102.sc").interpret;
[a, \meow].postln;
)

(
~paramMap[\modSource0].value
)

(
SS2ParamMapPreset.generateMigration("/Users/danzinkevich/Peeno/migrations/", "cooooool");
)

(
var p = SS2ParamMapPreset.newFrom([\a, 2]).migrateTimeStamp_("201701011102");
p.migrateUp("/Users/danzinkevich/Peeno/migrations/");
// p.migrateUp("/Users/danzinkevich/Peeno/migrations/");
// p.migrateDown("/Users/danzinkevich/Peeno/migrations/", "20181008175701");
// p.migrateTo("/Users/danzinkevich/Peeno/migrations/", "20181008185637");
// p.removeAt(\a);
p.postln;

);

(
// var p = SS2ParamMapPreset[\a -> 1, \b -> \sine];
var p = SS2ParamMapPreset[('a' -> 7), ('c' -> 'sine')].migrateTimeStamp_("20171009023541");
var d = SS2ParamMapPresetDictionary("/Users/danzinkevich/Peeno/presets/").loadAll();
d.put(\p, p).save(\p);
// d.loadAll();
// d[\p][\a] = 44;
// d.save(\meoeoe);
// d.loadAll();
// d[\p].migrateDate
// d;
d.migrate("/Users/danzinkevich/Peeno/migrations/");
d.saveAll();

d.postln;
// p.postln;

)

(
SS2ParamMapPreset[\a -> 1, \c -> 'sine'].asCompileString.interpret.asCompileString.interpret.asCompileString.interpret.asCompileString.interpret
)

(
~paramMap[\harmonics].normalized_(0.75).value
)

(
SS2ParamMapPreset.newFrom([\meow, 1])
// IdentityDictionary.newFrom([])
)

(
[1,1,1].wrapExtend(2)
)

(
~paramMap.asPreset();
b = Button(w, Rect(10, 10, 360, 40));
)

(
var panSets = [-1, 0, 1];
panSets.do {
	arg pan;
	[pan, ([-1, 1] + (pan * 2)).clip(-1, 1)].postln;
};
)

(
play{
	var p = [-1, 1];//SinOsc.kr(1);
	var a = Saw.ar(44.1.dup);
	//a = Pan2.ar(a, p);
	a = Pan2.ar(a[0], p[0]) + Pan2.ar(a[1], p[1]);
	Amplitude.kr([a[0], a[1]]).round(0.1).poll;
	a;
}
)

(
~group.set(\lfo2Spread, -1)
)