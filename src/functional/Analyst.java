package functional;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Stack;

import music.Bar;
import music.Melody;
import music.Metre;
import music.Note;
import music.Scale;
import signal.Sound;
import types.Key;
import types.ScaleName;

/**
 * @author kol2
 *
 */
public class Analyst {
	private static ArrayList<Sound> melodySounds = new ArrayList<Sound>();
	private static Melody melody;
	private static ArrayList<Note> notes = new ArrayList<Note>();
	private static Key key;
	private static Metre metre = new Metre();
	private static int numberOfAnacrusisNotes;
	private static int shift;
	private static ScaleName scale;
	private static int tempo = 60;

	public static Melody convertIntoMelody(ArrayList<Sound> sounds) throws CloneNotSupportedException {

		if (sounds.size() > 1) {
			melodySounds = sounds;
			key = defineKey(sounds);
			metre = definceInitialCentroids(sounds);
			createMelody(notes);
		} else {
			int[] occuredSounds = countSounds(sounds);
			key = getKey((occuredSounds[0]) % 12, 0);
			melody = new Melody(key, new Metre(1, 1), tempo);
			Note note = new Note(melodySounds.get(0).getPitch() % 12, 1);
			melody.addNote(note);
		}
		return melody;
	}

	private static void createMelody(ArrayList<Note> notes2) throws CloneNotSupportedException {
		double anacrusis = 0;
		if (numberOfAnacrusisNotes > 0) {
			ArrayList<Note> firstBarNotes = new ArrayList<Note>();
			for (int i = 0; i < numberOfAnacrusisNotes; i++) {
				anacrusis += notes.get(i).getRythmicValue();
				firstBarNotes.add(notes.get(i));
			}
			double firstTop = anacrusis / metre.getBase();
			int firstBottom = metre.getBase();
			while (firstTop % 1 != 0) {
				firstTop *= 2;
				firstBottom /= 2;
			}
			Metre firstBarMetre = new Metre((int) firstTop, firstBottom);
			Bar firstBar = new Bar(key, firstBarMetre, firstBarNotes);
			melody = new Melody(key, metre, tempo, firstBar);
		} else {
			melody = new Melody(key, metre, tempo);
		}

		for (int i = numberOfAnacrusisNotes; i < notes.size() - 1; i++) {
			melody.addNote(notes.get(i));
		}
		notes.get(notes.size() - 1)
				.setRythmicValue((melody.getMetre().getBase() / notes.get(notes.size() - 1).getRythmicValue())
						* melody.getMetre().getBase());
		System.out.println("Last notes" + notes.get(notes.size() - 2) + " " + notes.get(notes.size() - 1));
		melody.addNote(notes.get(notes.size() - 1));
	}

	

