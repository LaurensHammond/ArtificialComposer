package signal;

public class Event {
	private int status;
	private long time;
	private int pitch;
	public Event(int status, long time, int pitch) {
		super();
		this.status = status;
		this.time = time;
		this.pitch = pitch;
	}
	@Override
	public String toString() {
		return "Event [status=" + status + ", time=" + time + ", pitch=" + pitch + "]\n";
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public int getPitch() {
		return pitch;
	}
	public void setPitch(int pitch) {
		this.pitch = pitch;
	}

	

}
