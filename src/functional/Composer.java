package functional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


import music.Bar;
import music.Chord;
import music.Melody;
import music.Metre;
import music.Note;
import music.Piece;
import types.ChordName;
import types.Key;
import types.ScaleName;

public class Composer {
	private ArrayList<Chord> chordsForMajor = new ArrayList<Chord>();
	private ArrayList<Chord> chordsForMinor = new ArrayList<Chord>();
	private ArrayList<Note> notes = new ArrayList<Note>();

	public Composer() {
		super();
		this.implementChords();

	}

	public void compose(Piece piece) throws CloneNotSupportedException {
		
		setNotesPriority(piece);
		getHarmonicNotes(piece, 2);
		setPossibleChordsForNotes(notes, piece);
		setCadences();
		setConnections();
		setSecondaryChords();
		fillEmptyPlaces();
		createVoices(piece);
		
	}

	private void fillEmptyPlaces() {
		for(Note n: notes){
			if(n.getFunction() == null){
				
				Random rand = new Random();
				n.setFunction(n.getPossibleChords().get(rand.nextInt(n.getPossibleChords().size())));
				
			}
		}
		
	}

	private void createVoices(Piece piece) throws CloneNotSupportedException {
		setBas(piece);
		setMiddleVoices(piece);
	}

	private void setBas(Piece piece) {

		if (notes.get(notes.size() - 1).getFunction() != null)
			notes.get(notes.size() - 1).getFunction().setInversion(1);
		if (notes.get(0).getFunction() != null)
			notes.get(0).getFunction().setInversion(1);

		for (int i = 1; i < notes.size() - 1; i++) {
			if (notes.get(i).getFunction() != null) {
				if (notes.get(i).getFunction().getPosition() != 1) {
					notes.get(i).getFunction().setInversion(1);
				} else {

					Random rand = new Random();
					notes.get(i).getFunction().setInversion(rand.nextInt(3) * 2 + 1);

				}
			}
		}

		for (int i = 1; i <= notes.size() - 1; i++) {
			if(notes.get(i).getFunction()!=null && notes.get(i - 1).getFunction()!=null){
				if (notes.get(i).getFunction().getName().equals(notes.get(i - 1).getFunction().getName())) {
					if (notes.get(i).getFunction().getInversion() == 1)
						if (notes.get(i).getFunction().getPosition() != 3)
							notes.get(i).getFunction().setInversion(3);
						else
							notes.get(i).getFunction().setInversion(5);
					else
						notes.get(i).getFunction().setInversion(1);
				}
			}
		}
	}

