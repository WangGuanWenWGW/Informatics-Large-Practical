package uk.ac.ed.inf.aqmaps;

/*
 * A class to represent a sensor and contains some information of the sensor
 */

public class Sensor {
	
	private String location;
	private String battery;
	private String reading;
 
	/*
	 * Constructor to create a new sensor of the specified location, battery and reading
	 * 
	 * @param location, battery, reading - the key properties of a sensor
	 * 
	 */
	
	public Sensor(String location, String battery, String reading) {
		this.setLocation(location);
		this.setBattery(battery);
		this.setReading(reading);
	}
	
	/*
	 * Methods to find the RGB string for the input reading and battery
	 * 
	 * @param reading, battery - the reading and the battery of sensor Drone
	 * 
	 * return a string that refers to a RGB string (color)
	 */
	public String getColor(String reading, String battery) {
		if (this.getReading().equals("NaN") || this.getReading().equals("null")) {
			return "#000000";
		}
		if (Double.parseDouble(this.getBattery()) < 10) {
			return "#000000";
		}
		double x = Double.parseDouble(this.getReading());
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
			return "#aaaaaa";
		}
	}
	
	/*
	 * Methods to find the symbol for the input reading and battery
	 * 
	 * @param reading, battery  - the reading and the battery of sensor Drone
	 * 
	 * return a string that refers to a symbol 
	 */
	public String getSymbol(String reading, String battery) {
		if (this.getReading().equals("NaN") || this.getReading().equals("null")) {
			return "cross";
		}
		if (Double.parseDouble(this.getBattery()) < 10) {
			return "cross";
		}
		double x = Double.parseDouble(this.getReading());
		if (x < 32 && x >= 0) {
			return "lighthouse";
		} else if (x < 64 && x >= 32) {
			return "lighthouse";
		} else if (x < 96 && x >= 64) {
			return "lighthouse";
		} else if (x < 128 && x >= 96) {
			return "lighthouse";
		} else if (x < 160 && x >= 128) {
			return "danger";
		} else if (x < 192 && x >= 160) {
			return "danger";
		} else if (x < 224 && x >= 192) {
			return "danger";
		} else if (x < 256 && x >= 224) {
			return "danger";
		}
		else {
			return null;
		}
	}
	
	/*
	 * Methods to get and set the specified reading, battery and location
	 * 
	 * @param reading, battery, location - the reading, battery and location of this sensor
	 * 
	 */
	public String getReading() {
		return reading;
	}

	public void setReading(String reading) {
		this.reading = reading;
	}

	public String getBattery() {
		return battery;
	}

	public void setBattery(String battery) {
		this.battery = battery;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
