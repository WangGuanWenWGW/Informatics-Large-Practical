package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.mapbox.geojson.Point;


/*
 * A class to represent a drone and store some information of the drone.
 */

public class Drone {
	
	private double longitude;
	private double latitude; 
	
	/*
	 * Constructor to create a new drone of the specified longitude and latitude
	 * 
	 * @param longitude, latitude  - the longitude and the latitude of this drone
	 * 
	 */
	
	public Drone(double longitude, double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	/*
	 * Methods to get and set the specified longitude and latitude
	 * 
	 * @param longitude, latitude  - the longitude and the latitude of this drone
	 * 
	 */
	
	public double getLng() { 
		return longitude;
	}

	public void setLng(double longitude) {
		this.longitude = longitude;
	}
	
	public double getLat() {
		return latitude; 
	}

	public void setLat(double latitude) {
		this.latitude = latitude;
	}
	
	/*
	 * Method to move the drone
	 * 
	 * @param drone_lng - the current longitude of the drone
	 * 
	 * @param drone_lat - the current latitude of the drone
	 * 
	 * @param angle - the angle in which the drone to move
	 * 
	 * return a point after movement
	 */
	public Point Move(double drone_lng, double drone_lat, int angle) {
		return FindPosition.newPosition(drone_lng, drone_lat, angle); 
	}

	/*
	 * Method to check if flight path has exceed 150 moves
	 * 
	 * @param moves - a counter to count on how many moves
	 * 
	 * return a boolean, true for yes, false for no
	 */
	public static boolean isEnd(int moves) {
		return (moves == Properties.getMaxMoves()); 
	}

	/*
	 * Method to find the closest drone
	 * 
	 * @param drone_lng - the current longitude of the drone
	 * 
	 * @param drone_lat - the current latitude of the drone
	 * 
	 * @param W3WList - a list of string that stores the information of sensors
	 * 
	 * @param database - a 2D array list that stores all the information from a
	 * sensor (W3W, lat, lng, reading and battery)
	 * 
	 * return a string that responds to where the closest sensor occurs
	 */
	
	 public String getClosestSensor(double drone_lng, double drone_lat, List<String> W3WList, String[][] database) { 
		  	List<Double> distances = new ArrayList<Double>(); 
		  	for (int i = 0; i < W3WList.size(); i++) { 
		  		String W3W = W3WList.get(i); 
		  		int row = getRow(database, W3W); 
		  		double sensor_lat = Double.parseDouble(database[row][1]); 
		  		double sensor_lng = Double.parseDouble(database[row][2]);
		  		distances.add(FindPosition.euclideanDistance(drone_lng, drone_lat, sensor_lng, sensor_lat)); 
		  		} 
		  	int closestIdx = distances.indexOf(Collections.min(distances)); 
		  	return W3WList.get(closestIdx); 
	  }

	/*
	 * Method to find the which row (which sensor)
	 * 
	 * @param database - a 2D array list that stores all the information from a
	 * sensor (W3W, lat, lng, reading and battery)
	 * 
	 * @param w3w - a String of What3Words of a sensor
	 * 
	 * return an integer which refers to a row where the sensor is
	 */
	public static int getRow(String[][] database, String w3w) {
		for (int row = 0; row < Properties.getNumSensors(); row++) {
			if (database[row][0] == w3w) {
				return row;
			}
		}
		return 0;
	}

	/*
	 * Method to check if the drone's location is close enough (< 0.0002) to a sensor
	 * 
	 * @param drone_lng - the current longitude of the drone
	 * 
	 * @param drone_lat - the current latitude of the drone
	 * 
	 * @param W3WList - a list of string that stores the information of sensors
	 * 
	 * @param database - a 2D array list that stores all the information from a
	 * sensor (W3W, lat, lng, reading and battery) 
	 * 
	 * return a string that responds to where the closest sensor occurs or "Sensor not in range"
	 */
	public String closeBy(double drone_lng, double drone_lat, List<String> W3WList, String[][] database) {
		List<Double> distances = new ArrayList<Double>();
		for (int i = 0; i < W3WList.size(); i++) {
			String W3W = W3WList.get(i);
			int row = getRow(database, W3W);
			double sensor_lat = Double.parseDouble(database[row][1]);
			double sensor_lng = Double.parseDouble(database[row][2]);
	  		distances.add(FindPosition.euclideanDistance(drone_lng, drone_lat, sensor_lng, sensor_lat)); 
		}
		double shortest = Collections.min(distances);
		if (shortest < Properties.getMaxDistance()) {
			return W3WList.get(distances.indexOf(shortest));
		} else {
			return "Sensor not in range";
		}
	}

	/*
	 * Method to check if the drone's location is close enough (< 0.0002) to move to
	 * the starting point
	 * 
	 * @param drone_lng - the current longitude of the drone
	 * 
	 * @param drone_lat - the current latitude of the drone
	 * 
	 * @param start_lng - the longitude of the staring point
	 * 
	 * @param start_lat - the latitude of the starting point
	 * 
	 * return a boolean, true for yes, false for no
	 */
	public boolean checkRange(double drone_lng, double drone_lat, double start_lng, double start_lat) {
		double distance = FindPosition.euclideanDistance(drone_lng, drone_lat, start_lng, start_lat);
		if (distance > Properties.getMaxDistance()) {
			return false;
		}
		return true;
	}

}
