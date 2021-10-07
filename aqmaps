package uk.ac.ed.inf.aqmaps;

/*
 * Main class for the project
 */

import java.io.IOException;
import java.util.List;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

public class App {

	public static String air_quality_data_jsonURL = "";
	public static String no_fly_zone_jsonURL = "";
	
	/*
	 * Read and parse all the input arguments and pass them to a new Play class and a new FlightPath class
	 * 
	 * @param args - input arguments
	 */
	
	public static void main(String[] args) throws IOException, InterruptedException {

		String day = args[0];
		String month = args[1]; 
		String year = args[2];
		String latitude = args[3];
		String longitude = args[4]; 
		String seed = args[5]; 
		String port = args[6];
		
		air_quality_data_jsonURL = "http://localhost:" + port + "/maps/" + year + "/" + month + "/" + day
				+ "/air-quality-data.json";
		no_fly_zone_jsonURL = "http://localhost:" + port + "/buildings/no-fly-zones.geojson";
		
		Play play = new Play(day, month, year, latitude, longitude, seed, port);
		FlightPath fp = new FlightPath(day, month, year, latitude, longitude, seed, port);
		String[][] database = play.getDatabase(day, month, year, latitude, longitude, seed, port);
		List<String[]> finalFlightPath = fp.finalFlightPath;	
		List<Point> linePoints = play.getLinePoints(day, month, year, latitude, longitude, seed, port, finalFlightPath);
		List<Feature> features = play.getFeatures(linePoints, database);
		String finalStr = play.convertStr(finalFlightPath);
		FeatureCollection fc = play.getFeatureCollection(linePoints, database, features);
		
		play.wirteTxtFile(finalStr, day, month, year);
		play.toJson(fc, day, month, year);	
		
		System.out.println(finalStr);
		
	}
}