	private static Metre getMetre() {
		double melodyLenght = (notes.get(notes.size() - 1).getAddress());

		ArrayList<Integer> lengthOfBar = new ArrayList<Integer>();
		ArrayList<Integer> lengthOfAnacrusis = new ArrayList<Integer>();
		ArrayList<Double> mean = new ArrayList<Double>();
		if (notes.size() >= 6) {
			for (int a = 0; a < 5; a++) {

				double temporaryAnacrusis = 0;
				// length of anacrusis
				for (int j = 0; j < a; j++) {
					temporaryAnacrusis += 32 / notes.get(j).getRythmicValue();
				}
				if (temporaryAnacrusis < 5) {
					melodyLenght = (notes.get(notes.size() - 1).getAddress()) - temporaryAnacrusis;
					for (int i = 2; i <= melodyLenght / 3; i++) {

						if (melodyLenght % i == 0 && melodyLenght / i <= 32 && a < melodyLenght / i
								&& melodyLenght / i > 3) {

							double found = 0;
							for (int j = 0; j < i; j++) {

								double barAdress = temporaryAnacrusis + j * (melodyLenght / i);
								for (Note n : notes) {
									if (n.getAddress() == barAdress) {
										found += 1 * (32 / n.getRythmicValue());
									}
								}
							}
							if (melodyLenght / i <= 9 || (melodyLenght / i) % 2 == 0 || (melodyLenght / i) % 3 == 0
									|| (melodyLenght / i) % 5 == 0 || (melodyLenght / i) % 7 == 0) {
								lengthOfBar.add(((int) melodyLenght / i));
								lengthOfAnacrusis.add(a);
								mean.add((found / i) + i * 0.1);
							}
						}
					}
				}
			}
		} else {
			lengthOfBar.add((int) melodyLenght);
			lengthOfAnacrusis.add(0);
			mean.add(1.0);
		}
		if (mean.isEmpty()) {
			lengthOfBar.add((int) melodyLenght);
			lengthOfAnacrusis.add(0);
			mean.add(1.0);
		}
		ArrayList<Integer> max = new ArrayList<Integer>();
		max.add(0);
		//looking for the best result
		for (int i = 1; i < mean.size(); i++) {

			if (mean.get(max.get(max.size() - 1)) <= mean.get(i)) {

				if (mean.get(max.get(max.size() - 1)) < mean.get(i)) {

					max.clear();
				}
				max.add(i);
			}
		}
		Random rand = new Random();
		int finalChoice = max.get(rand.nextInt(max.size()));

		for (int i = finalChoice; i < mean.size(); i++) {
			if (mean.get(i) >= 1.0 && lengthOfAnacrusis.get(i) == lengthOfAnacrusis.get(finalChoice)
					&& lengthOfBar.get(i) % lengthOfBar.get(finalChoice) == 0
					&& (lengthOfBar.get(i) / lengthOfBar.get(finalChoice) == 2
							|| lengthOfBar.get(i) / lengthOfBar.get(finalChoice) == 3)) {
				finalChoice = i;
			}

		}

		numberOfAnacrusisNotes = lengthOfAnacrusis.get(finalChoice);
		int metreNumerator = lengthOfBar.get(finalChoice);
		int metreBase = 32;
		if (findTheShortestSound(melodySounds) >= 200) {

			if (findTheLongestNote(notes) / 2 >= 1) {
				doubleNotes();
				metreBase /= 2;
			}
		}
		if ((findTheShortestSound(melodySounds) >= 430)) {

			if (findTheLongestNote(notes) / 2 >= 1) {
				doubleNotes();
				metreBase /= 2;
			}
		}
		if (findTheShortestSound(melodySounds) > 430) {

			if (findTheLongestNote(notes) / 2 >= 1) {
				doubleNotes();
				metreBase /= 2;

			}
		}

		return new Metre(metreNumerator, metreBase);

	}

	private static void doubleNotes() {
		for (Note n : notes) {
			n.doubleNote();
		}
	}

	private static void setNoteValues(ArrayList<Sound> sounds, ArrayList<Integer> currentCentroids) {
		double[] values = new double[currentCentroids.size() + 2];
		double[][] possibleValues = new double[][] { { 1, 0 }, { 2, 0 }, { 3, 0 }, { 4, 0 }, { 6, 0 }, { 8, 0 },
				{ 12, 0 }, { 16, 0 }, { 24, 0 }, { 32, 0 } };
		values[0] = 32;
		values[1] = 16;

		for (int i = 2; i < currentCentroids.size(); i++) {
			double n = currentCentroids.get(i) / currentCentroids.get(0);

			for (int psbl = 0; psbl < possibleValues.length; psbl++) {
				possibleValues[psbl][1] = Math.abs(possibleValues[psbl][0] - n);
			}
			double min = possibleValues[0][1];
			int minIndex = 0;
			for (int psbl = 0; psbl < possibleValues.length; psbl++) {
				if (possibleValues[psbl][1] < min) {
					min = possibleValues[psbl][1];
					minIndex = psbl;
				}
			}
			values[i] = 32 / possibleValues[minIndex][0];
		}

		for (int i = 0; i < sounds.size(); i++) {
			double val = values[setNote(currentCentroids, sounds.get(i))];
			if (i == sounds.size() - 1) {
				val = 32.0;
			}
			int pitch = (sounds.get(i).getPitch() - shift) % 12;
			int oct = (sounds.get(i).getPitch() - shift) / 12;
			Note n = null;
			switch (pitch) {
			case 0:
				n = new Note(1, val, null, oct);
				break;
			case 1:
				if (scale.equals(ScaleName.minor))
					n = new Note(2, val, "b", oct);
				else
					n = new Note(1, val, "#", oct);
				break;
			case 2:
				n = new Note(2, val, null, oct);
				break;
			case 3:
				if (scale.equals(ScaleName.minor))
					n = new Note(2, val, null, oct);
				else
					n = new Note(3, val, "b", oct);
				break;
			case 4:
				if (scale.equals(ScaleName.major))
					n = new Note(3, val, null, oct);
				else
					n = new Note(3, val, "#", oct);
				break;
			case 5:
				n = new Note(4, val, null, oct);
				break;
			case 6:
				n = new Note(4, val, "#", oct);
				break;
			case 7:
				n = new Note(5, val, null, oct);
				break;
			case 8:
				if (scale.equals(ScaleName.minor))
					n = new Note(6, val, null, oct);
				else
					n = new Note(5, val, "#", oct);
				break;
			case 9:
				if (scale.equals(ScaleName.major))
					n = new Note(6, val, null, oct);
				else
					n = new Note(6, val, "#", oct);
				break;
			case 10:
				if (scale.equals(ScaleName.minor))
					n = new Note(7, val, null, oct);
				else
					n = new Note(7, val, "b", oct);
			case 11:
				if (scale.equals(ScaleName.major))
					n = new Note(7, val, null, oct);
				else
					n = new Note(7, val, "#", oct);
				break;
			default:
				n = new Note(1, val, null, oct);
			}
			System.out.println(n);
			n.setAddress(defineTemporaryAdress());
			notes.add(n);
		}
	}

