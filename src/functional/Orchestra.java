package functional;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;

import music.Note;
import music.Piece;
import types.Key;
import types.ScaleName;
import types.Sign;

public class Orchestra {
	private static  Synthesizer synth = null;
	private static Sequencer mySequencer = null;
	private static ArrayList<MidiDevice> outputInstruments = new ArrayList<MidiDevice>();
	private static MidiDevice outputDevice = null;
	
	
	public void conductMIDIOrchestra(Piece piece) throws InvalidMidiDataException, MidiUnavailableException, InterruptedException {
	 	findSynthesizer();
	 	synth.getReceiver();
	 	if(synth.isOpen()){
		 	int tempo = piece.getTempo();
		 	int sopranChannel = 0, altChannel = 0, tenorChannel = 0, basChannel = 0;
		 	ShortMessage sopranMessage = new ShortMessage(); int lastSopran = 0;
		 	ShortMessage altoMessage = new ShortMessage(); int lastAlto = 0;
		 	ShortMessage tenorMessage = new ShortMessage(); int lastTenor = 0;
		 	ShortMessage basMessage = new ShortMessage(); int lastBas = 0;
		 	piece.getMelody().printMelody();
		 	for(int i=0;i<piece.getMelody().getBars().size();i++){
		 		for(Note n1 : piece.getMelody().getBars().get(i).getNotes()){
		 			System.out.println(i+" "+n1);
		 			if(n1.getFunction()==null){
		 				
		 				if(lastSopran!=0){
		 					sopranMessage.setMessage(ShortMessage.NOTE_OFF, sopranChannel, lastSopran, 90);
		 					synth.getReceiver().send(sopranMessage, -1);
		 				}
			 			lastSopran = readNote(n1,piece.getKey());
			 			sopranMessage.setMessage(ShortMessage.NOTE_ON, sopranChannel, lastSopran, 90);
			 			synth.getReceiver().send(sopranMessage, -1);
					TimeUnit.MILLISECONDS
							.sleep(60000 / tempo * (piece.getMetre().getBase()/ (long)n1.getRythmicValue()));
		 			}else{
		 				if(lastSopran!=0){
		 					sopranMessage.setMessage(ShortMessage.NOTE_OFF, sopranChannel, lastSopran, 90);
		 				}
		 				if(lastAlto!=0){
		 					altoMessage.setMessage(ShortMessage.NOTE_OFF, altChannel, lastAlto, 90);
		 				}
		 				if(lastTenor!=0){
		 					tenorMessage.setMessage(ShortMessage.NOTE_OFF, tenorChannel, lastTenor, 90);
		 				}
		 				if(lastBas!=0){
		 					basMessage.setMessage(ShortMessage.NOTE_OFF, basChannel, lastBas, 90);
		 				}
		 				
		 				lastSopran = readNote(n1,piece.getKey());
		 				
		 				synth.getReceiver().send(sopranMessage, -1);outputDevice.getReceiver().send(sopranMessage, -1);
			 			synth.getReceiver().send(altoMessage, -1);outputDevice.getReceiver().send(altoMessage, -1);
			 			synth.getReceiver().send(tenorMessage, -1);outputDevice.getReceiver().send(tenorMessage, -1);
			 			synth.getReceiver().send(basMessage, -1);outputDevice.getReceiver().send(basMessage, -1);
		 				
		 				
		 				for(Note n2 : piece.getAccompaniment()[0].getBars().get(i).getNotes())
		 					if(n1.getAddress()==n2.getAddress())
		 						lastAlto = readNote(n2,piece.getKey())-12;

		 				for(Note n2 : piece.getAccompaniment()[1].getBars().get(i).getNotes())
		 					if(n1.getAddress()==n2.getAddress())
		 						lastTenor = readNote(n2,piece.getKey())-12;
		 				
		 				for(Note n2 : piece.getAccompaniment()[2].getBars().get(i).getNotes())
		 					if(n1.getAddress()==n2.getAddress())
		 						lastBas = readNote(n2,piece.getKey())-24;
		 				
		 				
			 			
			 			sopranMessage.setMessage(ShortMessage.NOTE_ON, sopranChannel, lastSopran, 90);
			 			altoMessage.setMessage(ShortMessage.NOTE_ON, altChannel, lastAlto, 90);
			 			tenorMessage.setMessage(ShortMessage.NOTE_ON, tenorChannel, lastTenor, 90);
			 			basMessage.setMessage(ShortMessage.NOTE_ON, basChannel, lastBas, 90);
			 			
			 			synth.getReceiver().send(sopranMessage, -1);
			 			synth.getReceiver().send(altoMessage, -1);
			 			synth.getReceiver().send(tenorMessage, -1);
			 			synth.getReceiver().send(basMessage, -1);
			 			TimeUnit.MILLISECONDS.sleep(60000/tempo * (piece.getMetre().getBase()/ (long)n1.getRythmicValue()));
		 			}

		 		}
		 	}
 			TimeUnit.MILLISECONDS.sleep(60000/tempo * piece.getMetre().getLenght());
 			sopranMessage.setMessage(ShortMessage.NOTE_ON, sopranChannel, lastSopran, 90);
 			altoMessage.setMessage(ShortMessage.NOTE_ON, altChannel, lastAlto, 90);
 			tenorMessage.setMessage(ShortMessage.NOTE_ON, tenorChannel, lastTenor, 90);
 			basMessage.setMessage(ShortMessage.NOTE_ON, basChannel, lastBas, 90);
 			
 			synth.getReceiver().send(sopranMessage, -1);outputDevice.getReceiver().send(sopranMessage, -1);
 			synth.getReceiver().send(altoMessage, -1);outputDevice.getReceiver().send(altoMessage, -1);
 			synth.getReceiver().send(tenorMessage, -1);outputDevice.getReceiver().send(tenorMessage, -1);
 			synth.getReceiver().send(basMessage, -1);outputDevice.getReceiver().send(basMessage, -1);
 			synth.close();
	 	}
}

private static int readNote(Note note, Key key) {
	int finalPitch = 0;
	ScaleName scale;
	int shift = 0;
	if(key.equals(Key.C)||key.equals(Key.A)||key.equals(Key.Aflat)||key.equals(Key.Aflat)||key.equals(Key.B)||key.equals(Key.Bflat)||
			key.equals(Key.D)||key.equals(Key.Dflat)||key.equals(Key.E)||key.equals(Key.Eflat)||key.equals(Key.F)||
			key.equals(Key.Fsharp)||key.equals(Key.G)){
		scale = ScaleName.major;
	}else{
		scale = ScaleName.minor;
	}
	if(key.equals(Key.C)||key.equals(Key.c))
		shift = 0;
	else if(key.equals(Key.Dflat)||key.equals(Key.csharp))
		shift = 1;
	else if(key.equals(Key.D)||key.equals(Key.d))
		shift = 2;
	else if(key.equals(Key.Eflat)||key.equals(Key.dsharp))
	shift = 3;
	else if(key.equals(Key.E)||key.equals(Key.e))
		shift = 4;
	else if(key.equals(Key.F)||key.equals(Key.f))
		shift = 5;
	else if(key.equals(Key.Fsharp)||key.equals(Key.fsharp))
		shift = 6;
	else if(key.equals(Key.G)||key.equals(Key.g))
		shift = 7;
	else if(key.equals(Key.Aflat)||key.equals(Key.Aflat))
		shift = 8;
	else if(key.equals(Key.A)||key.equals(Key.a))
		shift = 9;
	else if(key.equals(Key.Bflat)||key.equals(Key.bflat))
		shift = 10;
	else if(key.equals(Key.B)||key.equals(Key.b))
		shift = 11;

	switch(note.getPitch()){
	case 1:
		finalPitch = note.getOctave()*12 + 0 + shift;
		break;
	case 2:
		finalPitch = note.getOctave()*12 + 2 + shift;
		break;
	case 3:
		if(scale.equals(ScaleName.minor))
			finalPitch = note.getOctave()*12 + 3 + shift;
		else
			finalPitch = note.getOctave()*12 + 4 + shift;
		break;
	case 4:
		finalPitch = note.getOctave()*12 + 5 + shift;
		break;
	case 5:
		finalPitch = note.getOctave()*12 + 7 + shift;
		break;
	case 6:
		if(scale.equals(ScaleName.minor))
			finalPitch = note.getOctave()*12 + 8 + shift;
		else
			finalPitch = note.getOctave()*12 + 9 + shift;
		break;
	case 7:
		if(scale.equals(ScaleName.minor))
			finalPitch = note.getOctave()*12 + 10 + shift;
		else
			finalPitch = note.getOctave()*12 + 11 + shift;
		break;
	}
	if(note.isModulation()){
		if(note.getSign().equals(Sign.flat))
			finalPitch -=1;
		else
			finalPitch +=1;
	}
	return finalPitch;
}

private static void findSynthesizer() throws MidiUnavailableException, InvalidMidiDataException {
	
	
	
	MidiDevice device = null;	
	MidiDevice.Info[] midiDevicesInfo = MidiSystem.getMidiDeviceInfo();
	for (int i = 0; i < midiDevicesInfo.length; i++) {
	 
	    device = MidiSystem.getMidiDevice(midiDevicesInfo[i]);

	    if (device instanceof Synthesizer) {
	    	synth = (Synthesizer) MidiSystem.getMidiDevice(midiDevicesInfo[i]);
	    	synth.open();
	    	if(synth.isOpen()){
	    		System.out.println(synth+" has been opened");
	    	}
	    }

	    	try {
				device.getReceiver();
				outputInstruments.add(device);
				System.out.println("\n");
			} catch (MidiUnavailableException e) {
				System.out.println(e.toString() + "\n");
			}
	    	for(MidiDevice d: outputInstruments){
	    		if(d.getDeviceInfo().hashCode() != synth.hashCode()){ //condition for instrument
	    			outputDevice = d;
	    		}
	    	}
	    
	}
	
}
}
