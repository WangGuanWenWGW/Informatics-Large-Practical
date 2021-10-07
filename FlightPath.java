package uk.ac.ed.inf.aqmaps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mapbox.geojson.Point;

public class FlightPath {
	
	public List<String[]> finalFlightPath = new ArrayList<String[]>();
	
	/*
	 * Constructor to create a new flight path of the specified input parameters and
	 * select the best flight path with the smallest number of moves
	 * 
	 * @param day, month, year, latitude, longitude, seed, port - the specified
	 * command-line arguments
	 * 
	 */
	
	public FlightPath(String day, String month, String year, String latitude,
			String longitude, String seed, String port) throws IOException, InterruptedException {
		
		double startLng = Double.parseDouble(longitude); 
		double startLat = Double.parseDouble(latitude);
		List<String[]> finalFlightPath = new ArrayList<String[]>();
		List<String[]> flightpath = getFlightPath(day, month, year, latitude, longitude, seed, port);
		List<String[]> flightpathClockWise = getFlightPathClockwise(day, month, year, latitude, longitude, seed, port);
		List<String[]> flightpathAntiClockWise = getFlightPathAntiClockwise(day, month, year, latitude, longitude, seed, port);
		
		if (flightpathClockWise.size() < flightpathAntiClockWise.size() && flightpathClockWise.size() < flightpath.size()) {
			double lastLng = Double.parseDouble(flightpathClockWise.get(flightpathClockWise.size() - 1)[4]);
			double lastLat = Double.parseDouble(flightpathClockWise.get(flightpathClockWise.size() - 1)[5]);
			finalFlightPath  = getFinalPath(flightpathClockWise, lastLng, lastLat, startLng, startLat);
		}
		else if (flightpathAntiClockWise.size() < flightpathClockWise.size() && flightpathAntiClockWise.size() < flightpath.size()) {
			double lastLng = Double.parseDouble(flightpathAntiClockWise.get(flightpathAntiClockWise.size() - 1)[4]);
			double lastLat = Double.parseDouble(flightpathAntiClockWise.get(flightpathAntiClockWise.size() - 1)[5]);
			finalFlightPath  = getFinalPath(flightpathAntiClockWise, lastLng, lastLat, startLng, startLat);
		}
		else if (flightpath.size() < flightpathClockWise.size() && flightpath.size() < flightpathAntiClockWise.size()) {
			double lastLng = Double.parseDouble(flightpath.get(flightpath.size() - 1)[4]);
			double lastLat = Double.parseDouble(flightpath.get(flightpath.size() - 1)[5]);
			finalFlightPath  = getFinalPath(flightpath, lastLng, lastLat, startLng, startLat);
		}
		else if (flightpath.size() < flightpathAntiClockWise.size() && flightpath.size() == flightpathClockWise.size()) {
			double lastLng = Double.parseDouble(flightpath.get(flightpath.size() - 1)[4]);
			double lastLat = Double.parseDouble(flightpath.get(flightpath.size() - 1)[5]);
			finalFlightPath  = getFinalPath(flightpath, lastLng, lastLat, startLng, startLat);
		}
		else if (flightpathClockWise.size() < flightpathAntiClockWise.size() && flightpathClockWise.size() == flightpath.size()) {
			double lastLng = Double.parseDouble(flightpathClockWise.get(flightpathClockWise.size() - 1)[4]);
			double lastLat = Double.parseDouble(flightpathClockWise.get(flightpathClockWise.size() - 1)[5]);
			finalFlightPath  = getFinalPath(flightpathClockWise, lastLng, lastLat, startLng, startLat);
		}
		else if (flightpathAntiClockWise.size() < flightpathClockWise.size() && flightpathAntiClockWise.size() == flightpath.size()) {
			double lastLng = Double.parseDouble(flightpathAntiClockWise.get(flightpathAntiClockWise.size() - 1)[4]);
			double lastLat = Double.parseDouble(flightpathAntiClockWise.get(flightpathAntiClockWise.size() - 1)[5]);
			finalFlightPath  = getFinalPath(flightpathAntiClockWise, lastLng, lastLat, startLng, startLat);
		}
		else if (flightpathClockWise.size() == flightpathAntiClockWise.size() && flightpathClockWise.size() == flightpath.size()) {
			double lastLng = Double.parseDouble(flightpath.get(flightpath.size() - 1)[4]);
			double lastLat = Double.parseDouble(flightpath.get(flightpath.size() - 1)[5]);
			finalFlightPath  = getFinalPath(flightpath, lastLng, lastLat, startLng, startLat);
		}
		
		this.finalFlightPath = finalFlightPath;
	}
	
	/*
	 * Method to get the flight path in the first approach (approached are specified in the project report)
	 * 
	 * @param day, month, year, latitude, longitude, seed, port - the specified command-line arguments
	 * 
	 * @return a list of array of strings that stores the information of this flight path
	 */

