package dao;

import beans.User;


public class UserDAO {
	
	public User createNewUser(int tempID, String username, String name, String mailAddress, String password, String role) {
		
		int id = 0; //TODO creare l'user nel database e restituire l'id
		
		User user = new User(id, username);
		
		if(role.equals("manager")) user.setRole("manager");
		else if(role.equals("worker")) user.setRole("worker");
		else throw new IllegalArgumentException();
		
		return user;
	}
	
	public User checkCredentials(String username, String pwd) {
		
		//String query = "SELECT  id, role, username FROM user  WHERE username = ? AND password =?";
		
		int id = 0;
		
		if(username.equals("wronguser")) return null;
		else return new User(id, username);
	}
}
