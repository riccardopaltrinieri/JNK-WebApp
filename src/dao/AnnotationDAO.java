package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import beans.Annotation;
import beans.Campaign;
import beans.User;

public class AnnotationDAO {

	private Connection connection;
	
	
	public AnnotationDAO(Connection connection) {
		this.connection = connection;
	}


	public void addAnnotation(Annotation annotation, User user, Campaign campaign, int imageName) throws SQLException {
		
		String query = "INSERT INTO jnk.jnk_annotations(validity, text, trust, id_user, id_camp, id_image)"
				+ "VALUES (?, ?, ?, "
				+ "(SELECT u.id FROM jnk_users as u WHERE username = ?), "
				+ "(SELECT c.id FROM jnk_campaigns as c WHERE name = ?), "
				+ "(SELECT i.id FROM jnk_images as i, jnk_campaigns as c WHERE c.name = ? and c.id = id_campaign and i.name = ?))";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {

			pstatement.setInt(1, annotation.getValidity().ordinal());
			pstatement.setString(2, annotation.getNotes());
			pstatement.setString(3, annotation.getTrust().toString());
			pstatement.setString(4, user.getUsername());
			pstatement.setString(5, campaign.getName());
			pstatement.setString(6, campaign.getName());
			pstatement.setInt(7, imageName);
			
			pstatement.executeUpdate();
		}
		
	}

}
