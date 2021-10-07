package uk.ac.ed.inf.aqmaps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

/*
 * A class to represent four no-fly buildings as polygons
 */

public class NoFlyZone {

	private String day;
	private String month;
	private String year;
	private String latitude;
	private String longitude;
	private String seed;
	private String port;

	private static double[] poly0lats;
	private static double[] poly0lngs;
	private static double[] poly1lats;
	private static double[] poly1lngs;
	private static double[] poly2lats;
	private static double[] poly2lngs;
	private static double[] poly3lats;
	private static double[] poly3lngs;

	Play play = new Play(day, month, year, latitude, longitude, seed, port);

	/*
	 * Constructor to create a new no-fly zone that consists of four no-fly zones
	 */
	
	public NoFlyZone() throws IOException, InterruptedException {

		String no_fly_zone_json = play.getNoFlyZone();
		FeatureCollection fc = FeatureCollection.fromJson(no_fly_zone_json);
		List<Polygon> pl = new ArrayList<Polygon>();
		List<Feature> fl = new ArrayList<Feature>();
		fl = fc.features();
		for (Feature feature : fl) {
			Polygon polygon = (Polygon) feature.geometry();
			pl.add(polygon);
		}

		List<List<Point>> onePoly0 = new ArrayList<List<Point>>();
		List<List<Point>> onePoly1 = new ArrayList<List<Point>>();
		List<List<Point>> onePoly2 = new ArrayList<List<Point>>();
		List<List<Point>> onePoly3 = new ArrayList<List<Point>>();

		onePoly0 = pl.get(0).coordinates();
		onePoly1 = pl.get(1).coordinates();
		onePoly2 = pl.get(2).coordinates();
		onePoly3 = pl.get(3).coordinates();

		List<Point> polygonPoint0 = new ArrayList<Point>();
		List<Point> polygonPoint1 = new ArrayList<Point>();
		List<Point> polygonPoint2 = new ArrayList<Point>();
		List<Point> polygonPoint3 = new ArrayList<Point>();

		polygonPoint0 = onePoly0.get(0);
		polygonPoint1 = onePoly1.get(0);
		polygonPoint2 = onePoly2.get(0);
		polygonPoint3 = onePoly3.get(0);

		double[] poly0lats = new double[polygonPoint0.size()];
		double[] poly0lngs = new double[polygonPoint0.size()];
		double[] poly1lats = new double[polygonPoint1.size()];
		double[] poly1lngs = new double[polygonPoint1.size()];
		double[] poly2lats = new double[polygonPoint2.size()];
		double[] poly2lngs = new double[polygonPoint2.size()];
		double[] poly3lats = new double[polygonPoint3.size()];
		double[] poly3lngs = new double[polygonPoint3.size()];

		int a = 0;
		for (Point p0 : polygonPoint0) {
			poly0lats[a] = p0.latitude();
			poly0lngs[a] = p0.longitude();
			a++;
		}

		int b = 0;
		for (Point p1 : polygonPoint1) {
			poly1lats[b] = p1.latitude();
			poly1lngs[b] = p1.longitude();
			b++;
		}

		int c = 0;
		for (Point p2 : polygonPoint2) {
			poly2lats[c] = p2.latitude();
			poly2lngs[c] = p2.longitude();
			c++;
		}

		int d = 0;
		for (Point p3 : polygonPoint3) {
			poly3lats[d] = p3.latitude();
			poly3lngs[d] = p3.longitude();
			d++;
		}

		NoFlyZone.poly0lats = poly0lats;
		NoFlyZone.poly0lngs = poly0lngs;
		NoFlyZone.poly1lats = poly1lats;
		NoFlyZone.poly1lngs = poly1lngs;
		NoFlyZone.poly2lats = poly2lats;
		NoFlyZone.poly2lngs = poly2lngs;
		NoFlyZone.poly3lats = poly3lats;
		NoFlyZone.poly3lngs = poly3lngs;
	}

	/*
	 * Method to check if two line segments intersect
	 * 
	 * @param px1, py1 - the longitude and latitude of the first point
	 * 
	 * @param px2, py2 - the longitude and latitude of second point
	 * 
	 * @param px3, py3 - the longitude and latitude of third point
	 * 
	 * @param px4, py4 - the longitude and latitude of fourth point
	 * 
	 * @return a boolean, true for intersected, false for not intersected
	 */
	
