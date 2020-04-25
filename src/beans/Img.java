package beans;

import java.util.Date;

import enumerations.Level;

public class Img {

	private double latitude;
	private double longitude;
	private String path;
	private String city;
	private String region;
	private String source;
	private Date date;
	private Level resolution;
	
	public Img(String path) {
		this.path = path;
	}

	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Level getResolution() {
		return resolution;
	}
	public void setResolution(Level resolution) {
		this.resolution = resolution;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
}
