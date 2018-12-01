package music;

import java.util.ArrayList;

import types.Sign;

/**
 * The class representing objects being 
 * the single musical notes.
 * @author kol2
 *
 */
public class Note implements Cloneable {
	
	private int pitch;
	private boolean modulation;
	private Sign sign;
	private int octave;
	private double rythmicValue;
	private boolean legato;
	private double address;
	private int priority;
	private Chord function;
	private ArrayList<Chord> possibleChords = new ArrayList<Chord>();
	
	public Chord getFunction() {
		return function;
	}
	public void setFunction(Chord function) {
		this.function = function;
	}
	public ArrayList<Chord> getPossibleChords() {
		return possibleChords;
	}
	public void addPossibleChords(Chord possibleChord, int position) throws CloneNotSupportedException {
		Chord chord = possibleChord.clone();
		
		chord.setPosition(position);
		this.possibleChords.add(chord);
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	
	public double getAddress() {
		return address;
	}
	public void setAddress(double adress) {
		this.address = adress;
	}
	public void setLegato(boolean legato) {
		this.legato = legato;
	}
	public Note(int pitch, double rythmicValue) {
		super();
		this.pitch = pitch;
		this.rythmicValue = rythmicValue;
		this.modulation = false;
		this.sign = null;
		this.legato = false;
		this.priority = 10;
	}
	public Note(int pitch, double rythmicValue, String sign) {
		super();
		if(sign=="#"||sign=="sharp") {
			this.modulation = true;
			this.sign = Sign.sharp;
		}else if(sign=="b"||sign=="flat"||sign=="bemolle") {
			this.modulation = true;
			this.sign = Sign.flat;
		}else {
			this.modulation = false;
			this.sign = null;
		}
		this.pitch = pitch;
		this.rythmicValue = rythmicValue;
		this.legato = false;
		this.priority = 10;
	}
	public Note(int pitch, double rythmicValue, String sign, int oct) {
		super();
		if(sign=="#"||sign=="sharp") {
			this.modulation = true;
			this.sign = Sign.sharp;
		}else if(sign=="b"||sign=="flat"||sign=="bemolle") {
			this.modulation = true;
			this.sign = Sign.flat;
		}else {
			this.modulation = false;
			this.sign = null;
		}
		this.pitch = pitch;
		this.rythmicValue = rythmicValue;
		this.legato = false;
		this.octave = oct;
		this.priority = 10;
	}
	public Note(int pitch) {
		super();
		this.pitch = pitch;
		this.priority = 10;
	}
	
	public Note(int pitch, String sign) {
		super();
		this.pitch = pitch;
		
		if(sign=="#"||sign=="sharp") {
			this.modulation = true;
			this.sign = Sign.sharp;
		}else if(sign=="b"||sign=="flat"||sign=="bemolle") {
			this.modulation = true;
			this.sign = Sign.flat;
		}else {
			this.modulation = false;
			this.sign = null;
		}
		this.priority = 10;
	}
	
	public boolean isLegato() {
		return legato;
	}
	public void setLegato() {
		this.legato = true;
	}
	public Sign getSign() {
		return sign;
	}
	public void setSign(Sign sign) {
		this.sign = sign;
	}	
	public int getPitch() {
		return pitch;
	}
	public void setPitch(int pitch) {
		this.pitch = pitch;
	}
	public boolean isModulation() {
		return modulation;
	}
	public void setModulation(boolean modulation) {
		this.modulation = modulation;
	}
	public int getOctave() {
		return octave;
	}
	public void setOctave(int octave) {
		this.octave = octave;
	}
	public double getRythmicValue() {
		return rythmicValue;
	}
	public void setRythmicValue(double rythmicValue) {
		this.rythmicValue = rythmicValue;
	}
	public void divideNote() {
		this.rythmicValue = 2 * this.rythmicValue ; 
	}
	@Override
	public String toString() {
		if(this.isModulation())
			return " ["+this.getPitch()+"|"+this.rythmicValue+"|"+this.address+"|"+this.priority+"|"+this.sign+"|"+this.function+"] ";
		else
			return " ["+this.getPitch()+"|"+this.rythmicValue+"|"+this.address+"|"+this.priority+"|"+this.function+"] ";
	}

	public void doubleNote() {
		this.rythmicValue /= 2;
	}

	public Note clone() throws CloneNotSupportedException {
        return (Note) super.clone();
}
}
