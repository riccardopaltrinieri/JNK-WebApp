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

/**
 * An Object that can be used by the user to extract all the info about 
 * {@link Annotation} from a database. It uses the connection passed in the 
 * constructor.
 *
 */
public class AnnotationDAO {

	private Connection connection;
	
	/**
	 * Construct the DAO connecting it to the database saving the parameter connection.
	 * @param connection to a specific database
	 */
	public AnnotationDAO(Connection connection) {
		this.connection = connection;
	}


	/**
	 * Insert an annotation in the database table using the data 
	 * in the first parameter and using the Id of other parameters as external keys
	 * @param annotation containing the author, text, validity and trust of the note
	 * @param user who created the note
	 * @param campaign where the image is
	 * @param imageName where the annotation is
	 * @throws SQLException
	 */
	public void addAnnotation(Annotation annotation, User user, Campaign campaign, int imageName) throws SQLException {
		
		String query = "INSERT INTO jnk.jnk_annotations(validity, text, trust, id_user, id_camp, id_image)"
				+ "VALUES (?, ?, ?, ?, ?, "
				+ "(SELECT id FROM jnk_images WHERE id_campaign = ? and name = ?))";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {

			pstatement.setInt(1, annotation.getValidity().ordinal());
			pstatement.setString(2, annotation.getNotes());
			pstatement.setString(3, annotation.getTrust().toString());
			pstatement.setInt(4, user.getId());
			pstatement.setInt(5, campaign.getId());
			pstatement.setInt(6, campaign.getId());
			pstatement.setInt(7, imageName);
			
			pstatement.executeUpdate();
		}
		
	}

	/**
	 * Gets all the annotations from the database (with the relative info) 
	 * and save them in the returned list.
	 * @param image you want to get annotation from
	 * @throws SQLException
	 */
	public List<Annotation> getAnnotationsManager(Image image) throws SQLException {
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

	/**
	 * If a worker has already annotated an image, returns that annotation.
	 * @param image already annotated by the user
	 * @param user who want to see the note
	 * @throws SQLException
	 */
	public Annotation getAnnotationWorker(Image image, User user) throws SQLException {
		
		Annotation annotation = null;
		String query = "SELECT count(id), validity, text, date, trust "
					 + "FROM jnk_annotations WHERE id_image = ? AND id_user = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {
			
			pstatement.setInt(1, image.getId());
			pstatement.setInt(2, user.getId());
			pstatement.execute();

			try (ResultSet result = pstatement.executeQuery();) {
				result.next();
				if(result.getInt("count(id)") > 0) {
					int validity = result.getInt("validity");
					String trust = result.getString("trust");
					String notes = result.getString("text");
					Date date = result.getDate("date");
					annotation = new Annotation(user.getUsername(), validity, trust, notes);
					annotation.setDate(date);
				}
			}
		}
		return annotation;
	}

}
