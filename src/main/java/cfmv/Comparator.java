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
				new FileReader(output + "\\output.csv"), SEPARATOR, ESCAPE_CHAR,
				FIRST_LINE);

		int nMissedValues = 0;
		Map<String,String[]> wrongValues = new HashMap<String,String[]>(); // original value -> filled value
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
					
					if (!lineOriginal[f].equals(lineIncomplete[f])) { // Compare original - input
						nMissedValues++;
					}
					if (!lineOriginal[f].equals(lineOutput[f])) { // Compare original - output
						String[] data = {Integer.toString(f), lineOutput[f]}; // {Feature, output value}
						wrongValues.put(lineOriginal[f], data);
					}
				}
			}
		} finally {
			readerOriginal.close();
			readerIncomplete.close();
			readerOutput.close();
		}
		
		float[] distances = new float[highests.length];
		for(int i = 0; i < highests.length; i++){
			distances[i] = Math.abs(highests[i]-lowests[i]);
		}
		
		float averrageRelDistance = 0; // Average relative distance
		for(String originalV : wrongValues.keySet()){
			int feature = Integer.parseInt(wrongValues.get(originalV)[0]);
			String outputV = wrongValues.get(originalV)[1];
			
			int distance = Math.abs(Integer.parseInt(originalV)-Integer.parseInt(outputV));
			
			float relDistance = (((float) distance) / distances[feature]) * 100;
			System.out.print(originalV + " != " + outputV);
			System.out.printf(" | RD: %.2f%%\n", relDistance);
			averrageRelDistance += relDistance;
		}

		averrageRelDistance /= wrongValues.size();
		System.out.println("-----------------------------------------------");
		System.out.print("Exact hits: " + (nMissedValues-wrongValues.size()) + "/" + nMissedValues);
		System.out.printf(" (%.2f%%)\n",
				(1 - (((float) wrongValues.size()) / nMissedValues)) * 100);
		System.out.printf("Average relative distance : %.2f%%", averrageRelDistance);
	}
}