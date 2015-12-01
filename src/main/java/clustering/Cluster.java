package clustering;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import data.DataSet;
import data.Point;

/**
 * Collection of data points that are more similar to each other than to those
 * in other clusters. The centroid is the arithmetic mean (average) position of
 * all the points in the cluster.
 */
public class Cluster {
	/** Date set of points */
	DataSet ds;
	/** Points belonging to the cluster */
	private Set<Point> points;
	/** Centroid point of the cluster */
	private Point centroid;
	/**
	 * If the centroid is the same in two consecutive iterations of k-means the
	 * status of the cluster changes to completed.
	 */
	private boolean sameCentroid;

	/** List of features that contain missed values in the cluster */
	private Set<Integer> missedFeatures;

	/**
	 * Create a new cluster.
	 * 
	 * @param ds
	 *            data set
	 * @param centroid
	 *            centroid point
	 */
	public Cluster(DataSet ds, Point centroid) {
		this.ds = ds;
		points = new HashSet<Point>();
		this.centroid = centroid;
		sameCentroid = false;
		missedFeatures = new HashSet<Integer>();
	}

	/**
	 * Get centroid of the cluster.
	 * 
	 * @return centroid point
	 */
	public Point getCentroid() {
		return centroid;
	}

	/**
	 * Set centroid of the cluster.
	 * 
	 * @param centroid
	 *            centroid point of the cluster
	 */
	public void setCentroid(Point centroid) {
		this.centroid = centroid;
	}

	/**
	 * Get points of the cluster.
	 * 
	 * @return list with the points belonging to the cluster
	 */
	public Set<Point> getPoints() {
		return points;
	}

	/**
	 * Add point to the cluster.
	 * 
	 * @param p
	 *            point
	 */
	public void addPoint(Point p) {
		this.points.add(p);
	}

	/**
	 * Register a feature as having misssed valures.
	 * 
	 * @param missedFeatures
	 *            set with the features with missed values
	 */
	public void addMissedFeatures(Set<Integer> missedFeatures) {
		if (missedFeatures != null) {
			this.missedFeatures.addAll(missedFeatures);
		}
	}

	/**
	 * Get features with missed values of the cluster.
	 * 
	 * @return set with the features that have missed values
	 */
	public Set<Integer> getMissedFeatures() {
		return this.missedFeatures;
	}

	/**
	 * Answer if the cluster has missed values or not.
	 * 
	 * @return true if the cluster contains some feature with missed values
	 */
	public boolean hasMissedFeatures() {
		return !this.missedFeatures.isEmpty();
	}

	/**
	 * If the cluster is finnished for the k-means algorithm.
	 * 
	 * @return true if the cluster centroid doesn't change it two consecutive
	 *         iterations.
	 */
	public boolean isFinished() {
		return sameCentroid;
	}

	/**
	 * Set processing status.
	 * 
	 * @param status
	 *            true if finished
	 */
	public void setFinished(boolean status) {
		this.sameCentroid = status;
	}

	/**
	 * Number of points.
	 * 
	 * @return number of points the cluster has.
	 */
	public int nPoints() {
		return points.size();
	}

	/**
	 * Answer if the cluster has no points.
	 * 
	 * @return true if the cluster doesn't have any point
	 */
	public boolean isEmpty() {
		return points.isEmpty();
	}

	/**
	 * Remove all the points and missed features the cluster has.
	 */
	public void cleanPoints() {
		points.clear();
		missedFeatures.clear();
	}

	@Override
	public String toString() {
		String str = "Centroid: " + this.getCentroid().toString();
		if (this.points != null && !this.points.isEmpty()) {
			Iterator<Point> it = this.getPoints().iterator();
			for (int i = 0; i < 10; i++) { // Show 10 first point
				if (it.hasNext()) {
					str += "\n" + it.next().toString();
				}
			}
			str += "\n...";
		} else {
			str += " Empty";
		}
		return str;
	}
}