package beans;

import enumerations.Level;

public class Worker extends User {

	private String name;
	private String mailAddress;
	private String password;
	private Level LvlExp;
	private Img profilePic;
	
	public Worker(String username) {
		super(username);
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
	public Img getProfilePic() {
		return profilePic;
	}
	public void setProfilePic(String propic) {
		//TODO
	}

}