	private static double defineTemporaryAdress() {
		double l = 0;
		if (notes.isEmpty()) {
			return 0.0;
		}
		for (Note n : notes) {
			l += (32 / n.getRythmicValue());
		}
		return l;
	}
	private static Metre definceInitialCentroids(ArrayList<Sound> sounds) {

		int shortest = (int) (findTheShortestSound(sounds)*1.05), longest = findTheLongestSound(sounds) + shortest;
		ArrayList<Integer> centroids = new ArrayList<Integer>();
		int x = 1;
		do {
			centroids.add((shortest * x));
			if (x < 1)
				centroids.add((int) (shortest * (x * 1.5)));
			x *= 2;
		} while (shortest * x <= longest * 2);
		return findNoteValues(sounds, centroids);
	} 

	private static Metre findNoteValues(ArrayList<Sound> sounds, ArrayList<Integer> centroids) {
		ArrayList<Integer> currentCentroids = clustering(centroids, sounds);
		tempo = 60000 / currentCentroids.get(0);
		System.out.println("Centroids" + currentCentroids);
		System.out.println("tempo" + tempo);
		setNoteValues(sounds, currentCentroids);
		return getMetre();

	} 
	private static ArrayList<Integer> clustering(ArrayList<Integer> currentCentroids, ArrayList<Sound> sounds) {
		int[][] distance = new int[sounds.size()][currentCentroids.size()];
		int[][] nearestToCentroid = new int[currentCentroids.size()][2];
		for (int i = 0; i < sounds.size() - 1; i++) {
			for (int j = 0; j < currentCentroids.size(); j++) {
				distance[i][j] = Math.abs(sounds.get(i).getDuration() - currentCentroids.get(j));
			}
			int minDistance = distance[i][0], minDistanceIndex = 0;
			for (int j = 0; j < currentCentroids.size() - 1; j++) {
				if (distance[i][j] <= minDistance) {
					minDistance = distance[i][j];
					minDistanceIndex = j;
				}
			}
			nearestToCentroid[minDistanceIndex][0] += sounds.get(i).getDuration();
			nearestToCentroid[minDistanceIndex][1]++;
		}
		int currentCentroidsSize = currentCentroids.size();
		currentCentroids.clear();
		for (int i = 0; i < currentCentroidsSize; i++) {
			if (nearestToCentroid[i][1] > 0) {
				currentCentroids.add((int) (nearestToCentroid[i][0] / nearestToCentroid[i][1]));
			}
		}
		return currentCentroids;
	}

	private static int setNote(ArrayList<Integer> currentCentroids, Sound sound) {

		int[] distance = new int[currentCentroids.size()];

		for (int i = 0; i < currentCentroids.size(); i++) {
			distance[i] = Math.abs(sound.getDuration() - currentCentroids.get(i));

		}
		int minDistance = distance[0], minDistanceIndex = 0;
		for (int i = 0; i < currentCentroids.size(); i++) {

			if (distance[i] <= minDistance) {
				minDistance = distance[i];
				minDistanceIndex = i;
			}
		}
		return minDistanceIndex;
	}

	private static int findTheShortestSound(ArrayList<Sound> sounds) {
		int s = sounds.get(0).getDuration();
		for (int i = 1; i < sounds.size() - 1; i++) {
			if (sounds.get(i).getDuration() < s) {
				s = sounds.get(i).getDuration();
			}
		}
		return (int) (s * 1.1);
	}

