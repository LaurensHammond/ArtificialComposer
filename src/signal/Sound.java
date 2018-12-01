package signal;

public class Sound {
	private int pitch;
	private int duration;
	
	public Sound(int pitch, int duration) {
		super();
		this.pitch = pitch;
		this.duration = duration;
	}

	@Override
	public String toString() {
		return "Sound [pitch=" + pitch + ", duration=" + duration + "]";
	}

	public int getPitch() {
		return pitch;
	}

	public void setPitch(int pitch) {
		this.pitch = pitch;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

}
