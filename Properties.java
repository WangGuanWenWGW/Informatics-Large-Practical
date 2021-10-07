package uk.ac.ed.inf.aqmaps;

/*
 * A class which contains some properties and several static getters and setters functions
 */

public class Properties { 
	// Drone confinement areas boundaries
    private static final double TOP = 55.946233;
    private static final double BOTTOM = 55.942617;
    private static final double LEFT = -3.192473;
    private static final double RIGHT = -3.184319;
    
    // The maximum distance for a drone to receive readings from a sensor  
    private static final double MAX_DISTANCE = 0.0002;
    
    // The distance for a drone to move each time
    private static final double EACH_MOVE_DISTANCE = 0.0003;
    
    // The maximum number of moves for a drone
    private static final int MAX_MOVES = 150;
    
    // The total number of sensors to be read today
    private static final int NUM_SENSORS = 33;
    
    // The theoretical maximum degree of an angle for a drone to fly
    private static final int MAX_DEGREE = 360;

	/*
	 * Methods to get and set the variables
	 * 
	 */
    
	public static double getBottom() {
		return BOTTOM;
	}

	public static double getLeft() {
		return LEFT;
	}

	public static double getTop() {
		return TOP;
	}

	public static double getRight() {
		return RIGHT;
	}

	public static double getMaxDistance() {
		return MAX_DISTANCE;
	}

	public static double getEachMoveDistance() {
		return EACH_MOVE_DISTANCE;
	}

	public static int getMaxMoves() {
		return MAX_MOVES;
	}

	public static int getNumSensors() {
		return NUM_SENSORS;
	}

	public static int getMaxDegree() {
		return MAX_DEGREE;
	}
 
}
