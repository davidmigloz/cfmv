package cfmv;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import au.com.bytecode.opencsv.CSVReader;

public class App {

	public static void main(String[] args) {
		try {
			// Build reader instance
			// Read data.csv
			// Default seperator is comma
			// Default quote character is double quote
			// Start reading from line number 2 (line numbers start from zero)
			CSVReader reader = new CSVReader(new FileReader("src/main/resources/data.csv"), ',', '"', 1);

			// Read CSV line by line 
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				if (nextLine != null) {
					System.out.println(Arrays.toString(nextLine));
				}
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("Error I/O: " + e);
		} 
	}
}
