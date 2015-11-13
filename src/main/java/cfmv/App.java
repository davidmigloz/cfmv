package cfmv;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import clustering.Cluster;
import clustering.Point;
import kmeans.KMeans;

public class App {

	public static void main(String[] args) {
		String dataSet = "src/main/resources/data-1.csv";

		try {
			List<Point> puntos = processData(dataSet);
			KMeans kMeans = new KMeans(puntos);
			List<Cluster> clusters = kMeans.run(3);
			for (int i = 0; i < clusters.size(); i++) {
				System.out.println("Cluster " + i + ":");
				clusters.get(i).toString();
			}

		} catch (IOException e) {
			System.out
					.println("Error at processing data set. " + e.getMessage());
		} catch (NumberFormatException e) {
			System.out.println("Wrong value. " + e.getMessage());
		}

	}

	/**
	 * Parses the data set and creates Point objetcs with the features given.
	 * 
	 * @param dataSet
	 *            data set in CSV format
	 * @return list of Point objects contains in the data set
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private static List<Point> processData(String dataSet)
			throws NumberFormatException, IOException {
		List<Point> points; // List with Point objetcs obtained after parsing
		String[] point; // Array of strings with the features of a point
		long num = 0; // Number of points processed
		final char SEPARATOR = ','; // Separator of the values of the CSV file
		final char ESCAPE_CHAR = '"'; // Escape character in the CSV file
		final int FIRST_LINE = 1; // First line to read (starting from 0)

		// Set up CSV reader
		CSVReader reader = new CSVReader(new FileReader(dataSet), SEPARATOR,
				ESCAPE_CHAR, FIRST_LINE);
		points = new ArrayList<Point>();

		// Parse CSV dataset
		while ((point = reader.readNext()) != null) {
			float[] features = new float[point.length];
			for (int i = 0; i < point.length; i++) {
				if (point[i].equals("")) {
					System.out.println("Missed value!");
					features[i] = 0;
				} else {
					features[i] = Float.parseFloat(point[i]);
				}
			}
			Point p = new Point(num, features);
			points.add(p);
			num++;
		}
		reader.close();
		return points;
	}
}
