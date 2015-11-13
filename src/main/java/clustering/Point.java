package clustering;

import java.util.Arrays;

public class Point {
	/**
	 * Identifier of the point (position in the data set)
	 */
	private long id;
	/**
	 * Array with the values of the features of the point
	 */
	private float[] features;

	public Point(long id, float[] features) {
		this.id = id;
		this.features = features;
	}

	/**
	 * @return id of the point
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return array with all the values of the features of the point
	 */
	public float[] getFeatures() {
		return features;
	}

	/**
	 * @param i
	 *            position of the feature (starting from 0)
	 * @return value of the feature
	 */
	public float getFeature(int i) {
		return features[i];
	}

	/**
	 * @return number of features that the point has
	 */
	public int nFeatures() {
		return features.length;
	}

	/**
	 * Calculate the Euclidian distance from the point to another point.
	 * 
	 * @param destino
	 * @return Euclidian distance
	 */
	public Double euclidianDistance(Point target) {
		Double d = 0D;
		for (int i = 0; i < this.nFeatures(); i++) {
			d += Math.pow(features[i] - target.getFeature(i), 2);
		}
		return Math.sqrt(d);
	}

	@Override
	public String toString() {
		return "#" + id + ": " + Arrays.toString(features);
	}
}
