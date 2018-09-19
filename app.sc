(
// @TODO: I'd love to make this work but it doesn't seem to on any platform.
var currentDir = "/Users/danzinkevich/Peeno/";

Routine {

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
	});

	(currentDir +/+ "include/synthdefs.sc").load();
	(currentDir +/+ "include/paramMap.sc").load();
	(currentDir +/+ "include/node.sc").load();
	(currentDir +/+ "include/gui.sc").load();
	(currentDir +/+ "include/midi.sc").load();
}.run;

)

(
s.queryAllNodes;
)
