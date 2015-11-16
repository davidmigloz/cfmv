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
		String dataSet = "src/main/resources/data-2.csv";
		String output = "src/main/resources/output.csv";

		try {
			DataSet ds = new DataSet(dataSet);
			ds.standarizePoints();

			KMeans kMeans = new KMeans(ds);
			List<Cluster> clusters = kMeans.run(3);

			ds.destandarizePoints();
			
			Finder f = new Finder(clusters);
			f.replaceMissedValues();
			
			ds.exportDataSet(output);
			
			System.out.println("FIN");
		} catch (IOException e) {
			System.out
					.println("Error at processing data set. " + e.getMessage());
		} catch (NumberFormatException e) {
			System.out.println("Wrong value. " + e.getMessage());
		}
	}
}
