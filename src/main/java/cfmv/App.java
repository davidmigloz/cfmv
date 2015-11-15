package cfmv;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import clustering.Cluster;
import clustering.DataSet;
import kmeans.KMeans;

public class App {

	/** Logger */
	private static final Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		String dataSet = "src/main/resources/data.csv";

		try {
			DataSet ds = new DataSet(dataSet);

			// Clusterize data
			double fObjPrev;
			double fObj = Double.MAX_VALUE;
			int k = 2;
			do {
				KMeans kMeans = new KMeans(ds);
				List<Cluster> clusters = kMeans.run(k);
				fObjPrev = fObj;
				fObj = 0;
				k++;
				for (Cluster c : clusters) {
					fObj += c.calculateObjetiveFunction();
				}
				logger.info("F. obj: " + fObj + "F. obj prev: " + fObjPrev
						+ "Diff: " + (fObjPrev - fObj));
			} while ((fObjPrev - fObj) > 0.1);

			System.out.println("FIN");
		} catch (IOException e) {
			System.out
					.println("Error at processing data set. " + e.getMessage());
		} catch (NumberFormatException e) {
			System.out.println("Wrong value. " + e.getMessage());
		}
	}
}
