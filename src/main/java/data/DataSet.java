package data;

import java.io.FileReader;
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

public class DataSet {
	/** Points of the data set */
	private List<Point> points;
	/** Mean of the data set */
	private float[] mean;
	/** Standard deviation of the data set */
	private float[] standardDeviation;
	/** Array to indicate which features have missed values */
	private boolean[] incompleteFeature;

	/** Logger */
	private static final Logger logger = LoggerFactory.getLogger(DataSet.class);

	public DataSet(String csvFile) throws NumberFormatException, IOException {
		points = new ArrayList<Point>();
		processData(csvFile);
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

	public boolean hasMissedValues(int i) {
		return incompleteFeature[i];
	}

	/**
	 * Parses the data set and creates Point objetcs with the features given.
	 * 
	 * @param dataSet
	 *            data set in CSV format
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private void processData(String dataSet)
			throws NumberFormatException, IOException {
		String[] point; // Array of strings with the features of a point
		float[] sum; // Sum of all the values of each feature
		long num = 0; // Number of points processed

		final char SEPARATOR = ','; // Separator of the values of the CSV file
		final char ESCAPE_CHAR = '"'; // Escape character in the CSV file
		final int FIRST_LINE = 0; // First line to read (starting from 0)

		// Set up CSV reader
		CSVReader reader = new CSVReader(new FileReader(dataSet), SEPARATOR,
				ESCAPE_CHAR, FIRST_LINE);

		// Read header
		point = reader.readNext();
		sum = new float[point.length];
		mean = new float[point.length];
		standardDeviation = new float[point.length];
		incompleteFeature = new boolean[point.length];
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
		reader.close();

		// Calculate mean
		for (int i = 0; i < sum.length; i++) {
			mean[i] = sum[i] / num;
		}

		// Calculate standard deviation
		Arrays.fill(sum, 0F);
		for (Point p : points) {
			float[] features = p.getFeatures();
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

	/**
	 * Standarize the values of the features of all points. That is making the
	 * valuess of each feature in the data have zero-mean and unit-variance.
	 */
	public void standarizePoints() {
		for (Point p : points) {
			float[] features = p.getFeatures();
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
	 * valuess of each feature in the data have mean μ and a standardDeviation σ.
	 */
	public void destandarizePoints() {
		for (Point p : points) {
			float[] features = p.getFeatures();
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
