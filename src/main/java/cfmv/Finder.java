package cfmv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clustering.Cluster;
import data.Point;

public class Finder {
	List<Cluster> clusters;

	public Finder(List<Cluster> clusters) {
		this.clusters = clusters;
	}

	public void replaceMissedValues() {
		for (Cluster c : clusters) {
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
					for (Point p : incompletePoints.get(f)) {
						p.setValue(f, mean.get(f));
					}
				}
			}
		}
	}
}
