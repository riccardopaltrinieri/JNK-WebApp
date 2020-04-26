package beans;

import enumerations.State;

public class Campaign {
	private String name;
	private String customer;
	private String owner;
	private State state;
	
	
	public Campaign(String name, String customer, String owner, State state) {
		this.name = name;
		this.customer = customer;
		this.owner = owner;
		this.state = state;
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
	
}
