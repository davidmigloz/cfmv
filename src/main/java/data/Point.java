package data;

import java.util.Arrays;

public class Point {
	/** Array with the values of the features of the point */
	private float[] features;

	public Point(float[] features) {
		this.features = features;
	}

	/**
	 * @return array with all the values of the features of the point
	 */
	public float[] getFeatures() {
		return features;
	}

	/**
	 * Set all the features
	 * 
	 * @param features
	 *            array of features
	 */
	public void setFeatures(float[] features) {
		this.features = features;
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
	 * @param ds
	 *            dataset
	 * @param destino
	 * @return Euclidian distance
	 */
	public Double euclidianDistance(DataSet ds, Point target) {
		Double d = 0D;
		for (int i = 0; i < this.nFeatures(); i++) {
			if (!ds.hasMissedValues(i)) {
				d += Math.pow(features[i] - target.getFeature(i), 2.0);
			}
		}
		return Math.sqrt(d);
	}

	/**
	 * Determines if two points are the same (with a certein tolerance).
	 * 
	 * @param ds data set
	 * @param other point to compare
	 * @return
	 */
	public boolean equals(DataSet ds, Point other) {
		for (int i = 0; i < features.length; i++) {
			if (!ds.hasMissedValues(i)) {
				if (Math.abs(features[i] - other.getFeature(i)) > 0.1) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "#P:\t" + Arrays.toString(features);
	}
}
