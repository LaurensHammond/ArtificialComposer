package music;

import java.util.ArrayList;

import types.Key;

public class Bar {
	private Key key;
	private Metre metre;
	private int length;
	
	private ArrayList<Note> notes = new ArrayList<Note>();
	
	public Bar(Key key, Metre metre){
		super();
		this.key = key;
		this.metre = metre;
		this.length = metre.getLenght();
		
	}
	
	public Bar(Key key, Metre firstBarMetre, ArrayList<Note> firstBarNotes) {
		super();
		this.key = key;
		this.metre = firstBarMetre;
		this.length = firstBarMetre.getLenght();
		this.notes.addAll(firstBarNotes);
	}

	public double readNoteLength(Note n) {
		return (double)this.getMetre().getBase()/(double)n.getRythmicValue();
	}

	/**
	 * Check if note can be added to the bar
	 * @param n object of Note class
	 * @return true if note can be added, false if not cannot be added
	 */
	public boolean canAddNote(Note n) {
		if(this.currentFilling()+this.readNoteLength(n)<=this.length) {
			return true;
		}else
			return false;
	}
	

	public void addNote(Note n) {
		if(!this.isFilled()) {
			if(this.canAddNote(n)) {
				notes.add(n);
			}
		}
	}
	
	public void printBar() {
		System.out.print(this.metre);
		System.out.print(notes.size()+" ");
		for(Note n: notes) {
			System.out.print(n.toString());
			if(n.isModulation())
				System.out.print(n.getSign().name());
		}
		System.out.println(this.isFilled());
	}
	
	/**
	 * Function to check the level of filling a bar with notes
	 * @return the current sum of notes' length in bar
	 */
	public double currentFilling() {
		double filling = 0;
		for (Note n : notes) {
			filling += readNoteLength(n);
		}
		return filling;
	}
	public boolean isFilled() {
		if(this.currentFilling()<this.length)
			return false;
		else
			return true;
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


	public ArrayList<Note> getNotes() {
		return notes;
	}

	public void setNotes(ArrayList<Note> notes) {
		this.notes = notes;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public double convertToNoteRythmicValue(double val) {
		
		return this.getMetre().getBase()/val;
	}

	public boolean doNotesHavePriority() {
		boolean i = true;
		for(Note n: notes){
			if(n.getPriority()==10){
				i = false;				
			}
		}
		return i;
	}

}
