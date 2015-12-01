package cfmv;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Compare the differences between the original data set and the output data set
 * with the missed values filled.
 */
public class Comparator {

	/**
	 * Compare the differences between the original data set and the output data
	 * set with the missed values filled.
	 * 
	 * @param original
	 *            original data set
	 * @param incomplete
	 *            incomplete data set
	 * @param output
	 *            data set resulting of the execution of the aplication
	 * @throws IOException
	 *             error at parsing CSV files
	 */
	public static void compare(File original, File incomplete, File output)
			throws IOException {
		final char SEPARATOR = ','; // Separator of the values of the CSV file
		final char ESCAPE_CHAR = '"'; // Escape character in the CSV file
		final int FIRST_LINE = 1; // First line to read (ommit headers)

		// Set up CSV reader
		CSVReader readerOriginal = new CSVReader(new FileReader(original),
				SEPARATOR, ESCAPE_CHAR, FIRST_LINE);
		CSVReader readerIncomplete = new CSVReader(new FileReader(incomplete),
				SEPARATOR, ESCAPE_CHAR, FIRST_LINE);
		CSVReader readerOutput = new CSVReader(
				new FileReader(output + "/output.csv"), SEPARATOR, ESCAPE_CHAR,
				FIRST_LINE);

		int nMissedValues = 0;
		// original value -> [feature, filled value]
		Map<String, String[]> wrongValues = new HashMap<String, String[]>();
		float[] highests; // Highest value of each feature
		float[] lowests; // Lowest value of each feature

		String[] lineOriginal;
		String[] lineIncomplete;
		String[] lineOutput;

		try {
			// Read one line (types) to know the number of features
			lineOriginal = readerOriginal.readNext();
			lineIncomplete = readerIncomplete.readNext();
			lineOutput = readerOutput.readNext();

			highests = new float[lineOriginal.length];
			lowests = new float[lineOriginal.length];
			Arrays.fill(highests, Float.NEGATIVE_INFINITY);
			Arrays.fill(lowests, Float.POSITIVE_INFINITY);

			while ((lineOriginal = readerOriginal.readNext()) != null) {
				lineIncomplete = readerIncomplete.readNext();
				lineOutput = readerOutput.readNext();

				for (int f = 0; f < lineOriginal.length; f++) {
					// Calculate heighest and lowest values of each feature
					Float val = Float.parseFloat(lineOriginal[f]);
					highests[f] = highests[f] < val ? val : highests[f];
					lowests[f] = lowests[f] > val ? val : lowests[f];

					// Look for missed values
					if (lineIncomplete[f].equals("")) {
						nMissedValues++;
						// Compare original - output
						if (!lineOriginal[f].equals(lineOutput[f])) {
							// Wrong value
							// [feature, output value]
							String[] data = { Integer.toString(f),
									lineOutput[f] };
							wrongValues.put(lineOriginal[f], data);
						}
					}
				}
			}
		} finally {
			readerOriginal.close();
			readerIncomplete.close();
			readerOutput.close();
		}

		// Distance between highest and lowest value of each feature
		float[] distances = new float[highests.length];
		for (int i = 0; i < highests.length; i++) {
			distances[i] = Math.abs(highests[i] - lowests[i]);
		}

		// Average relative ereror
		float averrageRelError = 0;
		System.out.println("> Inaccuracies (output != original):");
		for (String originalV : wrongValues.keySet()) {
			int feature = Integer.parseInt(wrongValues.get(originalV)[0]);
			String outputV = wrongValues.get(originalV)[1];

			int distance = Math.abs(
					Integer.parseInt(originalV) - Integer.parseInt(outputV));

			// Relative error (distance from inferred point to real point
			// divided by distance between the lowest and hihgest value of the
			// feature).
			float relError = (((float) distance) / distances[feature]) * 100;
			averrageRelError += relError;

			System.out.print("    " + outputV + " != " + originalV);
			System.out.printf(" | RE: %.2f%%\n", relError);
		}
		if(wrongValues.isEmpty()){
			System.out.println("    None");
		} else {
			averrageRelError /= wrongValues.size();
		}
		System.out.println("-----------------------------------------------");
		System.out.print("> Exact hits: " + (nMissedValues - wrongValues.size())
				+ "/" + nMissedValues);
		System.out.printf(" (%.2f%%)\n",
				(1 - (((float) wrongValues.size()) / nMissedValues)) * 100);
		System.out.printf("> Average relative error: %.2f%%", averrageRelError);
	}
}