	private void setMiddleVoices(Piece piece) throws CloneNotSupportedException {
		System.out.println(notes);
		ArrayList<Note> alto = new ArrayList<Note>();
		ArrayList<Note> tenor = new ArrayList<Note>();
		ArrayList<Note> bas = new ArrayList<Note>();

		Random rand = new Random();
		for(Note n: notes){
			
			ArrayList<Integer> k = new ArrayList<Integer>();
			k.add(1);k.add(3);k.add(5);
			if(n.getFunction().isTriad()){
				if(n.getFunction().getInversion()!=3)
					k.add(rand.nextInt(2)*4+1);
				else
					k.add(1);
			}else{
				k.add(7);
			}
			

			k.remove(k.indexOf(n.getFunction().getInversion()));
			if(n.getFunction().getInversion()!=n.getFunction().getPosition())
				k.remove(k.indexOf(n.getFunction().getPosition()));

			int a = k.get(rand.nextInt(2));
			k.remove(k.indexOf(a));
			int t = k.get(0);

				
				Note aNote = n.clone();
				if((a-1)/2 < n.getFunction().getNotes().length)
					aNote.setPitch(n.getFunction().getNotes()[(a-1)/2].getPitch());
				else
					aNote.setPitch(n.getFunction().getNotes()[0].getPitch());
				alto.add(aNote);
					
				Note tNote = n.clone();
				if((t-1)/2 < n.getFunction().getNotes().length)
					tNote.setPitch(n.getFunction().getNotes()[(t-1)/2].getPitch());
				else
					tNote.setPitch(n.getFunction().getNotes()[0].getPitch());
				tenor.add(tNote); 
				
				Note bNote = n.clone();
					bNote.setPitch(n.getFunction().getNotes()[(n.getFunction().getInversion()-1)/2].getPitch());
				bas.add(bNote);
				
		}
		
		
		
		
		for(int i=notes.size()-2;i>=0;i--){
			Metre pieceMetre = piece.getMetre();
			alto.get(i).setRythmicValue((double)pieceMetre.getBase()/(notes.get(i+1).getAddress()-notes.get(i).getAddress()));
			tenor.get(i).setRythmicValue(pieceMetre.getBase()/(notes.get(i+1).getAddress()-notes.get(i).getAddress()));
			bas.get(i).setRythmicValue(pieceMetre.getBase()/(notes.get(i+1).getAddress()-notes.get(i).getAddress()));
		}
		System.out.println(alto);
		System.out.println(tenor);
		System.out.println(bas);
		Melody altoMelody, tenorMelody, basMelody ;
		
		if(piece.getMelody().getBars().get(0).getMetre().equals(piece.getMetre())){
			altoMelody = new Melody(piece.getKey(),piece.getMetre(),piece.getTempo());
			
			tenorMelody = new Melody(piece.getKey(),piece.getMetre(),piece.getTempo());
			
			basMelody = new Melody(piece.getKey(),piece.getMetre(),piece.getTempo());
				
		}else{
			altoMelody = new Melody(piece.getKey(),piece.getMetre(),piece.getTempo(),
					new Bar(piece.getMelody().getBars().get(0).getKey(),
							piece.getMelody().getBars().get(0).getMetre()));
			altoMelody.getBars().add(new Bar(piece.getKey(),piece.getMetre()));	
			tenorMelody = new Melody(piece.getKey(),piece.getMetre(),piece.getTempo(),
					new Bar(piece.getMelody().getBars().get(0).getKey(),
							piece.getMelody().getBars().get(0).getMetre()));
			tenorMelody.getBars().add(new Bar(piece.getKey(),piece.getMetre()));
			basMelody = new Melody(piece.getKey(),piece.getMetre(),piece.getTempo(),
					new Bar(piece.getMelody().getBars().get(0).getKey(),
							piece.getMelody().getBars().get(0).getMetre()));
			basMelody.getBars().add(new Bar(piece.getKey(),piece.getMetre()));	
		}
		
		System.out.println("Alt");
		altoMelody.printMelody();
		System.out.println("Tenor");
		tenorMelody.printMelody();
		System.out.println(notes.size());
		System.out.println(alto.size());
		System.out.println(tenor.size());

		
		for(Note a: alto){
			altoMelody.addNote(a);
		}
		for(Note t: tenor){
			tenorMelody.addNote(t);
		}
		for(Note b: bas){
			basMelody.addNote(b);
		}
		
		piece.getAccompaniment()[0] = altoMelody;
		piece.getAccompaniment()[1] = tenorMelody;
		piece.getAccompaniment()[2] = basMelody;
		
		
		System.out.println("Sopran");
		piece.getMelody().printMelody();
		System.out.println("Alt");
		piece.getAccompaniment()[0].printMelody();
		System.out.println("Tenor");
		piece.getAccompaniment()[1].printMelody();
		System.out.println("Bas");
		piece.getAccompaniment()[2].printMelody();
	
		
	}

	private void setSecondaryChords() {
		Random rand = new Random();
		int numberOfattemps = rand.nextInt(notes.size());
		for (int i = 0; i < numberOfattemps; i++) {
			int noteToReplaceIndex = rand.nextInt(notes.size() - 1);
			replaceWithSecondaryChord(noteToReplaceIndex);
		}

	}

	private void replaceWithSecondaryChord(int noteToReplaceIndex) {
		ArrayList<Chord> possibleChordsToReplace = new ArrayList<Chord>();
		if ((notes.get(noteToReplaceIndex).getFunction() != null)
				&& (!notes.get(noteToReplaceIndex).getFunction().isPac())) {
			for (Chord ch : notes.get(noteToReplaceIndex).getPossibleChords()) {
				if (ch.getPriority() != 0)
					possibleChordsToReplace.add(ch);
			}
			Random rand = new Random();
			if (possibleChordsToReplace.size() > 0) {
				int choice = rand.nextInt(possibleChordsToReplace.size());
				notes.get(noteToReplaceIndex).setFunction(possibleChordsToReplace.get(choice));
			}
		}

	}

