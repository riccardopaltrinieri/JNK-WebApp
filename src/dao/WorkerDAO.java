package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import beans.Img;
import beans.User;
import beans.Worker;
import enumerations.Level;

public class WorkerDAO {

	private Connection con;

	public WorkerDAO(Connection connection) {
		this.con = connection;
	}

	public Worker getCredentials(String username) throws SQLException {
		
		String query = "SELECT name, mail_address, password, lvl_exp, propic, role FROM jnk_users WHERE username = ?";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, username);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					Worker wrk = new Worker(username);
					wrk.setName(result.getString("name"));
					wrk.setMailAddress(result.getString("mail_address"));
					wrk.setPassword(result.getString("password"));
					if(result.getString("role").equals("worker")) {
						wrk.setProfilePic(result.getString("name"));
						wrk.setLvlExp(result.getString("lvl_exp"));
					}
					return wrk;
				}
			}
		}
	}

	public User editUser(String usr, String username, String name, String mailAddress, String password) throws SQLException {

		String query = "UPDATE jnk.jnk_users SET username = ?, mail_address = ?, name = ?, password = ? WHERE username = ?;";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {

			pstatement.setString(1, username);
			pstatement.setString(2, mailAddress);
			pstatement.setString(3, name);
			pstatement.setString(4, password);
			pstatement.setString(5, usr);
			pstatement.executeUpdate();
			
			User user = new User(username);
			return user;
		}
	}
}
