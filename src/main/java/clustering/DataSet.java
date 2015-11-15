package clustering;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

public class DataSet {
	private List<Point> points;
	private float[] mean;
	private float[] standardDeviation;

	/** Logger */
	private static final Logger logger = LoggerFactory.getLogger(DataSet.class);

	public DataSet(String csvFile) throws NumberFormatException, IOException {
		points = new ArrayList<Point>();
		processData(csvFile);
		standarizePoints();
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
		Arrays.fill(sum, 0F);

		// Parse CSV dataset
		while ((point = reader.readNext()) != null) {
			float[] features = new float[point.length];
			for (int i = 0; i < point.length; i++) {
				if (point[i].equals("")) {
					System.out.println("Missed value!");
					features[i] = 0;
				} else {
					features[i] = Float.parseFloat(point[i]);
					sum[i] += features[i];
				}
			}
			Point p = new Point(num, features);
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
		for (Point p : points) {
			logger.debug(p.toString());
		}
	}

	/**
	 * Standarize the values of the features of all points. That is making the
	 * valuess of each feature in the data have zero-mean and unit-variance.
	 */
	private void standarizePoints() {
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
}