	private void setConnections() {
		System.out.println(notes);
		for (int i = notes.size() - 1; i >= 1; i--) {
			setRelatedConnections(notes.get(i - 1), notes.get(i));
			
		}
		for (int i = notes.size() - 1; i >= 1; i--) {
			setUnrelatedConnections(notes.get(i - 1), notes.get(i));

		}
	}

	private void setRelatedConnections(Note note1, Note note2) {

		Chord chord1 = note1.getFunction(), chord2 = note2.getFunction();
		if (chord2 == null) {
			if (chord1 == null) {
				ArrayList<Chord[]> possibleConnections = new ArrayList<Chord[]>();
				for (Chord ch1 : note1.getPossibleChords()) {
					for (Chord ch2 : note2.getPossibleChords()) {
						if ((ch1.getName().equals(ChordName.D) || ch1.getName().equals(ChordName.D7)
								|| ch1.getName().equals(ChordName.d))
								&& (ch2.getName().equals(ChordName.T) || ch2.getName().equals(ChordName.t))) {
							possibleConnections.add(new Chord[] { ch1, ch2 });
						}
						if ((ch2.getName().equals(ChordName.D) || ch2.getName().equals(ChordName.D7)
								|| ch2.getName().equals(ChordName.d))
								&& (ch1.getName().equals(ChordName.T) || ch1.getName().equals(ChordName.t))) {
							possibleConnections.add(new Chord[] { ch1, ch2 });
						}
						if ((ch1.getName().equals(ChordName.S) || ch1.getName().equals(ChordName.s))
								&& (ch2.getName().equals(ChordName.T) || ch2.getName().equals(ChordName.t))) {
							possibleConnections.add(new Chord[] { ch1, ch2 });
						}
						if ((ch1.getName().equals(ChordName.T) || ch1.getName().equals(ChordName.t))
								&& (ch2.getName().equals(ChordName.S) || ch2.getName().equals(ChordName.s))) {
							possibleConnections.add(new Chord[] { ch1, ch2 });
						}
					}
				}
				if (!possibleConnections.isEmpty()) {
					Random rand = new Random();
					int finalChoice = rand.nextInt(possibleConnections.size());
					note1.setFunction(possibleConnections.get(finalChoice)[0]);
					note2.setFunction(possibleConnections.get(finalChoice)[1]);
				}
				// [ ][ ]
			} else {
				ArrayList<Chord> possibleConnections = new ArrayList<Chord>();
				// [x][ ]
				for (Chord ch2 : note2.getPossibleChords()) {
					if ((chord1.getName().equals(ChordName.D) || chord1.getName().equals(ChordName.D7)
							|| chord1.getName().equals(ChordName.d))
							&& (ch2.getName().equals(ChordName.T) || ch2.getName().equals(ChordName.t))) {
						possibleConnections.add(ch2);
					}
					if ((ch2.getName().equals(ChordName.D) || ch2.getName().equals(ChordName.D7)
							|| ch2.getName().equals(ChordName.d))
							&& (chord1.getName().equals(ChordName.T) || chord1.getName().equals(ChordName.t))) {
						possibleConnections.add(ch2);
					}
					if ((chord1.getName().equals(ChordName.S) || chord1.getName().equals(ChordName.s))
							&& (ch2.getName().equals(ChordName.T) || ch2.getName().equals(ChordName.t))) {
						possibleConnections.add(ch2);
					}
					if ((chord1.getName().equals(ChordName.T) || chord1.getName().equals(ChordName.t))
							&& (ch2.getName().equals(ChordName.S) || ch2.getName().equals(ChordName.s))) {
						possibleConnections.add(ch2);
					}
				}
				if (!possibleConnections.isEmpty()) {
					Random rand = new Random();
					int finalChoice = rand.nextInt(possibleConnections.size());
					note2.setFunction(possibleConnections.get(finalChoice));
				}
			}
		} else {
			if (chord1 == null) {
				ArrayList<Chord> possibleConnections = new ArrayList<Chord>();
				// [ ][x]
				for (Chord ch1 : note2.getPossibleChords()) {
					if ((ch1.getName().equals(ChordName.D) || ch1.getName().equals(ChordName.D7)
							|| ch1.getName().equals(ChordName.d))
							&& (chord2.getName().equals(ChordName.T) || chord2.getName().equals(ChordName.t))) {
						possibleConnections.add(ch1);
					}
					if ((chord2.getName().equals(ChordName.D) || chord2.getName().equals(ChordName.D7)
							|| chord2.getName().equals(ChordName.d))
							&& (ch1.getName().equals(ChordName.T) || ch1.getName().equals(ChordName.t))) {
						possibleConnections.add(ch1);
					}
					if ((ch1.getName().equals(ChordName.S) || ch1.getName().equals(ChordName.s))
							&& (chord2.getName().equals(ChordName.T) || chord2.getName().equals(ChordName.t))) {
						possibleConnections.add(ch1);
					}
					if ((ch1.getName().equals(ChordName.T) || ch1.getName().equals(ChordName.t))
							&& (chord2.getName().equals(ChordName.S) || chord2.getName().equals(ChordName.s))) {
						possibleConnections.add(ch1);
					}
				}
				if (!possibleConnections.isEmpty()) {
					Random rand = new Random();
					int finalChoice = rand.nextInt(possibleConnections.size());
					note1.setFunction(possibleConnections.get(finalChoice));
				}
			}
		}
	}