	public static List<String[]> getFlightPath(String day, String month, String year, String latitude,
			String longitude, String seed, String port) throws IOException, InterruptedException {
		
		List<String[]> flightpath = new ArrayList<String[]>();
		// flightpath format : move no., before-lng, before-lat, angle, after-lng,
		Play play = new Play(day, month, year, latitude, longitude, seed, port);

		List<String> W3WList = new ArrayList<String>();
		String[][] database = play.getDatabase(day, month, year, latitude, longitude, seed, port);
		
		for (int i = 0; i < Properties.getNumSensors(); i++) {
			W3WList.add(database[i][0]);
		}

		// initial drone coordinate
		double drone_lat = Double.parseDouble(latitude);
		double drone_lng = Double.parseDouble(longitude); 
		Drone drone = new Drone(drone_lng, drone_lat);

		int moves = 1;
		while (!W3WList.isEmpty()) {	
			if (moves >= Properties.getMaxMoves()) {
				break;
			}
			String[] pathInfo = new String[7];
			pathInfo[0] = moves + "";
			pathInfo[1] = drone_lng + "";
			pathInfo[2] = drone_lat + "";

			String closestW3W = drone.getClosestSensor(drone_lng, drone_lat, W3WList, database);

			int row = Drone.getRow(database, closestW3W);
			double sensor_lat = Double.parseDouble(database[row][1]);
			double sensor_lng = Double.parseDouble(database[row][2]);
		
			int angle = FindPosition.getMinAngle(drone_lng, drone_lat, sensor_lng, sensor_lat);
			
			pathInfo[3] = angle + "";

			Point newPoint = drone.Move(drone_lng, drone_lat, angle);

			drone_lng = newPoint.longitude();
			drone_lat = newPoint.latitude();
 
			pathInfo[4] = drone_lng + "";
			pathInfo[5] = drone_lat + "";
		
			String sensorW3W = drone.closeBy(drone_lng, drone_lat, W3WList, database);

			if (sensorW3W == "Sensor not in range") {
				pathInfo[6] = "null";
			} else {
				pathInfo[6] = sensorW3W;
				W3WList.remove(sensorW3W);
			}
			flightpath.add(pathInfo);
			
			moves++;
		}
		return flightpath;
	}
	
	/*
	 * Method to get the flight path in the second approach (approached are specified in the project report)
	 * 
	 * @param day, month, year, latitude, longitude, seed, port - the specified command-line arguments
	 * 
	 * @return a list of array of strings that stores the information of this flight path
	 */
	
	public List<String[]> getFlightPathAntiClockwise(String day, String month, String year, String latitude,
			String longitude, String seed, String port) throws IOException, InterruptedException {
		List<String[]> flightpath = new ArrayList<String[]>();
		// flightpath format : move no., before-lng, before-lat, angle, after-lng,
		Play play = new Play(day, month, year, latitude, longitude, seed, port);
		List<String> W3WList = new ArrayList<String>();
		String[][] database = play.getDatabase(day, month, year, latitude, longitude, seed, port);
		
 
		for (int i = 0; i < Properties.getNumSensors(); i++) {
			W3WList.add(database[i][0]);
		}

		// initial drone coordinate
		double drone_lat = Double.parseDouble(latitude);
		double drone_lng = Double.parseDouble(longitude); 
		Drone drone = new Drone(drone_lng, drone_lat);

		int moves = 1;
		while (!W3WList.isEmpty()) {	
			if (moves >= Properties.getMaxMoves()) {
				break;
			}
			String[] pathInfo = new String[7];
			pathInfo[0] = moves + "";
			pathInfo[1] = drone_lng + "";
			pathInfo[2] = drone_lat + "";

			String closestW3W = drone.getClosestSensor(drone_lng, drone_lat, W3WList, database);

			int row = Drone.getRow(database, closestW3W);
			double sensor_lat = Double.parseDouble(database[row][1]);
			double sensor_lng = Double.parseDouble(database[row][2]);
		
			int angle = FindPosition.getAngleAntiClockwise(drone_lng, drone_lat, sensor_lng, sensor_lat);
			
			pathInfo[3] = angle + "";

			Point newPoint = drone.Move(drone_lng, drone_lat, angle);

			drone_lng = newPoint.longitude();
			drone_lat = newPoint.latitude();
 
			pathInfo[4] = drone_lng + "";
			pathInfo[5] = drone_lat + "";
		
			String sensorW3W = drone.closeBy(drone_lng, drone_lat, W3WList, database);

			if (sensorW3W == "Sensor not in range") {
				pathInfo[6] = "null";
			} else {
				pathInfo[6] = sensorW3W;
				W3WList.remove(sensorW3W);
			}
			flightpath.add(pathInfo);
			moves++;
		}
		return flightpath;
	}
	
