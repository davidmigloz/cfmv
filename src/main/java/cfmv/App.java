package cfmv;

import java.io.File;
import java.io.IOException;
import java.util.List;

import clustering.Cluster;
import clustering.KMeans;
import data.DataSet;

public class App {

	/**
	 * Run the application.
	 * 
	 * @param incompleteDS csv file with the data set that has missed values
	 * @param output 
	 * @param k argument for the k-means algorithm
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static void run(File incompleteDS, File output, int k)
			throws NumberFormatException, IOException {
		System.out.println(">Running...");

		DataSet ds = new DataSet(incompleteDS);
		ds.standardizePoints();

		KMeans kMeans = new KMeans(ds);
		List<Cluster> clusters = kMeans.run(k);

		ds.destandardizePoints();

		Finder f = new Finder(ds, clusters);
		f.replaceMissedValues();

		ds.exportDataSet(output);

		System.out.println(">Finished!");
	}
}
