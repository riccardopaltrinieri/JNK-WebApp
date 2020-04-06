package beans;

import java.util.Date;

import enumerations.Validity;
import enumerations.Level;


public class Annotation {
	
	private Date date;
	private Validity validity;
	private Level trust;
	private String Notes;
	
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Validity getValidity() {
		return validity;
	}
	public void setValidity(Validity validity) {
		this.validity = validity;
	}
	public Level getTrust() {
		return trust;
	}
	public void setTrust(Level trust) {
		this.trust = trust;
	}
	public String getNotes() {
		return Notes;
	}
	public void setNotes(String notes) {
		Notes = notes;
	}

}
