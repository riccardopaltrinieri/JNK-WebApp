package beans;

import java.sql.Date;
import java.util.List;

import enumerations.Level;

public class Image {

	private int id;
	private int name;
	private String latitude;
	private String longitude;
	private String city;
	private String region;
	private String source;
	private Date date;
	private Level resolution;
	private boolean annotated;
	private List<Annotation> annotations;
	
	
	public Image(int name) {
		this.setName(name);
	}
	
	public boolean isAnnotated() {
		return annotated;
	}
	public void setAnnotated(boolean annotated) {
		this.annotated = annotated;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
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
	public void setResolution(String resolution) {
		this.resolution = Level.valueOf(resolution);
	}
	public List<Annotation> getAnnotations() {
		return annotations;
	}
	public void setAnnotations(List<Annotation> annotations) {
		this.annotations = annotations;
	}
	public int getName() {
		return name;
	}
	public void setName(int name) {
		this.name = name;
	}
}
