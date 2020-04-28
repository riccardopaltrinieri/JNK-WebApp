package beans;

import enumerations.Level;

public class User {

	private String username;
	private String role;
	private String name;
	private String mailAddress;
	private String password;
	private Level LvlExp;
	
	
	public User(String username, String role, String name, String mailAddress, String password) {
		this.username = username;
		this.role = role;
		this.name = name;
		this.mailAddress = mailAddress;
		this.password = password;
		this.LvlExp = Level.Low;
	}
	

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMailAddress() {
		return mailAddress;
	}
	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Level getLvlExp() {
		return LvlExp;
	}
	public void setLvlExp(String lvlExp) {
		LvlExp = Level.valueOf(lvlExp);
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRole() {
		return role;
	}

}