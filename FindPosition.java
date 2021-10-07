package uk.ac.ed.inf.aqmaps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mapbox.geojson.Point;

/*
 * A class which contains several static functions for looking for the next position of a drone
 */

public class FindPosition {
	
	public static List<Point> path = new ArrayList<Point>();
	
	/*
	 * Method to compute the euclidean distance between two point
	 * 
	 * @param lng0, lat0 - the longitude and latitude of the first point 
	 * 
	 * @param lng1, lat1 - the longitude and latitude of the second point 
	 * 
	 * @return the diance of type double
	 */
	
	public static double euclideanDistance(double lng0, double lat0, double lng1, double lat1) {
		return Math.sqrt((Math.pow((lng0 - lng1), 2)) + ((Math.pow((lat0 - lat1), 2))));
	}

	/*
	 * Method to compute a new position from the current position with a given angle
	 * 
	 * @param drone_lng, drone_lat - the longitude and latitude of the current point of the drone
	 * 
	 * @param angle - the specified angle.
	 * 
	 * @return a new point after movement with input angle.
	 */

	public static Point newPosition(double drone_lng, double drone_lat, int angle) {
		double newLng = drone_lng + Properties.getEachMoveDistance() * Math.cos(Math.toRadians(angle));
		double newLat = drone_lat + Properties.getEachMoveDistance() * Math.sin(Math.toRadians(angle));
		Point newPoint = Point.fromLngLat(newLng, newLat);
		return newPoint;
	}
	
	/*
	 * Method to compute an angle between a drone and a sensor
	 * 
	 * @param drone_lng, drone_lat - the longitude and latitude of the current point of the drone
	 * 
	 * @param sensor_lng, sensor_lat - the longitude and latitude of the sensor
	 * 
	 * @return an angle of type integer 
	 */
	
	public static int getOneAngle(double drone_lng, double drone_lat, double sensor_lng, double sensor_lat) {
		double theta = Math.toDegrees(Math.atan2((sensor_lat - drone_lat), (sensor_lng - drone_lng)));
		if (theta % 10 != 0) {
			theta = Math.round(theta / 10.0) * 10;
		}
		if (theta == 360) {
			theta = theta - 360;
		}
		if (theta < 0) {
			theta = theta + 360;
		}
		return (int)theta;
	}

	/*
	 * Method compute an angle between a drone and a sensor in anti-clockwise
	 * 
	 * @param drone_lng, drone_lat - the longitude and latitude of the current point of the drone
	 * 
	 * @param sensor_lng, sensor_lat - the longitude and latitude of the sensor
	 * 
	 * @return an angle of type integer 
	 */
	
	public static int getAngleAntiClockwise(double drone_lng, double drone_lat, double sensor_lng, double sensor_lat)
			throws IOException, InterruptedException {
		NoFlyZone noflyzone = new NoFlyZone();
		int theta = getOneAngle(drone_lng, drone_lat, sensor_lng, sensor_lat);
		List<Point> path1 = new ArrayList<Point>();
		Point newPoint = newPosition(drone_lng, drone_lat, (int) theta);
		double newLng = newPoint.longitude();
		double newLat = newPoint.latitude();
		while (noflyzone.checkTouchedNoFlyZone(drone_lng, drone_lat, newLng, newLat)
				|| !inArea(newLng, newLat)
				|| path.contains(newPoint)
				|| path1.contains(newPoint)) {
			    theta += 10;
			    if (theta == 360) {
					theta = theta - 360;
				}
				if (theta < 0) {
					theta = theta + 360;
				}
			newPoint = newPosition(drone_lng, drone_lat, (int) theta);
			newLng = newPoint.longitude();
			newLat = newPoint.latitude();
		}
		path.add(newPosition(drone_lng, drone_lat, theta));
		path1.add(newPosition(drone_lng, drone_lat, theta));
		return (int) theta;
	}	
	
	/*
	 * Method compute an angle between a drone and a sensor in clockwise
	 * 
	 * @param drone_lng, drone_lat - the longitude and latitude of the current point of the drone
	 * 
	 * @param sensor_lng, sensor_lat - the longitude and latitude of the sensor
	 * 
	 * @return an angle of type integer 
	 */
	