	static double findTheShortestNote(ArrayList<Note> n) {
		double s = n.get(0).getRythmicValue();
		for (int i = 1; i < n.size() - 1; i++) {
			if (n.get(i).getRythmicValue() > s) {
				s = n.get(i).getRythmicValue();
			}
		}
		return s;
	}

	private static int findTheLongestSound(ArrayList<Sound> sounds) {
		int s = sounds.get(0).getDuration();
		for (int i = 1; i < sounds.size() - 1; i++) {
			if (sounds.get(i).getDuration() > s) {
				s = sounds.get(i).getDuration();
			}
		}
		return s;
	}

	private static double findTheLongestNote(ArrayList<Note> n) {
		double s = n.get(0).getRythmicValue();
		for (int i = 1; i < n.size() - 1; i++) {
			if (n.get(i).getRythmicValue() < s) {
				s = n.get(i).getRythmicValue();
			}
		}
		return s;
	}

	/**
	 * The method that estimates the ratio of accordance of notes in melody with every scale
	 * @param sounds
	 * @return the most probably key of melody
	 */
	private static Key defineKey(ArrayList<Sound> sounds) {
		int[] occuredSounds = countSounds(sounds);
		ArrayList<Scale> filters = createFilters();
		int[][] fitness = new int[12][filters.size()];
		for (int shift = 0; shift < 12; shift++) {
			for (int i = 0; i < occuredSounds.length; i++) {
				for (int fltr = 0; fltr < filters.size(); fltr++) { 
					boolean occuredInFilter = false;

					for (int j = 0; j < filters.get(fltr).getStep().length; j++) {
						if ((occuredSounds[i]) != 0) {
							if (i == (filters.get(fltr).getStep()[j] + shift) % 12) {
								fitness[shift][fltr] += 2;
								occuredInFilter = true;
							}
						}
					}
					if (!occuredInFilter)
						fitness[shift][fltr]--; 

				}
			}
		}
		for (int i = 0; i < 12; i++) {
			for (int f = 0; f < filters.size(); f++) {
				fitness[i][f] = (fitness[i][f] * 50 / (filters.get(f).getStep().length));
			}

		}

		return getTheMostLikelyKey(fitness, filters, occuredSounds);

	}
 
	/**
	 * The method selecting the most probably key for melody
	 * @param fitness
	 * @param filters
	 * @param occuredSounds
	 * @return the most likely key
	 */
	private static Key getTheMostLikelyKey(int[][] fitness, ArrayList<Scale> filters, int[] occuredSounds) {

		int max = fitness[0][0];
		Stack<Integer> maxShift = new Stack<Integer>();
		Stack<Integer> maxScale = new Stack<Integer>();
		for (int i = 0; i < 12; i++) {
			for (int f = 0; f < filters.size(); f++) {
				if (fitness[i][f] >= max) {
					if (fitness[i][f] > max) {
						maxShift.clear();
						maxScale.clear();
						max = fitness[i][f];
					}
					maxShift.add(i);
					maxScale.add(f);
				}
			}
			
		}
		if(maxShift.size()>1){
		PriorityQueue<Integer> best = new PriorityQueue<Integer>();
		int[] best2 = new int[12];
		int[] points = new int[maxShift.size()];

		for (int i = 0; i < maxShift.size(); i++) {
			int maxTonic, maxDominant, maxSubdominant;
			maxTonic = occuredSounds[maxShift.get(i)] * 4;
			maxDominant = occuredSounds[(maxShift.get(i) + 7) % 12] * 2;
			maxSubdominant = occuredSounds[(maxShift.get(i) + 5) % 12] * 2;
			best.add((maxTonic + maxDominant + maxSubdominant) * 100 / melodySounds.size());
			best2[i] = (maxTonic + maxDominant + maxSubdominant) * 100 / melodySounds.size();
		}
		max = 0;
		for (int i = 0; i < maxShift.size(); i++) {
			int w = best.poll();
			for (int j = 0; j < maxShift.size(); j++) {
				if (w == best2[j]) {
					points[j] += (12 - i);
				}
			}
		}

		for (int i = 0; i < maxShift.size(); i++) {

			if (maxShift.get(i) == melodySounds.get(melodySounds.size() - 1).getPitch() % 12) {
				points[i] += 3;
			}
			if (maxScale.get(i) == 0) {
				if (occuredSounds[(4 + i) % 12] > occuredSounds[(3 + i) % 12]) {
					points[i] += 5;

				}
			}
			if (maxScale.get(i) == 1) {
				if (occuredSounds[(10 + i) % 12] > occuredSounds[(10 + i) % 12]) {
					points[i] += 3;

				}
			}
			if (maxScale.get(i) == 2) {
				if (occuredSounds[(10 + i) % 12] > occuredSounds[(11 + i) % 12]) {
					points[i] += 3;

				}
			}
			if ((maxScale.get(i) == 0) || (maxScale.get(i) == 3)) {
				if (occuredSounds[(9 + i) % 12] > occuredSounds[(8 + i) % 12]) {
					points[i] += 1;

				}
			}
		}

		Stack<Integer> finalBest = new Stack<Integer>();
		max = 0;
		for (int i = 0; i < points.length; i++) {
			if (points[i] >= max) {
				if (points[i] > max) {
					max = points[i];
					finalBest.clear();
					;
				}
				finalBest.add(i);
			}
		}

		Random rand = new Random();

		int finalChoice = finalBest.get(rand.nextInt(finalBest.size()));
		shift = maxShift.get(finalChoice);
		if (maxScale.get(finalChoice) == 0)
			scale = ScaleName.major;
		else
			scale = ScaleName.minor;
		return getKey(maxShift.get(finalChoice), maxScale.get(finalChoice));
		}else
			return getKey(maxShift.get(0), maxScale.get(0));
	}

