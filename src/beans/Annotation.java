package beans;

import enumerations.Validity;
import enumerations.Level;


public class Annotation {
	
	private Validity validity;
	private Level trust;
	private String Notes;
	
	
	public Validity getValidity() {
		return validity;
	}
	public void setValidity(String validity) {
		this.validity = Validity.valueOf(validity);
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
