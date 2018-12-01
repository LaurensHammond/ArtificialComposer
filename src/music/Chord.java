package music;

import types.ChordName;
import types.ScaleName;

public class Chord implements Comparable<Object>, Cloneable {
	private ChordName name;
	private boolean isTriad;
	private Note notes[];
	private ScaleName type;
	private int priority;
	private int position;
	private int inversion;
	private boolean pac;

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public Chord(ChordName name, boolean isTriad, Note[] notes, ScaleName type, int priority) {
		super();
		this.name = name;
		this.isTriad = isTriad;
		this.notes = notes;
		this.type = type;
		this.priority = priority;
		this.pac = false;
	}


	public boolean isPac() {
		return pac;
	}

	public void setPac() {
		this.pac = true;
	}

	public ChordName getName() {
		return name;
	}

	public void setName(ChordName name) {
		this.name = name;
	}

	public boolean isTriad() {
		return isTriad;
	}

	public void setTriad(boolean isTriad) {
		this.isTriad = isTriad;
	}

	public Note[] getNotes() {
		return notes;
	}

	public void setNotes(Note[] notes) {
		this.notes = notes;
	}

	public ScaleName getType() {
		return type;
	}

	public void setType(ScaleName type) {
		this.type = type;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public String toString() {
		return this.inversion+"-"+this.name +"-"+this.position+""+this.pac;
	}

	@Override
	public int compareTo(Object chord) {
		int compareage = ((Chord) chord).getPriority();
		
		// TODO Auto-generated method stub
		return this.priority-compareage;
	}
	public Chord clone() throws CloneNotSupportedException {
        return (Chord) super.clone();
	}

	public void setInversion(int inversion) {
		this.inversion = inversion;
	}

	public int getInversion() {
		return inversion;
	}

	public void setPac(boolean pac) {
		this.pac = pac;
	}

}
