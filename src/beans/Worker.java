package beans;

import enumerations.Level;

public class Worker extends User{


	private Level lvlXP;
	private int profilePic;
	
	
	public Worker(int id, String username) {
		super(id, username);
	}

	public Level getLvlXP() {
		return lvlXP;
	}

	public void setLvlXP(Level lvlXP) {
		this.lvlXP = lvlXP;
	}

	public int getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(int profilePic) {
		this.profilePic = profilePic;
	}

}
