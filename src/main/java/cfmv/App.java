package cfmv;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import clustering.Cluster;
import clustering.KMeans;
import data.DataSet;

public class App {

	/** Logger */
	private static final Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		String dataSet = "src/main/resources/data.csv";

		try {
			DataSet ds = new DataSet(dataSet);
			ds.standarizePoints();

			KMeans kMeans = new KMeans(ds);
			List<Cluster> clusters = kMeans.run(3);

			ds.destandarizePoints();

			logger.debug("Final clusters:");
			for (int i = 0; i < clusters.size(); i++) {
				logger.debug("Cluster " + i + ":");
				logger.debug(clusters.get(i).toString());
			}
			
			System.out.println("FIN");
		} catch (IOException e) {
			System.out
					.println("Error at processing data set. " + e.getMessage());
		} catch (NumberFormatException e) {
			System.out.println("Wrong value. " + e.getMessage());
		}
	}
}