	public boolean checkIntersection(double px1, double py1, double px2, double py2, double px3, double py3, double px4,
			double py4) {
		boolean flag = false;
		double d = ((py4 - py3) * (px2 - px1) - (px4 - px3) * (py2 - py1));
		if (d != 0) {
			double r = ((py1 - py3) * (px4 - px3) - (px1 - px3) * (py4 - py3)) / d;
			double s = ((py1 - py3) * (px2 - px1) - (px1 - px3) * (py2 - py1)) / d;
			if (r >= 0 && r <= 1 && s >= 0 && s <= 1) {
				flag = true;
			}
		}
		return flag;
	}

	/*
	 * Method to check if the new point is in the no-fly zones
	 * 
	 * @param start_lng, start_lat - the longitude and latitude of current point where the drone is
	 * 
	 * @param lng, lat - the longitude and latitude of the point where the drone is about to move
	 * 
	 * @param poly_lng, poly_lat - the longitudes and latitudes of the edge points of no-fly zones
	 * 
	 * @return a boolean, true for not in the zone, false for in the zone
	 */
	
	public boolean checkFlyZone(double start_lng, double start_lat, double lng, double lat, double[] poly_lng,
			double[] poly_lat) {
		for (int i = 0; i < poly_lat.length - 1; i++) {
			if (checkIntersection(start_lng, start_lat, lng, lat, poly_lng[i], poly_lat[i], poly_lng[i + 1],
					poly_lat[i + 1])) {
				return false;
			}
		}
		return true;
	}
	
	/*
	 * Methods to check if the new point is in the no-fly zones for each of the four buildings
	 * 
	 * @param start_lng, start_lat - the longitude and latitude of current point where the drone is
	 * 
	 * @param lng, lat - the longitude and latitude of the point where the drone is about to move
	 * 
	 * @return a boolean, true for not in the zone, false for in the zone
	 */
	
	public boolean check0(double start_lng, double start_lat, double lng, double lat) {
		return checkFlyZone(start_lng, start_lat, lng, lat, poly0lngs, poly0lats);
	}

	public boolean check1(double start_lng, double start_lat, double lng, double lat) {
		return checkFlyZone(start_lng, start_lat, lng, lat, poly1lngs, poly1lats);
	}

	public boolean check2(double start_lng, double start_lat, double lng, double lat) {
		return checkFlyZone(start_lng, start_lat, lng, lat, poly2lngs, poly2lats);
	}

	public boolean check3(double start_lng, double start_lat, double lng, double lat) {
		return checkFlyZone(start_lng, start_lat, lng, lat, poly3lngs, poly3lats);
	}
	
	/*
	 * Methods to check if the drone is under the fly condition
	 * 
	 * @param drone_lng, drone_lat - the longitude and latitude of current point
	 * where the drone is
	 * 
	 * @param new_lng, new_lat - the longitude and latitude of the point where the
	 * drone is about to move
	 * 
	 * @return a boolean, true for not in the zone and within the confinement area,
	 * false for in the zone and not in the confinement area
	 */
	
	public boolean checkFlyCondition(double drone_lng, double drone_lat, double new_lng, double new_lat) {
		boolean flag = false;
		if (check0(drone_lng, drone_lat, new_lng, new_lat) && check1(drone_lng, drone_lat, new_lng, new_lat)
				&& check2(drone_lng, drone_lat, new_lng, new_lat) && check3(drone_lng, drone_lat, new_lng, new_lat)
				&& FindPosition.inArea(drone_lng, drone_lat)) {
			flag = true;
		}
		return flag;
	}
	
	/*
	 * Methods to check if the drone touches the margin of the four no-fly zones
	 * 
	 * @param drone_lng, drone_lat - the longitude and latitude of current point where the drone is
	 * 
	 * @param new_lng, new_lat - the longitude and latitude of the point where the drone is about to move
	 * 
	 * @return a boolean, true for the drone has touched the margin, false for not touched
	 */
	
	public boolean checkTouchedNoFlyZone(double drone_lng, double drone_lat, double new_lng, double new_lat) {
		boolean flag = false;
		if (!check0(drone_lng, drone_lat, new_lng, new_lat) || !check1(drone_lng, drone_lat, new_lng, new_lat)
				|| !check2(drone_lng, drone_lat, new_lng, new_lat) || !check3(drone_lng, drone_lat, new_lng, new_lat)) {
			flag = true;
		}
		return flag;
	}
}
