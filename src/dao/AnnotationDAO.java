package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Annotation;
import beans.Campaign;
import beans.Image;
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


	public boolean isAnnotated(Image image, User user) throws SQLException {
		String query = "SELECT count(id) FROM jnk_annotations WHERE id_image = ? AND id_user = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {
			
			pstatement.setInt(1, image.getId());
			pstatement.setInt(2, user.getId());
			pstatement.execute();

			try (ResultSet result = pstatement.executeQuery();) {
				result.next();
				return result.getInt("count(id)") > 0;
			}
		}
	}


	public List<Annotation> getAnnotations(Image image) throws SQLException {
		List<Annotation> annotations = new ArrayList<>();
		
		String query = "SELECT u.username, a.validity, text, date, trust "
					 + "FROM jnk.jnk_annotations as a JOIN jnk.jnk_users as u "
					 + "ON a.id_user = u.id "
					 + "WHERE a.id_image = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {
				
				pstatement.setInt(1, image.getId());
				pstatement.execute();
	
				try (ResultSet result = pstatement.executeQuery();) {
					while(result.next()) {
						String author = result.getString("u.username");
						int validity = result.getInt("validity");
						String trust = result.getString("trust");
						String notes = result.getString("text");
						Date date = result.getDate("date");
						Annotation ann = new Annotation(author, validity, trust, notes);
						ann.setDate(date);
						annotations.add(ann);
					}
					return annotations;
				}
		}
	}

}
