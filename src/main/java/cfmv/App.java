package cfmv;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;
import clustering.Cluster;
import clustering.KMeans;
import data.DataSet;

public class App {

	/** Logger */
	private static final Logger logger = LoggerFactory.getLogger(App.class);

	public static void run(File incompleteDS, File output, int k)
			throws NumberFormatException, IOException {
		System.out.println("Running...");

		DataSet ds = new DataSet(incompleteDS);
		ds.standarizePoints();

		KMeans kMeans = new KMeans(ds);
		List<Cluster> clusters = kMeans.run(k);

		ds.destandarizePoints();

		Finder f = new Finder(ds, clusters);
		f.replaceMissedValues();

		ds.exportDataSet(output);

		System.out.println("Finished!");
	}

	public static void similarity(File original, File incomplete, File output)
			throws IOException {
		final char SEPARATOR = ','; // Separator of the values of the CSV file
		final char ESCAPE_CHAR = '"'; // Escape character in the CSV file
		final int FIRST_LINE = 0; // First line to read (starting from 0)

		// Set up CSV reader
		CSVReader readerOriginal = new CSVReader(new FileReader(original),
				SEPARATOR, ESCAPE_CHAR, FIRST_LINE);
		CSVReader readerIncomplete = new CSVReader(new FileReader(incomplete),
				SEPARATOR, ESCAPE_CHAR, FIRST_LINE);
		CSVReader readerOutput = new CSVReader(
				new FileReader(output + "\\output.csv"), SEPARATOR, ESCAPE_CHAR,
				FIRST_LINE);

		int nMissedValues = 0;
		int nErrors = 0;

		String[] lineOriginal;
		String[] lineIncomplete;
		String[] lineOutput;

		try {
			while ((lineOriginal = readerOriginal.readNext()) != null) {
				lineIncomplete = readerIncomplete.readNext();
				lineOutput = readerOutput.readNext();

				for (int v = 0; v < lineOriginal.length; v++) {
					if (!lineOriginal[v].equals(lineIncomplete[v])) {
						nMissedValues++;
					}
					if (!lineOriginal[v].equals(lineOutput[v])) {
						nErrors++;
						logger.debug(lineOriginal[v] + " != " + lineOutput[v]);
					}
				}
			}
		} finally {
			readerOriginal.close();
			readerIncomplete.close();
			readerOutput.close();
		}

		System.out.println("Misssed values: " + nMissedValues);
		System.out.println("Errors: " + nErrors);
		System.out.println("Accuracy ratio: "
				+ (1 - ((float) nErrors) / nMissedValues) * 100 + "%");
	}
}
