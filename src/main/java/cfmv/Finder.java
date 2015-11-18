package cfmv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import clustering.Cluster;
import data.DataSet;
import data.Point;

public class Finder {
	List<Cluster> clusters;
	DataSet ds;

	/** Logger */
	private static final Logger logger = LoggerFactory.getLogger(Finder.class);

	public Finder(DataSet ds, List<Cluster> clusters) {
		this.ds = ds;
		this.clusters = clusters;
	}

	public void replaceMissedValues() {
		logger.info("Replace missed values.");
		logger.debug("Number of clusters: " + clusters.size());

		for (Cluster c : clusters) {
			logger.debug("Cluster with " + c.nPoints() + " points. MV: "
					+ c.hasMissedFeatures());

			if (c.hasMissedFeatures()) {
				Map<Integer, Float> mean = new HashMap<Integer, Float>();
				Map<Integer, List<Point>> incompletePoints = new HashMap<Integer, List<Point>>();

				// Prepare maps with an entry per misdded feature
				for (int f : c.getMissedFeatures()) {
					mean.put(f, new Float(0));
					incompletePoints.put(f, new ArrayList<Point>());
				}

				// Register points with missed values and sum all values of each
				// feature with missed values to calculate the mean
				for (Point p : c.getPoints()) {
					for (int f : c.getMissedFeatures()) {
						if (p.isMissedFeature(f)) {
							incompletePoints.get(f).add(p);
						} else {
							mean.put(f, mean.get(f) + p.getValue(f));
						}
					}
				}

				// Divide by total number of points that don't have a missed
				// value for this feature to obtain the mean
				for (int f : c.getMissedFeatures()) {
					mean.put(f, mean.get(f)
							/ (c.nPoints() - incompletePoints.get(f).size()));
				}

				// Replace missed values by the mean of the feature
				for (int f : incompletePoints.keySet()) {
					String type = ds.getType(f);
					for (Point p : incompletePoints.get(f)) {
						if (type.equals("i") || type.equals("c")) {
							// Integer
							p.setValue(f, Math.round(mean.get(f)));
						} else {
							// Decimal
							p.setValue(f, mean.get(f));
						}
					}
				}
			}
		}
	}
}
