package data;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class DataSet {
	/** Points of the data set */
	private List<Point> points;
	/** Mean of the data set */
	private float[] mean;
	/** Standard deviation of the data set */
	private float[] standardDeviation;
	/** Array to indicate which features have missed values */
	private boolean[] incompleteFeature;
	/** Names of each feature */
	private String[] headers;
	/**
	 * Type of data of each feature. Types: -i: integer number -d: decimal
	 * number -n: n categorical possible values (starting from 0)
	 */
	private String[] types;

	/** Logger */
	private static final Logger logger = LoggerFactory.getLogger(DataSet.class);

	public DataSet(File incompleteDS)
			throws NumberFormatException, IOException {
		points = new ArrayList<Point>();
		processData(incompleteDS);
	}

	/**
	 * @return list of data set's points
	 */
	public List<Point> getPoints() {
		return points;
	}

	/**
	 * @return number of features of each point
	 */
	public int nFeatures() {
		return mean.length;
	}

	public boolean hasMissedValues(int f) {
		return incompleteFeature[f];
	}

	/**
	 * @return array with the type of each feature
	 */
	public String getType(int f) {
		return this.types[f];
	}

	/**
	 * Parses the data set and creates Point objetcs with the features given.
	 * 
	 * @param incompleteDS
	 *            data set in CSV format
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private void processData(File incompleteDS)
			throws NumberFormatException, IOException {
		System.out.println("Processing data set file...");

		String[] point; // Array of strings with the features of a point
		float[] sum; // Sum of all the values of each feature
		long num = 0; // Number of points processed
		int nMissedValues = 0; // Number of missed values

		final char SEPARATOR = ','; // Separator of the values of the CSV file
		final char ESCAPE_CHAR = '"'; // Escape character in the CSV file
		final int FIRST_LINE = 0; // First line to read (starting from 0)

		// Set up CSV reader
		CSVReader reader = new CSVReader(new FileReader(incompleteDS),
				SEPARATOR, ESCAPE_CHAR, FIRST_LINE);

		try {
			// Read header
			headers = reader.readNext();
			// Read types
			types = reader.readNext();

			// Arrays
			sum = new float[headers.length];
			mean = new float[headers.length];
			standardDeviation = new float[headers.length];
			incompleteFeature = new boolean[headers.length];
			Arrays.fill(sum, 0F);
			Arrays.fill(incompleteFeature, false);

			// Parse CSV dataset
			while ((point = reader.readNext()) != null) {
				float[] features = new float[point.length];
				Set<Integer> missedFeatures = new HashSet<Integer>(
						point.length / 2);

				// Process each feature of the point
				for (int f = 0; f < point.length; f++) {
					if (point[f].equals("")) { // Missed value
						incompleteFeature[f] = true;
						missedFeatures.add(f);
						features[f] = 0;
						nMissedValues++;
					} else {
						features[f] = Float.parseFloat(point[f]);
						sum[f] += features[f];
					}
				}

				Point p = missedFeatures.isEmpty() ? new Point(features)
						: new Point(features, missedFeatures);
				points.add(p);
				num++;
			}
		} finally {
			reader.close();
		}
		
		System.out.println("Missed values: " + nMissedValues);

		// Calculate mean
		for (int i = 0; i < sum.length; i++) {
			mean[i] = sum[i] / num;
		}

		// Calculate standard deviation
		Arrays.fill(sum, 0F);
		for (Point p : points) {
			float[] features = p.getValues();
			for (int i = 0; i < features.length; i++) {
				sum[i] += Math.pow(features[i] - mean[i], 2);
			}
		}
		for (int i = 0; i < sum.length; i++) {
			standardDeviation[i] = (float) Math.sqrt(sum[i] / num);
		}

		logger.debug("Data set processed");
		logger.debug("Mean:\n" + Arrays.toString(mean));
		logger.debug(
				"Standar deviation:\n" + Arrays.toString(standardDeviation));
		logger.debug("Features with missed values\n"
				+ Arrays.toString(incompleteFeature));
		for (Point p : points) {
			logger.debug(p.toString());
		}
	}

	public void exportDataSet(File output) throws IOException {
		System.out.println("Exporting data set...");

		final char SEPARATOR = ','; // Separator of the values of the CSV file
		final char ESCAPE_CHAR = '"'; // Escape character in the CSV file
		CSVWriter writer = new CSVWriter(
				new FileWriter(output + "\\output.csv"), SEPARATOR,
				ESCAPE_CHAR);

		try {
			writer.writeNext(headers);
			writer.writeNext(types);
			for (Point p : points) {
				// Convert array of values (floats) to array of strings and
				// write it
				String[] line = new String[this.nFeatures()];
				for (int f = 0; f < this.nFeatures(); f++) {
					if (this.getType(f).equals("i")
							|| this.getType(f).equals("c")) {
						int n = (int) Math.round(p.getValue(f));
						line[f] = Integer.toString(n);
					} else {
						line[f] = Float.toString(p.getValue(f));
					}
				}
				writer.writeNext(line);
			}
		} finally {
			writer.close();
		}
	}

	/**
	 * Standarize the values of the features of all points. That is making the
	 * valuess of each feature in the data have zero-mean and unit-variance.
	 */
	public void standarizePoints() {
		System.out.println("Standarizing points...");

		for (Point p : points) {
			float[] features = p.getValues();
			for (int i = 0; i < features.length; i++) {
				features[i] = (features[i] - mean[i]) / standardDeviation[i];
			}
		}

		logger.debug("Points standarized");
		for (Point p : points) {
			logger.debug(p.toString());
		}
	}

	/**
	 * Destandarize the values of the features of all points. That is making the
	 * valuess of each feature in the data have mean μ and a standardDeviation
	 * σ.
	 */
	public void destandarizePoints() {
		System.out.println("Destandarizing points...");

		for (Point p : points) {
			float[] features = p.getValues();
			for (int i = 0; i < features.length; i++) {
				// Round to 6 decimal places to remove truncation errors
				features[i] = round(
						mean[i] + features[i] * standardDeviation[i], 6);
			}
		}

		logger.debug("Points destandarized");
		for (Point p : points) {
			logger.debug(p.toString());
		}
	}

	/**
	 * Rounds a decimal number to a specific number of decimal places.
	 * 
	 * @param number
	 *            number to round
	 * @param nDecimals
	 *            specified number of decimal places
	 * @return rounded number
	 */
	private float round(float number, int nDecimals) {
		BigDecimal bd = new BigDecimal(number);
		bd = bd.setScale(nDecimals, RoundingMode.HALF_DOWN);
		return bd.floatValue();
	}
}
