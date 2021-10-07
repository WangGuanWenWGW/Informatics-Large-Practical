package uk.ac.ed.inf.aqmaps;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

/*
 * A class which obtains the data from the web server, process the data and write out the text and geojson file
 */
 
public class Play {
	
	private String day;
	private String month;
	private String year; 
	private String latitude; 
	private String longitude;
	private String seed; 
	private String port;
	
	public final String[][] database = new String[Properties.getNumSensors()][5];
	
	/*
	 * Constructor to create a new play of the specified input parameters
	 * 
	 * @param day, month, year, latitude, longitude, seed, port - the specified command-line arguments
	 * 
	 */
	
	public Play(String day, String month, String year, String latitude, String longitude, String seed, String port) {
		this.setDay(day);
		this.setMonth(month);
		this.setYear(year);
		this.setLatitude(latitude);
		this.setLongitude(longitude);
		this.setSeed(seed);
		this.setPort(port);
	}
	
	/*
	 * Method to download the data of sensors from the web server and store all the data into a 2D array
	 * 
	 * @param day, month, year, latitude, longitude, seed, port - the specified command-line arguments
	 * 
	 * @return a 2D array of strings that stores the information of all sensors
	 */
	
	public String[][] getDatabase(String day, String month, String year, String latitude, String longitude,
			String seed, String port) throws IOException, InterruptedException { 

		String air_quality_data_jsonURL = App.air_quality_data_jsonURL;
		var client = HttpClient.newHttpClient();
		var request = HttpRequest.newBuilder().uri(URI.create(air_quality_data_jsonURL)).build();
		var response = client.send(request, BodyHandlers.ofString());

		Type listType = new TypeToken<ArrayList<Sensor>>() {
		}.getType();
		List<Sensor> listOFsensors = new Gson().fromJson(response.body(), listType);

		for (int i = 0; i < listOFsensors.size(); i++) {
			String address = listOFsensors.get(i).getLocation();
			String[] W3W = address.split("\\.");
			String W3W_URL = "http://localhost:" + port + "/words/" + W3W[0] + "/" + W3W[1] + "/" + W3W[2]
					+ "/details.json";

			var clients = HttpClient.newHttpClient();
			var requests = HttpRequest.newBuilder().uri(URI.create(W3W_URL)).build();
			response = clients.send(requests, BodyHandlers.ofString());
			W3W W3Wobject = new Gson().fromJson(response.body(), W3W.class);

			database[i][0] = address;
			database[i][1] = "" + W3Wobject.coordinates.lat;
			database[i][2] = "" + W3Wobject.coordinates.lng;
			database[i][3] = listOFsensors.get(i).getReading(); 
			database[i][4] = "" + listOFsensors.get(i).getBattery();		
		}
		return database;
	}
	
	/*
	 * Method to download the data of no-fly buildings from the web server 
	 * 
	 * @return a Json string
	 */
	
	public String getNoFlyZone() throws IOException, InterruptedException {
		String no_fly_zone_jsonURL = App.no_fly_zone_jsonURL;
		var client = HttpClient.newHttpClient();
		var request = HttpRequest.newBuilder().uri(URI.create(no_fly_zone_jsonURL)).build();
		var response = client.send(request, BodyHandlers.ofString());
		return response.body();
	}
	
	/*
	 * Method to get the features of sensors
	 * 
	 * @param linePoints - a list of points to read
	 * 
	 * @param database - a 2D array of strings that stores information of all sensors to read
	 * 
	 * @return a list of features that stores the features of all sensors
	 */
	
	public List<Feature> getFeatures(List<Point> linePoints, String[][] database) {
		List<Feature> features = new ArrayList<Feature>();
		LineString line = LineString.fromLngLats(linePoints);
		Feature f = Feature.fromGeometry((Geometry) line);
		features.add(f);

		for (int i = 0; i < Properties.getNumSensors(); i++) {
			Sensor sensor = new Sensor(database[i][0], database[i][4], database[i][3]);
			double lat = Double.parseDouble(database[i][1]);
			double lng = Double.parseDouble(database[i][2]);
			Point marker = Point.fromLngLat(lng, lat);
			Feature feature = Feature.fromGeometry((Geometry) marker);
			feature.addStringProperty("location", database[i][0]);
			feature.addStringProperty("rgb-string", sensor.getColor(database[i][3], database[i][4]));
			feature.addStringProperty("marker-color", sensor.getColor(database[i][3], database[i][4]));
			feature.addStringProperty("marker-symbol", sensor.getSymbol(database[i][3], database[i][4]));
			features.add(feature);
		}
		return features;
	}
	
	/*
	 * Method to get the feature collection from a list of features
	 * 
	 * @param linePoints - a list of points to read
	 * 
	 * @param database - a 2D array of strings that stores information of all sensors to read
	 * 
	 * @param features - a list features to read
	 * 
	 * @return a FeatureCollection that stores the features all the features on the map
	 */
	
	public FeatureCollection getFeatureCollection(List<Point> linePoints, String[][] database, List<Feature> features) {
		FeatureCollection fc = FeatureCollection.fromFeatures(features);
		return fc;
	}
	
	/*
	 * Method to get a list of points
	 * 
	 * @param day, month, year, latitude, longitude, seed, port  - the specified command-line arguments
	 * 
	 * @return a list of points that in the flight path
	 */
	
	public List<Point> getLinePoints(String day, String month, String year, String latitude, String longitude,
			String seed, String port, List<String[]> finalFlightPath) throws IOException, InterruptedException {
		List<Point> linePoints = new ArrayList<Point>();
		for (String[] path : finalFlightPath) {
			double lng = Double.parseDouble(path[1]);
			double lat = Double.parseDouble(path[2]);
			Point point = Point.fromLngLat(lng, lat);
			linePoints.add(point);
		}
		return linePoints;
	}
	
	/*
	 * Method to convert a list of array of strings to a string
	 * 
	 * @param paths - the flight path in the format of list of strings
	 * 
	 * @return the flight path as a string
	 */
	
	public String convertStr(List<String[]> paths) {
		String finalStr = "";
		for (String[] path : paths) {
			finalStr += String.join(",", path) + "\n";
		}
		return finalStr;
	}
	
	/*
	 * Method to write the output into a geojson file
	 * 
	 * @param fc - a feature collection that stores all information of features
	 * 
	 * @param day, month, year - the specified command-line arguments
	 * 
	 * @return
	 */
	
	public void toJson(FeatureCollection fc, String day, String month, String year)  {
		try {
			var file = new FileWriter("readings-" + day + "-" + month + "-" + year + ".geojson");
			file.write(fc.toJson());
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Method to write the output flight path into a text file
	 * 
	 * @param str - a string that stores the flight path of the drone
	 * 
	 * @param day, month, year - the specified command-line arguments
	 * 
	 * @return
	 */
	
	public void wirteTxtFile(String str, String day, String month, String year) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(String.format("flightpath-%s-%s-%s.txt", day, month, year), "UTF-8");
		writer.write(str);
		writer.close();
	}
	
	/* Methods to get and set the specified day, month, longitude, latitude, year, port and seed
	 * 
	 * @param day, month, year, latitude, longitude, seed, port  - the specified command-line arguments
	 * 
	 */

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getSeed() {
		return seed;
	}

	public void setSeed(String seed) {
		this.seed = seed;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

}
