package uk.ac.ed.inf.aqmaps;

/*
 * A class to give the What3Words information corresponding to a What3Words address
 */

public class W3W {
	String country;
	Square square;
	public static class Square{
		Southwest southwest;
		public static class Southwest{
			double lng;
			double lat;
		}
		Northeast northeast;
		public static class Northeast{
			double lng;
			double lat;
		}
	}
	
	String nearestPlace;
	Coordinates coordinates;
	public static class Coordinates{
		double lng;
		double lat;
	}
	
	String words;
	String language;
	String map;
	
}

