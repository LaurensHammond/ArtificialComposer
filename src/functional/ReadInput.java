package functional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;

import jm.JMC;
import music.Melody;
import signal.Event;
import signal.Sound;

public class ReadInput {

	private static ArrayList<Event> events = new ArrayList<Event>();
	private static ArrayList<Sound> sounds = new ArrayList<Sound>();

	static ArrayList<MidiDevice> inputInstruments = new ArrayList<MidiDevice>();
	static MidiDevice currentInstrument = null;
	static Receiver myReceiver = null;
	static Sequencer mySequencer = null;
	static Transmitter myTransmitter = null;
	private static Synthesizer synth;

	public Melody readMelody() throws MidiUnavailableException, InterruptedException, InvalidMidiDataException,
			CloneNotSupportedException {
		
		mySequencer = MidiSystem.getSequencer();

		MidiDevice myDevice = findDevice();
		openDevices(myDevice);
		setConnection(myTransmitter, myReceiver);
		setSequencer(mySequencer);
		if (currentInstrument != null) {
			System.out.println("\n" + currentInstrument.getDeviceInfo().getName() + " is waiting for input melody:\n\n");
			listenToMidi();
			closeDevices(inputInstruments);
			return createScore();

		} else
			closeDevices(inputInstruments);

		return null;

	}

	private static void createSounds() {
		if (events.size() > 0) {

			long t1 = events.get(events.size() - 2).getTime();
			long t2 = events.get(events.size() - 1).getTime();
			int duration = (int) (t2 - t1);
			System.out.println(duration);
			sounds.add(new Sound(events.get(events.size() - 1).getPitch(), duration));

			for (int i = events.size() - 3; i >= 0; i--) {
				if (events.get(i).getStatus() == 144) {
					duration = (int) (t1 - events.get(i).getTime());
					if (duration >= 70) { // in case of accidently pressed key not taken into account 
						System.out.println(duration);
						t1 = events.get(i).getTime();
						sounds.add(new Sound(events.get(i).getPitch(), duration));
					}
				}

			}
			Collections.reverse(sounds);
			System.out.println(sounds);
		}
	}

	private static Melody createScore() throws CloneNotSupportedException {
		createSounds();

		Melody melody = Analyst.convertIntoMelody(sounds);

		return melody;
	}

	private static void closeDevices(ArrayList<MidiDevice> iI) throws MidiUnavailableException {

		if (mySequencer.isOpen())
			mySequencer.close();
		if(synth.isOpen())
			synth.close();
		if (inputInstruments.size() > 0) {
			System.out.println("System has closed " + inputInstruments.size() + " MIDI devices");

			for (int i = 0; i < iI.size(); i++) {
				if (iI.get(i).isOpen()) {
					iI.get(i).close();
					System.out.println(iI.get(i).getDeviceInfo().getName() + " has been closed");
				}
			}
		}

	}

	private static void listenToMidi() throws MidiUnavailableException, InvalidMidiDataException {
		long lastEventTime, firstEventTime;
		Sequence s = mySequencer.getSequence();

		
		 /* receiving the first event wait for the first event when it
		  * appears add to the events list as a very first element */
		 

		do {
			mySequencer.startRecording();
			s = mySequencer.getSequence();
		} while (s.getTracks()[0].size() <= 1);
		firstEventTime = System.currentTimeMillis();

		lastEventTime = 0;
		events.add(new Event(s.getTracks()[0].get(0).getMessage().getStatus(), lastEventTime,
				s.getTracks()[0].get(0).getMessage().getMessage()[1]));
		int lastNote = s.getTracks()[0].get(0).getMessage().getMessage()[1];
		ShortMessage message = new ShortMessage();
		message.setMessage(ShortMessage.NOTE_ON, 0, lastNote, 90);
		synth.getReceiver().send(message, -1);
		printEvent(s);

		/* receiving the other events when event appear appears add to the
		 * events list as a very first element */
		do {
			if (s.getTracks()[0].size() > 1)
				mySequencer.getSequence().getTracks()[0].remove(mySequencer.getSequence().getTracks()[0].get(0));
			do {
				s = mySequencer.getSequence();
			} while ((s.getTracks()[0].size() <= 1)
					&& (System.currentTimeMillis() - firstEventTime < lastEventTime + 10000));

			printEvent(s);
			if (s.getTracks()[0].get(0).getMessage().getStatus() != 255) {
				lastEventTime = System.currentTimeMillis() - firstEventTime;
				events.add(new Event(s.getTracks()[0].get(0).getMessage().getStatus(), lastEventTime,
						s.getTracks()[0].get(0).getMessage().getMessage()[1]));
				if (s.getTracks()[0].get(0).getMessage().getStatus() == 144)
					message.setMessage(ShortMessage.NOTE_ON, 0, s.getTracks()[0].get(0).getMessage().getMessage()[1], 90);
				else
					message.setMessage(ShortMessage.NOTE_OFF, 0, s.getTracks()[0].get(0).getMessage().getMessage()[1], 90);
				synth.getReceiver().send(message, -1);
					
			}
		} while (System.currentTimeMillis() - firstEventTime < lastEventTime + 10000);

		if (mySequencer.isRecording()) {
			mySequencer.stopRecording();
		}
		System.out.println(events);
	}

