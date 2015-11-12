package cfmv;

import java.util.Arrays;

public class Point {
	private long id;
	private int[] features;

	public Point(long id, int[] features) {
		this.id = id;
		this.features = features;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int[] getFeatures() {
		return features;
	}

	public void setFeatures(int[] features) {
		this.features = features;
	}

	@Override
	public String toString(){
		return "#" + id + ": " + Arrays.toString(features);
	}
}
