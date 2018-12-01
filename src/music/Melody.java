package music;

import java.util.ArrayList;

import types.Key;

/**
 * The class Melody defines the object representing a melody
 * 
 * @author kol2
 *
 */
public class Melody {
	private Key key;
	private Metre metre;
	private int tempo;

	private ArrayList<Bar> bars = new ArrayList<Bar>();

	public Melody(Key key, Metre metre, int tempo, Bar firstBar) {
		super();
		this.key = key;
		this.metre = metre;
		this.tempo = tempo;
		bars.add(firstBar);
	}

	public Melody() {
	}

	public Melody(Key key, Metre metre, int tempo) {
		super();
		this.key = key;
		this.metre = metre;
		this.tempo = tempo;
		bars.add(new Bar(key, metre));
	}

	/**
	 * Method adding the note to the end of melody
	 * @param note
	 * @throws CloneNotSupportedException
	 */
	public void addNote(Note note) throws CloneNotSupportedException {
		// if bar is filled then add new bar
		if (bars.get(bars.size() - 1).isFilled()) {
			bars.add(new Bar(this.key, this.metre));
		}
		if (bars.get(bars.size() - 1).canAddNote(note)) {
			double a = (bars.size() - 1) * bars.get(bars.size() - 1).getLength()
					+ bars.get(bars.size() - 1).currentFilling();
			note.setAddress(a);
			bars.get(bars.size() - 1).addNote(note);
		} else {
			Note note1 = note.clone();
			note1.divideNote();
			Note note2 = note.clone();
			note2.divideNote();
			note2.setLegato();
			addNote(note1);
			addNote(note2);
		}
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Metre getMetre() {
		return metre;
	}

	public void setMetre(Metre metre) {
		this.metre = metre;
	}

	public int getTempo() {
		return tempo;
	}

	public void setTempo(int tempo) {
		this.tempo = tempo;
	}

	public void printMelody() {
		for (Bar b : bars) {
			b.printBar();
		}
	}

	public ArrayList<Bar> getBars() {
		return bars;
	}

	public void setBars(ArrayList<Bar> bars) {
		this.bars = bars;
	}

	public void setFirstBar(Bar firstBar) {
		bars.set(0, firstBar);

	}

}