	/*
	 * Method to get the flight path in the third approach (approached are specified in the project report)
	 * 
	 * @param day, month, year, latitude, longitude, seed, port - the specified command-line arguments
	 * 
	 * @return a list of array of strings that stores the information of this flight path
	 */
	
	public List<String[]> getFlightPathClockwise(String day, String month, String year, String latitude,
			String longitude, String seed, String port) throws IOException, InterruptedException {
		List<String[]> flightpath = new ArrayList<String[]>();
		// flightpath format : move no., before-lng, before-lat, angle, after-lng,
		Play play = new Play(day, month, year, latitude, longitude, seed, port);
		List<String> W3WList = new ArrayList<String>();
		String[][] database = play.getDatabase(day, month, year, latitude, longitude, seed, port);
		
 
		for (int i = 0; i < Properties.getNumSensors(); i++) {
			W3WList.add(database[i][0]);
		}

		// initial drone coordinate
		double drone_lat = Double.parseDouble(latitude);
		double drone_lng = Double.parseDouble(longitude); 
		Drone drone = new Drone(drone_lng, drone_lat);

		int moves = 1;
		while (!W3WList.isEmpty()) {	
			if (moves >= Properties.getMaxMoves()) {
				break;
			}
			String[] pathInfo = new String[7];
			pathInfo[0] = moves + "";
			pathInfo[1] = drone_lng + "";
			pathInfo[2] = drone_lat + "";

			String closestW3W = drone.getClosestSensor(drone_lng, drone_lat, W3WList, database);

			int row = Drone.getRow(database, closestW3W);
			double sensor_lat = Double.parseDouble(database[row][1]);
			double sensor_lng = Double.parseDouble(database[row][2]);
		
			int angle = FindPosition.getAngleClockwise(drone_lng, drone_lat, sensor_lng, sensor_lat);
	
			pathInfo[3] = angle + "";
			

			Point newPoint = drone.Move(drone_lng, drone_lat, angle);

			drone_lng = newPoint.longitude();
			drone_lat = newPoint.latitude();
 
			pathInfo[4] = drone_lng + "";
			pathInfo[5] = drone_lat + "";
		
			String sensorW3W = drone.closeBy(drone_lng, drone_lat, W3WList, database);

			if (sensorW3W == "Sensor not in range") {
				pathInfo[6] = "null";
			} else {
				pathInfo[6] = sensorW3W;
				W3WList.remove(sensorW3W);
			}
			flightpath.add(pathInfo);
			
			moves++;
		}
		return flightpath;
	}
	
	/*
	 * Method to concatenate the flight path for the drone to read all the sensors
	 * and the flight path for the drone to return to the starting point
	 * 
	 * @param formerPath - the flight path for the drone to read all the sensors
	 * 
	 * @param lng, lat - the longitude and latitude of the last point for the drone before it returns to where it starts
	 * 
	 * @param start_lng - start_lat - the longitude and latitude of the point where the drone starts to fly
	 *
	 * @return a list of array of strings that stores the information of the final flight path
	 */

	public List<String[]> getFinalPath(List<String[]> formerPath, double lng, double lat, double start_lng, double start_lat)
			throws IOException, InterruptedException {
		int theta = 0;
		double newLng = 0;
		double newLat = 0;
		List<String[]> flightPath = new ArrayList<String[]>();
		
		Drone drone = new Drone(start_lng, start_lat);
		int moves = formerPath.size() + 1;
		
		while (!drone.checkRange(lng, lat, start_lng, start_lat)) {
			if (moves >= Properties.getMaxMoves()) {
				break;
			}
			String[] pathInfo = new String[7];
			pathInfo[0] = moves + "";
			pathInfo[1] = lng + "";
			pathInfo[2] = lat + "";
			int angle = FindPosition.getAngleClockwise(lng, lat, start_lng, start_lat);
			
			pathInfo[3] = angle + "";

			Point location = drone.Move(lng, lat, angle);
		
			lng = location.longitude();
			lat = location.latitude();
			
			pathInfo[4] = lng + "";
			pathInfo[5] = lat + "";
			pathInfo[6] = "null";
			flightPath.add(pathInfo);
			moves++;
			theta = angle;
			newLng = lng;
			newLat = lat;
		}
		String[] lastPathInfo = new String[7];
		lastPathInfo[0] = moves + "" ;
		lastPathInfo[1] = newLng + "";
		lastPathInfo[2] = newLat + "";
		lastPathInfo[3] = theta + "";
		Point p = FindPosition.newPosition(newLng, newLat, theta);
		lastPathInfo[4] = p.longitude() + "";
		lastPathInfo[5] = p.latitude() + "";
		lastPathInfo[6] = "null";
		flightPath.add(lastPathInfo);
		
		flightPath = Stream.of(formerPath, flightPath).flatMap(Collection::stream)
				.collect(Collectors.toList());
		return flightPath;
	}
}
