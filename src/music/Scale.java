package music;

import java.util.Arrays;

import types.ScaleName;

public class Scale {
	private ScaleName name;
	int[] step;
	public Scale(ScaleName name, int[] step) {
		super();
		this.name = name;
		this.step = step;
	}
	public ScaleName getName() {
		return name;
	}
	public void setName(ScaleName name) {
		this.name = name;
	}
	public int[] getStep() {
		return step;
	}
	public void setStep(int[] step) {
		this.step = step;
	}
	@Override
	public String toString() {
		return "Scale [name=" + name + ", step=" + Arrays.toString(step) + "]";
	}
	
}
