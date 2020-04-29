package beans;

import enumerations.State;

public class Campaign {
	
	private int id;
	private String name;
	private String customer;
	private String owner;
	private State state;
	private int numImages;
	
	public Campaign(String name, String customer, String owner, State state, int numImages) {
		this.name = name;
		this.customer = customer;
		this.owner = owner;
		this.state = state;
		this.setNumImages(numImages);
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public int getNumImages() {
		return numImages;
	}
	public void setNumImages(int numImages) {
		this.numImages = numImages;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
}
