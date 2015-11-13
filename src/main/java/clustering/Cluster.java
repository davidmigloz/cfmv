package clustering;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
	/**
	 * Points belonging to the cluster
	 */
	private List<Point> points;
	/**
	 * Centroid point of the cluster
	 */
	private Point centroid;
	/**
	 * Processing status
	 */
	private boolean completed;

	public Cluster(Point centroid) {
		this.centroid = centroid;
		completed = false;
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
		if (this.points == null) {
			points = new ArrayList<Point>();
		}
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
		if (this.points != null) {
			points.clear();
		}
	}

	/**
	 * @return sumatorium of the distance of each point of the cluster to its
	 *         centroid
	 */
	public double calculateObjetiveFunction() {
		Double value = 0D;
		for (Point p : this.getPoints()) {
			value += p.euclidianDistance(this.getCentroid());
		}
		return value;
	}

	@Override
	public String toString() {
		String str = "Centroid: " + this.getCentroid() + "\n";
		for (Point p : this.getPoints()) {
			str += p.toString() + "\n";
		}
		return str;
	}
}
