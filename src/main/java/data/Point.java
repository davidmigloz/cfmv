package data;

import java.util.Arrays;
import java.util.Set;

/**
 * A point is composed of one or more attributes or features. It has an array
 * with its values of each feature. It also stores information about the
 * features wich have missed values.
 */
public class Point {
	/** Array with the values of the features of the point */
	private float[] values;

	/** List of features that contain missed values */
	private Set<Integer> missedFeatures;

	public Point(float[] values) {
		this.values = values;
	}

	/**
	 * Create a new point.
	 * 
	 * @param values
	 *            values of the features
	 * @param missedFeatures
	 *            features with no value
	 */
	public Point(float[] values, Set<Integer> missedFeatures) {
		this.values = values;
		this.missedFeatures = missedFeatures;
	}

	/**
	 * Get value of a feature of the point.
	 * 
	 * @param feaure
	 *            position of the feature (starting from 0)
	 * @return value of the feature
	 */
	public float getValue(int feaure) {
		return values[feaure];
	}

	/**
	 * Get the values of all the features of the point.
	 * 
	 * @return array with all the values of the features of the point
	 */
	public float[] getValues() {
		return values;
	}

	/**
	 * Set the value of a feature.
	 * 
	 * @param f
	 *            feature
	 * @param value
	 *            value of the feature
	 */
	public void setValue(int f, float value) {
		this.values[f] = value;
	}

	/**
	 * Set the values of all features.
	 * 
	 * @param values
	 *            array of features
	 */
	public void setValues(float[] values) {
		this.values = values;
	}

	/**
	 * Get the missed features of the point.
	 * 
	 * @return a map with the missed features
	 */
	public Set<Integer> getMissedFeatures() {
		return this.missedFeatures;
	}

	/**
	 * Return if the feateure f of the point has a value or not.
	 * 
	 * @param f
	 *            feature
	 * @return true if missed value false if not
	 */
	public boolean isMissedFeature(int f) {
		return missedFeatures == null ? false : missedFeatures.contains(f);
	}

	/**
	 * Get number of features.
	 * 
	 * @return number of features that the point has
	 */
	public int nFeatures() {
		return values.length;
	}

	/**
	 * Calculate the Squared Euclidean Distance from the point to another point.
	 * It place progressively greater weight on objects that are farther apart.
	 * 
	 * @param ds
	 *            dataset
	 * @param target
	 *            point
	 * @return Squared Euclidean Distance
	 */
	public Double squaredEuclidianDistance(DataSet ds, Point target) {
		Double d = 0D;
		for (int i = 0; i < this.nFeatures(); i++) {
			if (!ds.hasMissedValues(i)) {
				d += Math.pow(values[i] - target.getValue(i), 2.0);
			}
		}
		return d;
	}

	/**
	 * Determines if two points are the same without taking into account missed
	 * values.
	 * 
	 * @param ds
	 *            data set
	 * @param other
	 *            point to compare
	 * @return true / false
	 */
	public boolean equals(DataSet ds, Point other) {
		for (int i = 0; i < values.length; i++) {
			if (!ds.hasMissedValues(i)) {
				return values[i] == other.getValue(i);
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "#P:" + Arrays.toString(values);
	}
}