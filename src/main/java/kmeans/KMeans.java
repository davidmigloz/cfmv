package kmeans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import clustering.Cluster;
import clustering.DataSet;
import clustering.Point;

/**
 * K-means algorithm.
 */
public class KMeans {
	int k;
	DataSet ds;

	/** Logger */
	private static final Logger logger = LoggerFactory.getLogger(KMeans.class);

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
		logger.info("Running k-means k=" + k);

		List<Cluster> clusters = chooseCentroids();

		while (!isFinished(clusters)) {
			logger.debug("Not finish");
			cleanClusters(clusters);
			assignPoints(clusters);
			recalculateCentroids(clusters);
			
			//logger.info("Centroids:");
			//for(Cluster c : clusters){
			//	logger.info(c.getCentroid().toString());
			//}
		}

		logger.info("K-means executions finished!");
		logger.debug("Final clusters:");
		for (int i = 0; i < clusters.size(); i++) {
			logger.debug("Cluster " + i + ":");
			logger.debug(clusters.get(i).toString());
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

		float[] highest = new float[ds.nFeatures()];
		float[] lowests = new float[ds.nFeatures()];

		// Get the max. and min. value of each feature
		for (int i = 0; i < ds.nFeatures(); i++) {
			float min = Float.POSITIVE_INFINITY;
			float max = Float.NEGATIVE_INFINITY;
			for (Point p : ds.getPoints()) {
				float feature = p.getFeature(i);
				min = min > feature ? feature : min;
				max = max < feature ? feature : max;
			}
			highest[i] = max;
			lowests[i] = min;
		}	

		// Get k random centroids with random features between min. and max.
		// values of each features
		Random random = new Random();
		for (int i = 0; i < k; i++) {
			float[] features = new float[ds.nFeatures()];
			for (int f = 0; f < ds.nFeatures(); f++) {
				features[f] = random.nextFloat()
						* (highest[f] - lowests[f]) + lowests[f];
			}
			Point centroid = new Point(Long.MAX_VALUE, features);
			Cluster c = new Cluster(centroid);
			centroids.add(c);
		}
		
		logger.debug("Initial centroids:");
		for(Cluster c : centroids){
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
		
		logger.debug("Points assigned");
		for(Cluster c : clusters){
			logger.debug(c.toString());
		}
	}

	/**
	 * Recalculate the centroids of each cluster taking the mean value of each
	 * feature.
	 * 
	 * @param clusters
	 */
	private void recalculateCentroids(List<Cluster> clusters) {
		logger.debug("New centroids:");
		
		for (Cluster c : clusters) {
			// Is the cluster has no points, the centroid is the same
			if (c.isEmpty()) {
				c.setCompleted(true);
				logger.debug("The same (empty)");
				continue;
			}

			// Calculate mean value of each feature
			float[] meanFeatures = new float[ds.nFeatures()];
			Arrays.fill(meanFeatures, 0F);
			for (Point p : c.getPoints()) {
				for (int f = 0; f < ds.nFeatures(); f++) {
					meanFeatures[f] += ((p.getFeature(f) - meanFeatures[f])
							/ c.nPoints());
				}
			}

			Point newCentroid = new Point(Long.MAX_VALUE, meanFeatures);

			// If the new centroid is the same as the previois centroid, we are
			// finished with this cluster. If not, the new centroid is setted
			if (newCentroid.equals(c.getCentroid())) {
				c.setCompleted(true);
				logger.debug("The same");
			} else {
				c.setCentroid(newCentroid);
				logger.debug(newCentroid.toString());
			}
		}
	}
}
