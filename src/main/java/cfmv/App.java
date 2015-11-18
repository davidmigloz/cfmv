package cfmv;

import java.io.FileNotFoundException;
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

	public static void main(String[] args) {
		logger.info("Start execution.");

		String originalDS = "src/main/resources/original.csv";
		String incompletDS = "src/main/resources/incomplete.csv";
		String output = "src/main/resources/output.csv";

		try {
			DataSet ds = new DataSet(incompletDS);
			ds.standarizePoints();

			KMeans kMeans = new KMeans(ds);
			List<Cluster> clusters = kMeans.run(3);

			ds.destandarizePoints();

			Finder f = new Finder(ds, clusters);
			f.replaceMissedValues();

			ds.exportDataSet(output);

			logger.info("End of execution.");

			similarity(originalDS, incompletDS, output);
		} catch (IOException e) {
			System.out
					.println("Error at processing data set. " + e.getMessage());
		} catch (NumberFormatException e) {
			System.out.println("Wrong value. " + e.getMessage());
		}
	}

	public static void similarity(String original, String incomplete,
			String output) throws IOException {
		final char SEPARATOR = ','; // Separator of the values of the CSV file
		final char ESCAPE_CHAR = '"'; // Escape character in the CSV file
		final int FIRST_LINE = 0; // First line to read (starting from 0)

		// Set up CSV reader
		CSVReader readerOriginal = new CSVReader(new FileReader(original),
				SEPARATOR, ESCAPE_CHAR, FIRST_LINE);
		CSVReader readerIncomplete = new CSVReader(new FileReader(incomplete),
				SEPARATOR, ESCAPE_CHAR, FIRST_LINE);
		CSVReader readerOutput = new CSVReader(new FileReader(output),
				SEPARATOR, ESCAPE_CHAR, FIRST_LINE);

		int nMissedValues = 0;
		int nErrors = 0;

		String[] lineOriginal;
		String[] lineIncomplete;
		String[] lineOutput;

		while ((lineOriginal = readerOriginal.readNext()) != null) {
			lineIncomplete = readerIncomplete.readNext();
			lineOutput = readerOutput.readNext();

			for (int v = 0; v < lineOriginal.length; v++) {
				if (!lineOriginal[v].equals(lineIncomplete[v])) {
					nMissedValues++;
				}
				if (!lineOriginal[v].equals(lineOutput[v])) {
					nErrors++;
				}
			}
		}

		logger.info("Misssed values: " + nMissedValues);
		logger.info("Errors: " + nErrors);
		logger.info("Accuracy ratio: "
				+ (1 - ((float) nErrors) / nMissedValues) * 100 + "%");
	}
}