	private void setUnrelatedConnections(Note note1, Note note2) {

		Chord chord1 = note1.getFunction(), chord2 = note2.getFunction();
		if (chord2 == null) {
			if (chord1 == null) {
				ArrayList<Chord[]> possibleConnections = new ArrayList<Chord[]>();
				for (Chord ch1 : note1.getPossibleChords()) {
					for (Chord ch2 : note2.getPossibleChords()) {
						if ((ch1.getName().equals(ChordName.S) || ch1.getName().equals(ChordName.s))
								&& (ch2.getName().equals(ChordName.D) || ch2.getName().equals(ChordName.D7)
										|| ch2.getName().equals(ChordName.d))) {
							possibleConnections.add(new Chord[] { ch1, ch2 });
						}
					}
				}
				if (!possibleConnections.isEmpty()) {
					Random rand = new Random();
					int finalChoice = rand.nextInt(possibleConnections.size());
					note1.setFunction(possibleConnections.get(finalChoice)[0]);
					note2.setFunction(possibleConnections.get(finalChoice)[1]);
				}
				// [ ][ ]
			} else {
				ArrayList<Chord> possibleConnections = new ArrayList<Chord>();
				// [x][ ]
				for (Chord ch2 : note2.getPossibleChords()) {
					if ((chord1.getName().equals(ChordName.S) || chord1.getName().equals(ChordName.s))
							&& (ch2.getName().equals(ChordName.D) || ch2.getName().equals(ChordName.D7)
									|| ch2.getName().equals(ChordName.d))) {
						possibleConnections.add(ch2);
					}
				}
				if (!possibleConnections.isEmpty()) {
					Random rand = new Random();
					int finalChoice = rand.nextInt(possibleConnections.size());
					note2.setFunction(possibleConnections.get(finalChoice));
				}
			}
		} else {
			if (chord1 == null) {
				ArrayList<Chord> possibleConnections = new ArrayList<Chord>();
				// [ ][x]
				for (Chord ch1 : note2.getPossibleChords()) {
					if ((ch1.getName().equals(ChordName.S) || ch1.getName().equals(ChordName.s))
							&& (chord2.getName().equals(ChordName.D) || chord2.getName().equals(ChordName.D7)
									|| chord2.getName().equals(ChordName.d))) {
						possibleConnections.add(ch1);
					}
				}
				if (!possibleConnections.isEmpty()) {
					Random rand = new Random();
					int finalChoice = rand.nextInt(possibleConnections.size());
					note1.setFunction(possibleConnections.get(finalChoice));
				}
			}
		}

	}

