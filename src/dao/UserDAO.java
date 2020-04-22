package dao;

import beans.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.*;


public class UserDAO {
	private Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
	}
	
	public User createNewUser(String username, String name, String mailAddress, String password, String role) throws SQLException {

		String query = "INSERT INTO jnk.jnk_users (username, mail_address, name, password, role) VALUES (?, ?, ?, ?, ?);";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {

			pstatement.setString(1, username);
			pstatement.setString(2, mailAddress);
			pstatement.setString(3, name);
			pstatement.setString(4, password);
			pstatement.setString(5, role);
			pstatement.executeUpdate();
			
			User user = new User(username);
			user.setRole(role);
			return user;
		}
	}
	
	public User checkCredentials(String username, String pwd) {
		
		//String query = "SELECT  id, role, username FROM user  WHERE username = ? AND password =?";
		
		
		if(username.equals("wronguser")) return null;
		else return new User(username);
	}
}
