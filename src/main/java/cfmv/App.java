package cfmv;

import java.io.File;
import java.io.IOException;
import java.util.List;

import clustering.Cluster;
import clustering.KMeans;
import data.DataSet;

/**
 * This class is responsible for conducting the execution of the different
 * algorithms of the aplication.
 */
public class App {

	/**
	 * Run the application.
	 * 
	 * @param incompleteDS
	 *            csv file with the data set that has missed values
	 * @param output
	 *            directory where the output data set will be created
	 * @param k
	 *            argument for the k-means algorithm
	 * @throws NumberFormatException
	 *             format of the data set not valid
	 * @throws IOException
	 *             error at parsing CSV files
	 */
	public static void run(File incompleteDS, File output, int k)
			throws NumberFormatException, IOException {
		System.out.println("> Running...");

		DataSet ds = new DataSet(incompleteDS);
		ds.standardizePoints();

		KMeans kMeans = new KMeans(ds);
		List<Cluster> clusters = kMeans.run(k);

		ds.destandardizePoints();

		Finder f = new Finder(ds, clusters);
		f.replaceMissedValues();

		ds.exportDataSet(output);

		System.out.println("> Finished!");
	}
}