	private static ArrayList<Scale> createFilters() {
		ArrayList<Scale> filters = new ArrayList<Scale>();
		int[] major = { 0, 2, 4, 5, 7, 9, 11 };
		int[] aeolian = { 0, 2, 3, 5, 7, 8, 10 };
		int[] harmonic = { 0, 2, 3, 5, 7, 8, 11 };
		int[] melodic = { 0, 2, 3, 5, 7, 8, 9, 10, 11 };
		filters.add(new Scale(ScaleName.major, major));
		filters.add(new Scale(ScaleName.minor, aeolian));
		filters.add(new Scale(ScaleName.minor, harmonic));
		filters.add(new Scale(ScaleName.minor, melodic));
		return filters;
	}

	private static Key getKey(Integer shift, Integer scale) {
		Key key = Key.C;
		switch (shift) {
		case 0:
			if (scale == 0)
				key = Key.C;
			else
				key = Key.C;
			break;
		case 1:
			if (scale == 0)
				key = Key.Dflat;
			else
				key = Key.csharp;
			break;
		case 2:
			if (scale == 0)
				key = Key.D;
			else
				key = Key.d;
			break;
		case 3:
			if (scale == 0)
				key = Key.Eflat;
			else
				key = Key.dsharp;
			break;
		case 4:
			if (scale == 0)
				key = Key.E;
			else
				key = Key.e;
			break;
		case 5:
			if (scale == 0)
				key = Key.F;
			else
				key = Key.f;
			break;
		case 6:
			if (scale == 0)
				key = Key.Fsharp;
			else
				key = Key.fsharp;
			break;
		case 7:
			if (scale == 0)
				key = Key.G;
			else
				key = Key.g;
			break;
		case 8:
			if (scale == 0)
				key = Key.Aflat;
			else
				key = Key.gsharp;
			break;
		case 9:
			if (scale == 0)
				key = Key.A;
			else
				key = Key.a;
			break;
		case 10:
			if (scale == 0)
				key = Key.Bflat;
			else
				key = Key.bflat;
			break;
		case 11:
			if (scale == 0)
				key = Key.B;
			else
				key = Key.b;
			break;
		default:
			key = Key.C;
		}
		return key;
	}

	private static int[] countSounds(ArrayList<Sound> sounds) {
		int[] occuredSounds = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

		for (Sound s : sounds) {
			int pitch = s.getPitch();

			switch (pitch % 12) {
			case 0:
				occuredSounds[0]++;
				break;
			case 1:
				occuredSounds[1]++;
				break;
			case 2:
				occuredSounds[2]++;
				break;
			case 3:
				occuredSounds[3]++;
				break;
			case 4:
				occuredSounds[4]++;
				break;
			case 5:
				occuredSounds[5]++;
				break;
			case 6:
				occuredSounds[6]++;
				break;
			case 7:
				occuredSounds[7]++;
				break;
			case 8:
				occuredSounds[8]++;
				break;
			case 9:
				occuredSounds[9]++;
				break;
			case 10:
				occuredSounds[10]++;
				break;
			case 11:
				occuredSounds[11]++;
				break;
			}
		}
		return occuredSounds;
	}

}