	private void setCadences() {
		for (Chord ch : notes.get(notes.size() - 1).getPossibleChords()) {
			if (ch.getName().equals(ChordName.T) || ch.getName().equals(ChordName.t)) {
				notes.get(notes.size() - 1).setFunction(ch);
			}
		}
		// wielka doskonala Perfect authentic cadence
		setPAC();
		// wielka niedoskonala Imperfect authentic cadence
		setIAC();
		// doskonala authentic cadence
		setAC();
		// plagalna plagal candence
		setPlagal();

	}

	private void setPlagal() {
		for (int i = 1; i < notes.size(); i++) {
			if ((notes.get(i).getPriority() <= notes.get(i - 1).getPriority()) && notes.get(i).getFunction() == null) {
				
				for (Chord ch : notes.get(i).getPossibleChords()) {
					if ((ch.getName().equals(ChordName.T) || ch.getName().equals(ChordName.t))) {
						for (Chord ch1 : notes.get(i - 1).getPossibleChords()) {
							if (ch1.getName().equals(ChordName.S) || ch1.getName().equals(ChordName.s)) {
								notes.get(i).setFunction(ch);
								notes.get(i - 1).setFunction(ch1);
							}
						}
					}
				}
			}
		}
	}

	private void setAC() {
		for (int i = 1; i < notes.size(); i++) {
			if ((notes.get(i).getPriority() <= notes.get(i - 1).getPriority()) && notes.get(i).getFunction() == null) {
				for (Chord ch : notes.get(i).getPossibleChords()) {
					if ((ch.getName().equals(ChordName.T) || ch.getName().equals(ChordName.t))) {
						for (Chord ch1 : notes.get(i - 1).getPossibleChords()) {
							if ((ch1.getName().equals(ChordName.D) || ch1.getName().equals(ChordName.D7)
									|| (ch1.getName().equals(ChordName.d) && ch1.getPosition() == 3))
									&& notes.get(i - 1).getFunction() == null) {
								notes.get(i).setFunction(ch);
								notes.get(i - 1).setFunction(ch1);
							}
						}
					}
				}
			}
		}
	}

