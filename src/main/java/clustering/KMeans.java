package clustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.DataSet;
import data.Point;

/**
 * K-means algorithm. K-means is an iterative algorithm that keeps assigning
 * data points to clusters identified by special points called centroids, until
 * the cluster assignment stabilizes.
 */
public class KMeans {
	/** Number of clusters to create */
	int k;
	/** Data set with the points */
	DataSet ds;

	/** Logger */
	private static final Logger logger = LoggerFactory.getLogger(KMeans.class);

	/**
	 * Create new instance of k-means algorithm.
	 * 
	 * @param ds
	 *            data set
	 */
	public KMeans(DataSet ds) {
		this.ds = ds;
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
		System.out.println("> Running " + k + "-means...");

		List<Cluster> clusters = chooseCentroids();

		int nIter = 0; // Number of iterations
		while (!isFinished(clusters)) {
			logger.trace("Not finish");
			nIter++;
			cleanClusters(clusters);
			assignPoints(clusters);
			recalculateCentroids(clusters);
		}

		// Show figures
		System.out.println("      " + nIter + " iterations executed");
		for (int i = 0; i < clusters.size(); i++) {
			System.out.println("      ·Cluster " + (i + 1) + ": "
					+ clusters.get(i).getPoints().size() + " points");
		}

		return clusters;
	}

	/**
	 * Choose centroids for the k clustes uniformly distributed between the min.
	 * and max. value of each feature.
	 * 
	 * @return k clusters each one with the centroid setted
	 */
	private List<Cluster> chooseCentroids() {
		List<Cluster> centroids = new ArrayList<Cluster>();

		float[] highest = new float[ds.nFeatures()];
		float[] lowests = new float[ds.nFeatures()];

		// Get the max. and min. value of each feature
		for (int i = 0; i < ds.nFeatures(); i++) {
			if (!ds.hasMissedValues(i)) {
				float min = Float.POSITIVE_INFINITY;
				float max = Float.NEGATIVE_INFINITY;
				for (Point p : ds.getPoints()) {
					float feature = p.getValue(i);
					min = min > feature ? feature : min;
					max = max < feature ? feature : max;
				}
				highest[i] = max;
				lowests[i] = min;
			}
		}

		// Get k random centroids uniformly distributed between min. and max.
		// values of each features
		for (int i = 0; i < k; i++) {
			float[] features = new float[ds.nFeatures()];

			for (int f = 0; f < ds.nFeatures(); f++) {
				if (!ds.hasMissedValues(f)) {
					float step = (highest[f] - lowests[f]) / (float) k;
					features[f] = lowests[f] + step / 2 + i * step;
				} else {
					features[f] = 0;
				}
			}
			Point centroid = new Point(features);
			Cluster c = new Cluster(ds, centroid);
			centroids.add(c);
		}

		logger.debug("Initial centroids:");
		for (Cluster c : centroids) {
			logger.debug(c.toString());
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
			if (!cluster.isFinished()) {
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
		logger.debug("Clusters cleaned");
	}

	/**
	 * Assign each point of the data set to its closest cluster.
	 * 
	 * @param clusters
	 */
	private void assignPoints(List<Cluster> clusters) {
		// Get the closest cluster for each point
		for (Point p : ds.getPoints()) {
			Cluster closest = clusters.get(0); // Choose an initial cluster
			Double minimumDistance = Double.MAX_VALUE;
			for (Cluster c : clusters) {
				// Squared Euclidian distance between the point and the centroid
				Double distance = p.squaredEuclidianDistance(ds,
						c.getCentroid());
				if (minimumDistance > distance) {
					minimumDistance = distance;
					closest = c;
				}
			}
			logger.debug(
					p.toString() + " --> " + closest.getCentroid().toString());
			// Add point to the closest cluster
			closest.addPoint(p);
			// Register in the cluster if the point has features with missed
			// values
			closest.addMissedFeatures(p.getMissedFeatures());
		}

		logger.debug("Points assigned");
		for (int i = 0; i < clusters.size(); i++) {
			logger.debug("Cluster " + (i + 1) + ": "
					+ clusters.get(i).getPoints().size() + " points.");
		}
	}

	/**
	 * Recalculate the centroids of each cluster taking as a new centroid a
	 * point with the mean value of each feature.
	 * 
	 * @param clusters
	 */
	private void recalculateCentroids(List<Cluster> clusters) {
		logger.debug("New centroids:");

		for (Cluster c : clusters) {
			// If the cluster has no points, the centroid is the same
			if (c.isEmpty()) {
				c.setFinished(true);
				logger.debug("The same (empty)");
				continue;
			}

			// Calculate mean value of each feature
			float[] meanFeatures = new float[ds.nFeatures()];
			Arrays.fill(meanFeatures, 0F);
			for (Point p : c.getPoints()) {
				for (int f = 0; f < ds.nFeatures(); f++) {
					if (!ds.hasMissedValues(f)) {
						meanFeatures[f] += p.getValue(f);
					}
				}
			}
			for (int f = 0; f < ds.nFeatures(); f++) {
				if (!ds.hasMissedValues(f)) {
					meanFeatures[f] /= c.nPoints();
				}
			}

			Point newCentroid = new Point(meanFeatures);

			// If the new centroid is the same as the previois centroid, we are
			// finished with this cluster. If not, the new centroid is setted
			if (newCentroid.equals(ds, c.getCentroid())) {
				c.setFinished(true);
				logger.debug("The same");
			} else {
				c.setCentroid(newCentroid);
				logger.debug(newCentroid.toString());
			}
		}
	}
}