	public static int getAngleClockwise(double drone_lng, double drone_lat, double sensor_lng, double sensor_lat)
			throws IOException, InterruptedException {
		NoFlyZone noflyzone = new NoFlyZone();
		int theta = getOneAngle(drone_lng, drone_lat, sensor_lng, sensor_lat);
		List<Point> path1 = new ArrayList<Point>();
		Point newPoint = newPosition(drone_lng, drone_lat, theta);
		double newLng = newPoint.longitude();
		double newLat = newPoint.latitude();
		while (noflyzone.checkTouchedNoFlyZone(drone_lng, drone_lat, newLng, newLat)
				|| !inArea(newLng, newLat)
				|| path.contains(newPoint)
				|| path1.contains(newPoint)) {
			theta -= 10;
			if (theta == 360) {
				theta = theta - 360;
			}
			if (theta < 0) {
				theta = theta + 360;
			}
			newPoint = newPosition(drone_lng, drone_lat, theta);
			newLng = newPoint.longitude();
			newLat = newPoint.latitude();
		}
		path.add(newPosition(drone_lng, drone_lat, theta));
		path1.add(newPosition(drone_lng, drone_lat, theta));
		return (int) theta;
	}
	
	/*
	 * Method compute the angle between a drone and a sensor which causes the least distance in all directions 
	 * 
	 * @param drone_lng, drone_lat - the longitude and latitude of the current point of the drone
	 * 
	 * @param sensor_lng, sensor_lat - the longitude and latitude of the sensor
	 * 
	 * @return an angle of type integer 
	 */
	
	public static int getMinAngle(double drone_lng, double drone_lat, double sensor_lng, double sensor_lat) throws IOException, InterruptedException {
		NoFlyZone noflyzone = new NoFlyZone(); 
		Point point = Point.fromLngLat(drone_lng, drone_lat);
		List<Integer> thetaList = getAngleList(drone_lng, drone_lat, sensor_lng, sensor_lat); 
		List<Double> distanceList = new ArrayList<Double>();	
		for (Integer angle : thetaList) {
			if (angle == 360) {
				angle = angle - 360;
			}
			Point newPoint = newPosition(drone_lng, drone_lat, angle);
			double new_lng = newPoint.longitude(); 
			double new_lat = newPoint.latitude();
			double distance = euclideanDistance(new_lng, new_lat, sensor_lng, sensor_lat);
			if (!(noflyzone.checkFlyCondition(drone_lng, drone_lat, new_lng, new_lat) && !checkInPath(new_lng, new_lat))) {
				distanceList.add(Double.MAX_VALUE);
			}	
			else {
				distanceList.add(distance);
			}
		}
		int closestIdx = distanceList.indexOf(Collections.min(distanceList));
		point = newPosition(drone_lng, drone_lat, thetaList.get(closestIdx));
		path.add(point);
		return thetaList.get(closestIdx);
	}
	
	/*
	 * Method to obtain a list of all possible angles between a drone and a sensor in 360 degrees
	 * 
	 * @param drone_lng, drone_lat - the longitude and latitude of the current point of the drone
	 * 
	 * @param sensor_lng, sensor_lat - the longitude and latitude of the sensor
	 * 
	 * @return a list of integers 
	 */
	
	public static List<Integer> getAngleList(double drone_lng, double drone_lat, double sensor_lng, double sensor_lat) {
		List<Integer> angleList = new ArrayList<Integer>();
		for (int i = 0; i < Properties.getMaxDegree(); i+=10) {
			angleList.add(i);
		}
		return angleList;
	}
	
	/*
	 * Method check if the flight path contains a point that has been reached
	 * 
	 * @param lng, lat - the longitude and latitude of a point
	 * 
	 * @return a boolean, true for yes, false for no
	 */
	
	public static boolean checkInPath(double lng, double lat) {
		Point point = Point.fromLngLat(lng, lat);
		if (path.contains(point)) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Method to check whether this point is in the drone confinement area.
	 * 
	 * @param longitude, latitude - the longitude and latitude of a point
	 * 
	 * @return true for yes, false otherwise.
	 */
	public static boolean inArea(double longitude, double latitude) {
		if ((latitude > Properties.getTop()) || (latitude < Properties.getBottom()) || (longitude > Properties.getRight()) || (longitude < Properties.getLeft())) {
			return false;
		}
		else {
			return true;
		}
	}
}