	private void setIAC() {
		for (int i = 2; i < notes.size(); i++) {
			if ((notes.get(i).getPriority() <= notes.get(i - 1).getPriority()) && notes.get(i).getFunction() == null) {
				for (Chord ch : notes.get(i).getPossibleChords()) {
					if ((ch.getName().equals(ChordName.T) || ch.getName().equals(ChordName.t))) {
						for (Chord ch1 : notes.get(i - 1).getPossibleChords()) {

							if ((ch1.getName().equals(ChordName.D) || ch1.getName().equals(ChordName.D7)
									|| (ch1.getName().equals(ChordName.d) && ch1.getPosition() == 3))
									&& notes.get(i - 1).getFunction() == null) {
								for (Chord ch2 : notes.get(i - 2).getPossibleChords()) {
									if ((ch2.getName().equals(ChordName.S) || ch2.getName().equals(ChordName.s))
											&& notes.get(i - 2).getFunction() == null) {
										notes.get(i).setFunction(ch);
										notes.get(i - 1).setFunction(ch1);
										notes.get(i - 2).setFunction(ch2);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private void setPAC() {
		for (int i = notes.size() - 1; i >= 2; i--) {
			if (notes.get(i).getPriority() == 0) {
				for (Chord ch : notes.get(i).getPossibleChords()) {
					if ((ch.getName().equals(ChordName.T) || ch.getName().equals(ChordName.t))
							&& ch.getPosition() == 1) {
						System.out.println("2nd done");
						for (Chord ch1 : notes.get(i - 1).getPossibleChords()) {
							if (ch1.getName().equals(ChordName.D) || ch1.getName().equals(ChordName.D7)
									|| (ch1.getName().equals(ChordName.d) && ch1.getPosition() == 3)) {
								for (Chord ch2 : notes.get(i - 2).getPossibleChords()) {
									if (ch2.getName().equals(ChordName.S) || ch2.getName().equals(ChordName.s)) {
										ch.setPac();
										ch1.setPac();
										ch2.setPac();
										notes.get(i).setFunction(ch);
										notes.get(i - 1).setFunction(ch1);
										notes.get(i - 2).setFunction(ch2);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private void getHarmonicNotes(Piece piece, int depth) {
		for (Bar b : piece.getMelody().getBars()) {
			for (Note n : b.getNotes()) {
				if (n.getPriority() < depth) {
					notes.add(n);
				}
			}
		}

	}

	private void setNotesPriority(Piece piece) {

		int x = 0;
		if (piece.getMelody().getBars().get(0).getMetre() != piece.getMetre()) {
			x = 1;
		}
		for (int i = x; i < piece.getMelody().getBars().size(); i++) {
			double lenght = piece.getMelody().getBars().get(i).getLength();
			ArrayList<Double> adress = new ArrayList<Double>();



			piece.getMelody().getBars().get(i).getNotes().get(0).setPriority(0);
			adress.add(0.0);
			int priorityRate = 1;

			if (lenght % 3 == 0 && lenght % 4 != 0) {
				adress.add(2 * (lenght / 3));
				for (Note n : piece.getMelody().getBars().get(i).getNotes()) {
					if (n.getAddress() - i * lenght == 2 * (lenght / 3)) {
						n.setPriority(priorityRate);
					}
				}
				priorityRate++;
			}

			if (lenght % 4 == 0) {
				adress.add(lenght / 2);
				for (Note n : piece.getMelody().getBars().get(i).getNotes()) {
					if (n.getAddress() - i * lenght == lenght / 2) {
						n.setPriority(priorityRate);
					}
				}
				priorityRate++;
			}
			if (lenght % 5 == 0) {
				adress.add(3 * (lenght / 5));
				for (Note n : piece.getMelody().getBars().get(i).getNotes()) {
					if (n.getAddress() - i * lenght == 3 * (lenght / 5)) {
						n.setPriority(priorityRate);
					}
				}
				priorityRate++;
			}
			if (lenght % 7 == 0) {
				adress.add(4 * (lenght / 7));
				for (Note n : piece.getMelody().getBars().get(i).getNotes()) {
					if (n.getAddress() - i * lenght == 4 * (lenght / 7)) {
						n.setPriority(priorityRate);
					}
				}
				priorityRate++;
			}
			if (lenght % 9 == 0) {

				adress.add((lenght / 3));
				for (Note n : piece.getMelody().getBars().get(i).getNotes()) {
					if (n.getAddress() - i * lenght == lenght / 3) {
						n.setPriority(priorityRate);
					}
				}
				priorityRate++;
			}

			for (int j = 0; j < lenght; j++) {
				if (!adress.contains(j)) {
					adress.add((double) j);
				}

				for (Note n : piece.getMelody().getBars().get(i).getNotes()) {
					if (n.getAddress() == j + i * lenght && n.getPriority() == 10) {
						n.setPriority(priorityRate);
					}
				}
			}
			priorityRate++;

			int division = 0;
			System.out.println((!piece.getMelody().getBars().get(i).doNotesHavePriority()));
			System.out.println(priorityRate);
			while ((!piece.getMelody().getBars().get(i).doNotesHavePriority()) && priorityRate <= 9) {
				division++;
				for (Double a : adress) {
					for (Note n : piece.getMelody().getBars().get(i).getNotes()) {
						if (n.getAddress() - i * lenght == a + (1 / Math.pow(2, division))) {
							n.setPriority(priorityRate);
						}
					}
				}
				priorityRate++;

			}

		}
		piece.getMelody().printMelody();
	}

	public void implementChords() {
		
		Note[] triad1 = { new Note(1), new Note(3), new Note(5) };
		Note[] triad1s = { new Note(1), new Note(3, "#"), new Note(5) };
		Note[] triad1b = { new Note(1), new Note(3, "b"), new Note(5) };
		Note[] triad2 = { new Note(2), new Note(4), new Note(6) };
		Note[] triad2s = { new Note(2), new Note(4, "#"), new Note(6) };
		Note[] triad3 = { new Note(3), new Note(5), new Note(7) };
		Note[] triad3s = { new Note(3), new Note(5, "#"), new Note(7) };
		Note[] triad3b = { new Note(3), new Note(5, "b"), new Note(7) };
		Note[] triad4 = { new Note(4), new Note(6), new Note(1) };
		Note[] triad4s = { new Note(4), new Note(6, "#"), new Note(1) };
		Note[] triad4b = { new Note(4), new Note(6, "b"), new Note(1) };
		Note[] triad5 = { new Note(5), new Note(7), new Note(2) };
		Note[] triad5s = { new Note(5), new Note(7, "#"), new Note(2) };
		Note[] triad5b = { new Note(5), new Note(7, "b"), new Note(2) };
		Note[] tetrad5 = { new Note(5), new Note(7), new Note(2), new Note(4) };
		Note[] tetrad5m = { new Note(5), new Note(7, "#"), new Note(2), new Note(4) };
		Note[] triad6 = { new Note(6), new Note(1), new Note(3) };
		Note[] triad6s = { new Note(6), new Note(1, "#"), new Note(3) };
		Note[] triad6b = { new Note(6), new Note(1, "b"), new Note(3) };
		Note[] triad7 = { new Note(7), new Note(2), new Note(4) };
		Note[] triad7s = { new Note(7), new Note(2, "#"), new Note(4) };
		Note[] triad7b = { new Note(7), new Note(2, "b"), new Note(4) };
		Note[] triadNeap1 = { new Note(1, "#"), new Note(4), new Note(5, "#") };
		Note[] triadNeap2 = { new Note(2, "b"), new Note(4), new Note(6, "b") };
		Note[] tetradDiminished = { new Note(1, "#"), new Note(4), new Note(5, "#") };

		// three-chord song: Tonic, tonic, Subdominant, subdominant, Dominant
		chordsForMajor.add(new Chord(ChordName.T, true, triad1, ScaleName.major, 0));
		chordsForMajor.add(new Chord(ChordName.t, true, triad1b, ScaleName.minor, 3));
		chordsForMinor.add(new Chord(ChordName.t, true, triad1, ScaleName.minor, 0));
		chordsForMinor.add(new Chord(ChordName.T, true, triad1s, ScaleName.minor, 3));
		chordsForMajor.add(new Chord(ChordName.S, true, triad4, ScaleName.major, 0));
		chordsForMajor.add(new Chord(ChordName.s, true, triad4b, ScaleName.major, 3));
		chordsForMinor.add(new Chord(ChordName.s, true, triad4, ScaleName.minor, 0));
		chordsForMinor.add(new Chord(ChordName.S, true, triad4s, ScaleName.minor, 3));
		chordsForMajor.add(new Chord(ChordName.D, true, triad5, ScaleName.major, 0));
		chordsForMajor.add(new Chord(ChordName.d, true, triad5b, ScaleName.major, 3));

		chordsForMinor.add(new Chord(ChordName.D, true, triad5s, ScaleName.major, 0));
		chordsForMinor.add(new Chord(ChordName.d, true, triad5, ScaleName.minor, 1));
		// from major Tonic
		chordsForMajor.add(new Chord(ChordName.tiii, true, triad3, ScaleName.minor, 1));
		chordsForMajor.add(new Chord(ChordName.tvi, true, triad6, ScaleName.minor, 1));
		chordsForMajor.add(new Chord(ChordName.Tiii, true, triad3s, ScaleName.minor, 3));
		chordsForMajor.add(new Chord(ChordName.Tvi, true, triad6s, ScaleName.minor, 3));
		// from minor Tonic
		chordsForMinor.add(new Chord(ChordName.Tiii, true, triad3, ScaleName.major, 1));
		chordsForMinor.add(new Chord(ChordName.Tvi, true, triad6, ScaleName.major, 1));
		chordsForMinor.add(new Chord(ChordName.tiii, true, triad3b, ScaleName.major, 3));
		chordsForMinor.add(new Chord(ChordName.tvi, true, triad6b, ScaleName.major, 3));
		// from major Subdominant
		chordsForMajor.add(new Chord(ChordName.sii, true, triad2, ScaleName.minor, 1));
		chordsForMajor.add(new Chord(ChordName.svi, true, triad6, ScaleName.minor, 1));
		chordsForMajor.add(new Chord(ChordName.Sii, true, triad2s, ScaleName.minor, 3));
		chordsForMajor.add(new Chord(ChordName.Svi, true, triad6s, ScaleName.minor, 3));
		// from minor Subdominant
		chordsForMinor.add(new Chord(ChordName.sii, true, triad2, ScaleName.diminished, 2));
		chordsForMinor.add(new Chord(ChordName.Svi, true, triad6, ScaleName.major, 1));
		chordsForMinor.add(new Chord(ChordName.Sii, true, triad2s, ScaleName.diminished, 3));
		chordsForMinor.add(new Chord(ChordName.svi, true, triad6b, ScaleName.major, 3));
		// from major Dominant
		chordsForMajor.add(new Chord(ChordName.diii, true, triad3, ScaleName.minor, 1));
		chordsForMajor.add(new Chord(ChordName.dvii, true, triad7, ScaleName.diminished, 2));
		chordsForMajor.add(new Chord(ChordName.Diii, true, triad3s, ScaleName.minor, 3));
		chordsForMajor.add(new Chord(ChordName.Dvii, true, triad7s, ScaleName.diminished, 3));
		chordsForMajor.add(new Chord(ChordName.D7, false, tetrad5, ScaleName.major, 1));
		// from minor Dominant
		chordsForMinor.add(new Chord(ChordName.Diii, true, triad3, ScaleName.major, 1));
		chordsForMinor.add(new Chord(ChordName.Dvii, true, triad7, ScaleName.major, 1));
		chordsForMinor.add(new Chord(ChordName.diii, true, triad3b, ScaleName.major, 3));
		chordsForMinor.add(new Chord(ChordName.dvii, true, triad7b, ScaleName.major, 3));
		chordsForMajor.add(new Chord(ChordName.D7, false, tetrad5m, ScaleName.major, 1));
		// others
		chordsForMinor.add(new Chord(ChordName.MinorDiminished, false, tetradDiminished, ScaleName.diminished, 2));
		chordsForMajor.add(new Chord(ChordName.Neapolitan, true, triadNeap1, ScaleName.major, 3));
		chordsForMajor.add(new Chord(ChordName.Neapolitan, true, triadNeap2, ScaleName.major, 3));
		chordsForMinor.add(new Chord(ChordName.Neapolitan, true, triadNeap1, ScaleName.major, 3));
		chordsForMinor.add(new Chord(ChordName.Neapolitan, true, triadNeap2, ScaleName.major, 3));
	}

	public void setPossibleChordsForNotes(ArrayList<Note> harmonicNotes, Piece piece)
			throws CloneNotSupportedException {
		for (Note n : harmonicNotes) {
			if (getScale(piece.getKey()) == ScaleName.major) {
				for (Chord ch : chordsForMajor) {

					for (int i = 0; i < ch.getNotes().length; i++) {
						if (ch.getNotes()[i].getPitch() == n.getPitch()
								&& ch.getNotes()[i].isModulation() == n.isModulation()) {
							if (ch.getPriority() < 3 || (i == 1 && ch.getName() != ChordName.Neapolitan))
								n.addPossibleChords(ch, i * 2 + 1);
						}
					}
				}
			} else {
				for (Chord ch : chordsForMinor) {

					for (int i = 0; i < ch.getNotes().length; i++) {
						if (ch.getNotes()[i].getPitch() == n.getPitch()
								&& ch.getNotes()[i].isModulation() == n.isModulation()) {
							if (ch.getPriority() < 3 || (i == 1 && !ch.getName().equals(ChordName.Neapolitan)))
								n.addPossibleChords(ch, i * 2 + 1);
						}
					}
				}
			}
			Collections.sort(n.getPossibleChords());

		}
	}

	public ScaleName getScale(Key k) {
		if (k == Key.C || k == Key.C || k == Key.G || k == Key.F || k == Key.D || k == Key.Bflat || k == Key.A
				|| k == Key.Eflat || k == Key.E || k == Key.Aflat || k == Key.B || k == Key.Dflat || k == Key.Fsharp) {
			return ScaleName.major;
		} else
			return ScaleName.minor;
	}
}
