package music;

public class Metre {
	
	private int lenght;
	private int base;

	
	public Metre(int lenght, int base) {
		super();
		this.lenght = lenght;
		this.base = base;
	}
	
	public Metre() {
		// TODO Auto-generated constructor stub
	}

	public int getLenght() {
		return lenght;
	}
	public int getBase() {
		return base;
	}
	@Override
	public String toString() {
		return "Metre[" + lenght + "/" + base + "]";
	}
}
