package clustering;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import data.DataSet;
import data.Point;

public class Cluster {
	DataSet ds;
	/** Points belonging to the cluster */
	private List<Point> points;
	/** Centroid point of the cluster */
	private Point centroid;
	/**
	 * If the centroid is the same in two consecutive iterations of k-means the
	 * status of the cluster changes to completed.
	 */
	private boolean completed;

	/** List of features that contain missed values in the cluster */
	private Set<Integer> missedFeatures;

	public Cluster(DataSet ds, Point centroid) {
		this.ds = ds;
		points = new ArrayList<Point>();
		this.centroid = centroid;
		completed = false;
		missedFeatures = new HashSet<Integer>();
	}

	/**
	 * @return centroid point
	 */
	public Point getCentroid() {
		return centroid;
	}

	/**
	 * Set centroid of the cluster
	 * 
	 * @param centroid
	 */
	public void setCentroid(Point centroid) {
		this.centroid = centroid;
	}

	/**
	 * @return list with the points belonging to the cluster
	 */
	public List<Point> getPoints() {
		return points;
	}

	/**
	 * Add point to the cluster
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
	 * @param missedFeature
	 */
	public void addMissedFeatures(Set<Integer> missedFeatures) {
		this.missedFeatures.addAll(missedFeatures);
	}

	/**
	 * @return true is the cluster is fully processed (the centroid doesn't
	 *         change)
	 */
	public boolean isCompleted() {
		return completed;
	}

	/**
	 * Set processing status
	 * 
	 * @param status
	 */
	public void setCompleted(boolean status) {
		this.completed = status;
	}

	/**
	 * @return number of points the cluster has.
	 */
	public int nPoints() {
		return points.size();
	}

	/**
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

	/**
	 * @return sumatorium of the distance of each point of the cluster to its
	 *         centroid
	 */
	public double calculateObjetiveFunction() {
		Double value = 0D;
		for (Point p : this.getPoints()) {
			value += p.euclidianDistance(ds, this.getCentroid());
		}
		return value;
	}

	@Override
	public String toString() {
		String str = "Centroid: " + this.getCentroid().toString();
		if (this.points != null) {
			for (Point p : this.getPoints()) {
				str += "\n" + p.toString();
			}
		} else {
			str += "Empty";
		}
		return str;
	}
}
