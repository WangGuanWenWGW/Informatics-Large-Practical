package uk.ac.ed.inf.heatmap;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

/**
 * Hello world!
 *
 */
public class App {
	
	// four variables refer to the boundary of the drone confinement region
	private static double top = 55.946233;
	private static double bottom = 55.942617;
	private static double right = -3.184319;
	private static double left = -3.192473;
	
	private static double polygon_height = (double) Math.abs(top - bottom) / 10; // height of each polygon
	private static double polygon_width = (double) Math.abs(right - left) / 10; // width of each polygon
	static List<Double> xList = new ArrayList<Double>(); // list of 100 doubles
	static List<Feature> fl = new ArrayList<Feature>(); // list of features for creating a FeatureCollection
	
	// method to output a corresponding rgb-string with an input of type "int";
	public static String getRGBstring(int x) {
		if (x < 32 && x >= 0) {
			return "#00ff00";
		} else if (x < 64 && x >= 32) {
			return "#40ff00";
		} else if (x < 96 && x >= 64) {
			return "#80ff00";
		} else if (x < 128 && x >= 96) {
			return "#c0ff00";
		} else if (x < 160 && x >= 128) {
			return "#ffc000";
		} else if (x < 192 && x >= 160) {
			return "#ff8000";
		} else if (x < 224 && x >= 192) {
			return "#ff4000";
		} else if (x < 256 && x >= 224) {
			return "#ff0000";
		} else {
			return "#000000";
		}
	}

	public static void main(String[] args) throws IOException {
		// read text file into a list
		String address = args[0];
		FileInputStream fi = new FileInputStream(address);
		InputStreamReader reader = new InputStreamReader(fi);
		Scanner sc = new Scanner(reader);
		List<String> list = new ArrayList<String>();
		while(sc.hasNext()) {
			list.add(sc.next());
		}
		sc.close();
		
		// to assign values from the read list into an array of strings with size being 100
		String predix[] = new String[100];
		for (int i = 0; i < predix.length; i++) {
			for (String str : list.get(i).split(",")) {
				predix[i] = str;
			}
		}
		// to convert 100 strings into 100 integers
		int predictions[] = new int[100];
		for (int j = 0; j < predictions.length; j++) {
			predictions[j] = Integer.parseInt(predix[j]);
		}
		
		for (double y = (top - polygon_height); y > bottom; y -= polygon_height) {
			for (double x = left; x < right; x += polygon_width) {
				xList.add(x);  //list of 100 doubles referred to the corresponding position in predictions
				List<Point> polygonPoints = new ArrayList<Point>(); // points of one polygon, sized 5
				List<List<Point>> onePolygon = new ArrayList<List<Point>>(); // points of 100 polygons, size of 5x100
				// get five points for each polygon
				polygonPoints.add(Point.fromLngLat(x, y));
				polygonPoints.add(Point.fromLngLat(x + polygon_width, y));
				polygonPoints.add(Point.fromLngLat(x + polygon_width, y + polygon_height));
				polygonPoints.add(Point.fromLngLat(x, y + polygon_height));
				polygonPoints.add(Point.fromLngLat(x, y));
				// add the five points forming a polygon to a list
				onePolygon.add(polygonPoints); 
				// form a polygon with five points
				Polygon Poly = Polygon.fromLngLats(onePolygon);
				// make the produced polygon be a geometry
				Geometry g = (Geometry) Poly;
				// form a feature with the geometry
				Feature f = Feature.fromGeometry(g);
				// for-loop to locate the corresponding polygon
			    for (int i = 0; i < xList.size(); i++) {
			    	// add properties of a polygon to its corresponding feature
			    	// get each polygon colored by calling method "getRGBstring"
			    	f.addStringProperty("rgb-string", getRGBstring(predictions[i])); 
			    	f.addStringProperty("fill", getRGBstring(predictions[i]));
			        f.addNumberProperty("fill-opacity", 0.75);
				}
				fl.add(f); // assign each feature of a polygon to a list of features
			}
		}
		FeatureCollection fc = FeatureCollection.fromFeatures(fl);
		String output = fc.toJson();
	    
	    // produce a geojson output
		FileWriter file;
	    try {
	    	file = new FileWriter("heatmap.geojson");
	    	file.write(output);
	    	file.flush();
	    	file.close();
	    	System.out.println("Hahaha");
	    }
	    catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
}