	private static void printEvent(Sequence s) {
		for (int i = 0; i < s.getTracks()[0].size() - 1; i++) {
			if (s.getTracks()[0].get(i).getMessage().getStatus() != 255) {
				if (s.getTracks()[0].get(i).getMessage().getStatus() == 144) {
					System.out.print("NoteOn  ");
					System.out.println(getNoteName(s.getTracks()[0].get(i).getMessage().getMessage()[1]));
					jm.music.data.Note n = new jm.music.data.Note();
					n.setPitch(s.getTracks()[0].get(i).getMessage().getMessage()[1]);
					n.setRhythmValue(JMC.SIXTEENTH_NOTE);
				} 
			}
		}
	}

	private static String getNoteName(byte b) {
		switch (b % 12) {
		case 0:
			return "c" + (b / 12 - 2);
		case 1:
			return "c#" + (b / 12 - 2);
		case 2:
			return "d" + (b / 12 - 2);
		case 3:
			return "d#" + (b / 12 - 2);
		case 4:
			return "e" + (b / 12 - 2);
		case 5:
			return "f" + (b / 12 - 2);
		case 6:
			return "f#" + (b / 12 - 2);
		case 7:
			return "g" + (b / 12 - 2);
		case 8:
			return "g#" + (b / 12 - 2);
		case 9:
			return "a" + (b / 12 - 2);
		case 10:
			return "a#" + (b / 12 - 2);
		case 11:
			return "b" + (b / 12 - 2);
		default:
			return null;
		}
	}

	private static void setSequencer(Sequencer sqncr) throws InvalidMidiDataException {
		Sequence seq = new Sequence(Sequence.SMPTE_24, 1);
		Track myTrack = seq.createTrack();
		seq.createTrack();
		sqncr.setSequence(seq);
		sqncr.setTickPosition(0);
		sqncr.recordEnable(myTrack, -1);

	}

	private static void setConnection(Transmitter transmitter, Receiver receiver) throws MidiUnavailableException {
		if (transmitter != null) {
			transmitter.setReceiver(receiver);
			System.out.println(transmitter+" has been contected with " + transmitter.getReceiver());
		}

	}

	private static void openDevices(MidiDevice myDevice) throws MidiUnavailableException, InterruptedException {

		myReceiver = mySequencer.getReceiver();
		System.out.println("\nSystem has found " + mySequencer.getDeviceInfo().getName() + " sequencer");
		mySequencer.open();
		synth.open();
		if (myDevice!=null) {
			System.out.println(
					"System has found " + myDevice + " devices what can be used to input MIDI:");

			try {
				
					myDevice.open();
					System.out.println(myDevice.getDeviceInfo().getName() + " has been opened");
				
			} catch (MidiUnavailableException e) {
				System.out.println(e);
			}
			currentInstrument = myDevice;
			myTransmitter = currentInstrument.getTransmitter();
		} else {
			System.out.println("No devices found to be opened.");
			TimeUnit.MILLISECONDS.sleep(1000);
			
		}
		System.out.println();
	}

	static MidiDevice findDevice() throws MidiUnavailableException {
		
		MidiDevice.Info[] midiDevicesInfo = MidiSystem.getMidiDeviceInfo();
		ArrayList<MidiDevice> avaliableInputDevices = new ArrayList<MidiDevice>();
		int deviceNo = 0;
		for(int i= 0 ; i < midiDevicesInfo.length; i++) {
        	MidiDevice device = MidiSystem.getMidiDevice(midiDevicesInfo[i]);
        	try {
	        	device.getTransmitter();
	        	if(!(device.getDeviceInfo().hashCode() == mySequencer.getDeviceInfo().hashCode())) {
	        		avaliableInputDevices.add(device);
	        	}
        	}catch(MidiUnavailableException e) {
        		
        	} 
        	 if (device instanceof Synthesizer){
				 synth = (Synthesizer) MidiSystem.getMidiDevice(midiDevicesInfo[i]);
			 }
		}
		if(avaliableInputDevices.size()>0) {
		System.out.println("Choose the device you play on:");
		for(int i = 0 ; i < avaliableInputDevices.size() ; i++ )
			System.out.println(i+" - "+avaliableInputDevices.get(i).getDeviceInfo().getName());
		Scanner reader = new Scanner(System.in);
		
		do {
			deviceNo = reader.nextInt();
		}while(deviceNo>=avaliableInputDevices.size());
		
		System.out.println("You've chosen the "+avaliableInputDevices.get(deviceNo).getDeviceInfo().getName());
		
		return avaliableInputDevices.get(deviceNo);
		}else return null;

	}
}