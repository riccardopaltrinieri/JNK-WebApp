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
			
			User user = new User(username, role, name, mailAddress, password);
			return user;
		}
	}
	
	public User checkCredentials(String username, String pwd) throws SQLException {
		
		String query = "SELECT name, mail_address, password, lvl_exp, propic, role FROM jnk_users WHERE username = ? AND password = ?";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, username);
			pstatement.setString(2, pwd);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					String role = result.getString("role");
					String name = result.getString("name");
					String mailAddress = result.getString("mail_address");
					String password = result.getString("password");
					User user = new User(username, role, name, mailAddress, password);
					return user;
				}
			}
		}
	}
	

	public User editUser(String usr, String username, String name, String mailAddress, String password, String role) throws SQLException {

		String query = "UPDATE jnk.jnk_users SET username = ?, mail_address = ?, name = ?, password = ? WHERE username = ?;";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {

			pstatement.setString(1, username);
			pstatement.setString(2, mailAddress);
			pstatement.setString(3, name);
			pstatement.setString(4, password);
			pstatement.setString(5, usr);
			pstatement.executeUpdate();
			
			User user = new User(username, role, name, mailAddress, password);
			return user;
		}
	}
}
