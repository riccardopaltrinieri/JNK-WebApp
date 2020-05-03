package dao;

import beans.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import enumerations.Level;

import java.sql.*;


/**
 * An Object that can be used by the user to extract all the info about 
 * {@link User} from a database. It uses the connection passed in the 
 * constructor.
 */
public class UserDAO {
	private Connection con;

	/**
	 * Construct the DAO connecting it to the database saving the parameter connection.
	 * @param connection to a specific database
	 */
	public UserDAO(Connection connection) {
		this.con = connection;
	}
	
	/**
	 * Create a new user in the database with all the info passed as parameters
	 * @param username
	 * @param name
	 * @param mailAddress
	 * @param password
	 * @param role
	 * @throws SQLException
	 */
	public void createNewUser(String username, String name, String mailAddress, String password, String role) throws SQLException {

		ImageDAO img = new ImageDAO();
		String query = "INSERT INTO jnk.jnk_users (username, mail_address, name, password, role) VALUES (?, ?, ?, ?, ?);";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {

			pstatement.setString(1, username);
			pstatement.setString(2, mailAddress);
			pstatement.setString(3, name);
			pstatement.setString(4, password);
			pstatement.setString(5, role);
			pstatement.executeUpdate();
			
			// The level of experience of a new user is Low as Default
			User user = new User(username, role, name, mailAddress, password, Level.Low);
			// The profile picture of a new worker is a Default Image
			if(role.equals("worker")) img.addUserImage(user, null);
		}
	}
	
	/**
	 * Check if the user with this username (unique in the database) has a password that coincide 
	 * with pwd used by the User
	 * @param username inserted by the user in the login
	 * @param pwd password inserted by the user in the login
	 * @return the {@link User} object with all the info about the user
	 * @throws SQLException
	 */
	public User checkCredentials(String username, String pwd) throws SQLException {
		
		String query = "SELECT id, name, mail_address, password, role FROM jnk_users WHERE username = ? AND password = ?";
		
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
					Level lvlExp = getUserExperience(result.getInt("id"));
					User user = new User(username, role, name, mailAddress, password, lvlExp);
					user.setId(result.getInt("id"));
					return user;
				}
			}
		}
	}

	/**
	 * Return the level of experience of a worker based on the number of annotations
	 * he posted in the campaigns
	 * @param id
	 * @return "Low" if annotations < 5, "Medium" if 5 < annotations < 15, "High" otherwise
	 * @throws SQLException
	 */
	public Level getUserExperience(int id) throws SQLException {
		
		String query = "SELECT count(id) FROM jnk_annotations WHERE id_user = ?";
		
		try(PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setInt(1, id);
			pstatement.execute();
			try (ResultSet result = pstatement.executeQuery();) {
				result.next();
				return Level.getLevel(result.getInt("count(id)"));
			}
		}
	}

	/**
	 * Update the user row in the database with the new info and the directory in the images database
	 * if the username has changed
	 * @param usr is the old user that has to be changed
	 * @param username is the new username
	 * @param name is the new name
	 * @param mailAddress is the new mailAddress
	 * @param password is the new password
	 * @return the new {@link User} with new datas
	 * @throws SQLException
	 */
	public User editUser(User usr, String username, String name, String mailAddress, String password) throws SQLException {

		ImageDAO img = new ImageDAO();
		String query = "UPDATE jnk.jnk_users SET username = ?, mail_address = ?, name = ?, password = ? WHERE username = ?;";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			
			pstatement.setString(1, username);
			pstatement.setString(2, mailAddress);
			pstatement.setString(3, name);
			pstatement.setString(4, password);
			pstatement.setString(5, usr.getUsername());
			pstatement.executeUpdate();
		}			
		
		// the images database uses the username as name of the profile picture
		// or the directory associated with the user so it has to be updated
		if(!usr.getUsername().equals(username)) {
			img.editUserImages(usr, username);
		}
			
		User user = new User(username,  usr.getRole(), name, mailAddress, password, usr.getLvlExp());
		user.setId(usr.getId());
		return user;
	}

}
