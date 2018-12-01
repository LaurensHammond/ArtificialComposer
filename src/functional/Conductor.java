package functional;



import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import music.Melody;
import music.Piece;


public class Conductor {
	
	
	private Melody cantusFirmus ;
	

	public static void main(String[] args) throws Throwable {
		while(true){
			Conductor conductor = new Conductor();
			Composer composer = new Composer();
			ReadInput listener = new ReadInput();
			Orchestra orchestra = new Orchestra();
			
			conductor.setCantusFirmus(listener.readMelody());
			if (conductor.getCantusFirmus() != null) {
				
				Piece myChorale = new Piece(conductor.getCantusFirmus(),"Artificial Composer","myPiece");
				composer.compose(myChorale);

				orchestra.conductMIDIOrchestra(myChorale);
				myChorale = null;
			}
			conductor.setCantusFirmus(null);
			conductor.finalize();
		}
	}

	public Melody getCantusFirmus() {
		return this.cantusFirmus;
	}

	public void setCantusFirmus(Melody cantusFirmus) {
		this.cantusFirmus = cantusFirmus;
	}
	
	

	

}
