package kmeans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import clustering.Cluster;
import clustering.Point;

/**
 * K-means algorithm.
 */
public class KMeans {
	int k;
	List<Point> points;
	int nFeatures;

	public KMeans(List<Point> points) {
		this.points = points;
		nFeatures = points.get(0).nFeatures();
	}

	/**
	 * Execute k-means algorithm with the data set settled.
	 * 
	 * @param k
	 *            number of clusters to build
	 * @return list of final clusters
	 */
	public List<Cluster> run(int k) {
		this.k = k;
		List<Cluster> clusters = chooseCentroids();

		while (!isFinished(clusters)) {
			cleanClusters(clusters);
			assignPoints(clusters);
			recalculateCentroids(clusters);
		}
		return clusters;
	}

	/**
	 * Choose randoms centroids for the k clustes between the min. and max.
	 * value of each feature.
	 * 
	 * @return k clusters each one with the centroid setted
	 */
	private List<Cluster> chooseCentroids() {
		List<Cluster> centroids = new ArrayList<Cluster>();

		List<Float> highest = new ArrayList<Float>();
		List<Float> lowests = new ArrayList<Float>();

		// Get the max. and min. value of each feature
		for (int i = 0; i < nFeatures; i++) {
			Float min = Float.POSITIVE_INFINITY;
			Float max = Float.NEGATIVE_INFINITY;
			for (Point p : points) {
				float feature = p.getFeature(i);
				min = min > feature ? feature : min;
				max = max < feature ? feature : max;
			}
			highest.add(max);
			lowests.add(min);
		}

		// Get k random centroids with random features between min. and max.
		// values of each features
		Random random = new Random();
		for (int i = 0; i < k; i++) {
			float[] features = new float[nFeatures];
			for (int f = 0; f < nFeatures; f++) {
				features[f] = random.nextFloat()
						* (highest.get(f) - lowests.get(f)) + lowests.get(f);
			}
			Point centroid = new Point(Long.MAX_VALUE, features);
			Cluster c = new Cluster(centroid);
			centroids.add(c);
		}
		return centroids;
	}

	/**
	 * Check if all the clusters are complete.
	 * 
	 * @param clusters
	 * @return true if all are complete, otherwise false
	 */
	private boolean isFinished(List<Cluster> clusters) {
		for (Cluster cluster : clusters) {
			if (!cluster.isCompleted()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Remove all the points each cluster had.
	 * 
	 * @param clusters
	 */
	private void cleanClusters(List<Cluster> clusters) {
		for (Cluster c : clusters) {
			c.cleanPoints();
		}
	}

	/**
	 * Assign each point of the data set to its closest cluster.
	 * 
	 * @param clusters
	 */
	private void assignPoints(List<Cluster> clusters) {
		// Get the closest cluster for each point
		for (Point p : points) {
			Cluster closest = clusters.get(0);
			Double minimumDistance = Double.MAX_VALUE;
			for (Cluster c : clusters) {
				// Euclidian distance between the point and the centroid
				Double distance = p.euclidianDistance(c.getCentroid());
				if (minimumDistance > distance) {
					minimumDistance = distance;
					closest = c;
				}
			}
			closest.addPoint(p);
		}
	}

	/**
	 * Recalculate the centroids of each cluster taking the mean value of each
	 * feature.
	 * 
	 * @param clusters
	 */
	private void recalculateCentroids(List<Cluster> clusters) {
		for (Cluster c : clusters) {
			// Is the cluster has no points, we are finished with it
			if (c.isEmpty()) {
				c.setCompleted(true);
				continue;
			}

			// Calculate mean value of each feature
			float[] meanFeatures = new float[nFeatures];
			Arrays.fill(meanFeatures, 0F);
			for (Point p : c.getPoints()) {
				for (int f = 0; f < nFeatures; f++) {
					meanFeatures[f] += ((p.getFeature(f) - meanFeatures[f])
							/ c.nPoints());
				}
			}

			Point newCentroid = new Point(Long.MAX_VALUE, meanFeatures);

			// If the new centroid is the same as the previois centroid, we are
			// finished with this cluster. If not, the new centroid is setted
			if (newCentroid.equals(c.getCentroid())) {
				c.setCompleted(true);
			} else {
				c.setCentroid(newCentroid);
			}
		}
	}
}
