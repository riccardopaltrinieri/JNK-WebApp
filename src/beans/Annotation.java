package beans;

import enumerations.Validity;

import java.sql.Date;

import enumerations.Level;


public class Annotation {
	
	private String author;
	private Validity validity;
	private Level trust;
	private String notes;
	private Date date;
	
	
	public Annotation(String author, int validity, String trust, String notes) {
		this.author = author;
		this.validity = Validity.fromInteger(validity);
		this.trust = Level.valueOf(trust);
		this.notes = notes;
	}
	
	public Validity getValidity() {
		return validity;
	}
	public void setValidity(String validity) {
		this.validity = Validity.valueOf(validity);
	}
	public Level getTrust() {
		return trust;
	}
	public void setTrust(String trust) {
		this.trust = Level.valueOf(trust);
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
