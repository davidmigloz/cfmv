package clustering;

import java.util.ArrayList;
import java.util.List;

import data.DataSet;
import data.Point;

public class Cluster {
	DataSet ds;
	/** Points belonging to the cluster */
	private List<Point> points;
	/** Centroid point of the cluster */
	private Point centroid;
	/** Processing status */
	private boolean completed;

	public Cluster(DataSet ds, Point centroid) {
		this.ds = ds;
		this.centroid = centroid;
		completed = false;
		points = new ArrayList<Point>();
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
	 * @return true is the cluster is fully processed
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
	 * Remove all the points the cluster has.
	 */
	public void cleanPoints() {
		points.clear();
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
