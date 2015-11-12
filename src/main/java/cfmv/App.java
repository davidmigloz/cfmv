package cfmv;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class App {
	private String data = "src/main/resources/data.csv";
	private List<Point> points;

	public App(String data) {
		this.data = data;
		this.points = new ArrayList<Point>();
	}

	private void parseCSV() {
		try {
			CSVReader reader = new CSVReader(new FileReader(data), ';', '"', 1);

			String[] nextLine;
			long line = 0;
			while ((nextLine = reader.readNext()) != null) {
				int[] features = new int[nextLine.length];
				for(int i = 0; i < nextLine.length; i++){
					if(nextLine[i].equals("")){
						System.out.println("Missed value!");
						features[i] = 0;
					} else {
						features[i] = Integer.parseInt(nextLine[i]);
					}
				}
				Point p = new Point(line, features);
				points.add(p);
				line++;
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("Error I/O: " + e.getMessage());
		} catch (NumberFormatException e){
			System.out.println("Wrong value. " + e.getMessage());
		}
	}
	
	private void show(){
		for(Point p : points){
			System.out.println(p);
		}
	}

	public static void main(String[] args) {
		App myApp = new App("src/main/resources/data.csv");
		myApp.parseCSV();
		myApp.show();
	}
}
