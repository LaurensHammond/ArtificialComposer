package music;


import types.Key;

public class Piece {
	private String title;
	private String author;
	private Key key;
	private Metre metre;
	private int tempo;
	private Melody melody;
	private Melody[] accompaniment = new Melody[3];
	
	public Piece(Melody melody, String author, String title) {
		super();
		this.title = title;
		this.author = author;
		this.melody = melody;
		this.key = melody.getKey();
		this.metre = melody.getMetre();
		this.tempo = melody.getTempo();
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
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

	public Melody getMelody() {
		return melody;
	}

	public void setMelody(Melody melody) {
		this.melody = melody;
	}

	public Melody[] getAccompaniment() {
		return accompaniment;
	}

	public void setAccompaniment(Melody[] accompaniment) {
		this.accompaniment = accompaniment;
	}
}
