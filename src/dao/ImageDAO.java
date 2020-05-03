package dao;

import beans.Campaign;
import beans.Image;
import beans.User;
import javax.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * An Object that can be used by the user to extract all the info about 
 * {@link Image} from a database. It uses the static database location :
 * {@value #dbLocation}
 * in the File System.
 * 
 */
public class ImageDAO {

	private static final String dbLocation = "C:\\Users\\ricky\\Documents\\GitHub\\tiwproject2020\\WebContent\\WEB-INF\\images\\";
	private static final String profilePicturesDir = "profilePictures\\";
	private static final String campaignPicturesDir = "campaigns\\";

	/**
	 * Construct the DAO with the database location.
	 * @param connection to a specific database
	 */
	public ImageDAO() {
		super();
	}
	
	/**
	 * Create a new profile picture in the database named like the username of the user (which is unique).
	 * If the user has just been created and the "image" is null the profile picture set is the default one.
	 * @param user who own the profile picture
	 * @param image 
	 */
	public void addUserImage(User user, Part image) {

		File file = new File(dbLocation + profilePicturesDir + user.getUsername() + ".jpg");
		if(file.exists()) file.delete();
		
		if(image != null) {
			try (InputStream input = image.getInputStream()) {
			    Files.copy(input, file.toPath());
			    
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				File defaultAvatar = new File(dbLocation + profilePicturesDir + "Default.jpg");
			    Files.copy(defaultAvatar.toPath(), file.toPath());
			    
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Adds an image to the campaign directory naming it with the number of the image.
	 * If the directory doesn't exist it creates one named like the campaign.
	 * 
	 * To add the image to the sql database call also {@link CampaignDAO#newImage(Campaign, Image)}
	 * @param campaign where to add the image
	 * @param image to add
	 */
	public void addCampaignImage(Campaign campaign, Part image) {
		
		try (InputStream input = image.getInputStream()) {
			
			File file = new File(dbLocation + campaignPicturesDir + campaign.getOwner() + "\\" + campaign.getName());
			if(!file.exists()) file.mkdirs();
			
			int num = campaign.getNumImages() + 1;
			
			file = new File(file.toString()+ "\\" + num + ".jpg");
		    Files.copy(input, file.toPath());
		    campaign.setNumImages(num);
		    
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets all the info about an Image and save them in the returned {@link Image}
	 * @param campaign where the image is
	 * @param imageName number of the image
	 * @param connection to the database where the data are stored
	 * @return an {@link Image} object with all the data needed
	 * @throws SQLException
	 */
	public Image getImageInfo(Campaign campaign, String imageName, Connection connection) throws SQLException {

		Image image = new Image(Integer.parseInt(imageName));
		
		String query = "SELECT id, latitude, longitude, city, region, source, resolution, date "
					 + "FROM jnk_images "
					 + "WHERE id_campaign = ? AND name = ? ";
			
			try (PreparedStatement pstatement = connection.prepareStatement(query); ) {
	
				pstatement.setInt(1, campaign.getId());
				pstatement.setInt(2, Integer.parseInt(imageName));
				pstatement.execute();
	
				try (ResultSet result = pstatement.executeQuery();) {
					result.next();
					image.setId(result.getInt("id"));
					image.setLatitude(result.getString("latitude"));
					image.setLongitude(result.getString("longitude"));
					image.setCity(result.getString("city"));
					image.setRegion(result.getString("region"));
					image.setSource(result.getString("source"));
					image.setResolution(result.getString("resolution"));
				}
			}
		return image;
	}
	
	/**
	 * Gets the path of a campaign image in the File System using the database 
	 * absolute path and the name of the campaign and of the owner
	 * @param requestedImage the number of the requested image
	 * @param campaign of the campaign which contains the image
	 * @return
	 */
	public File getCampaignImage(String requestedImage, Campaign campaign)  {
		
		String pathImage = campaignPicturesDir + campaign.getOwner() + "\\" + campaign.getName() + "\\" + requestedImage + ".jpg";
		
		return new File(dbLocation, pathImage);
	}

	/**
	 * Gets the path of the user profile picture in the File System using the
	 * database absolute path and the username of the user
	 * @param username of the user 
	 * @return
	 */
	public File getUserImage(String username) {

		String pathImage = profilePicturesDir + "\\" + username + ".jpg";
		
		return new File(dbLocation, pathImage);
	}

	/**
	 * If a user change the username this change also the name of the directory 
	 * with the campaign if he's a manager, or the name of the profile picture if
	 * he's a worker 
	 * @param oldUser
	 * @param newUsername
	 */
	public void editUserImages(User oldUser, String newUsername) {
		
		if(oldUser.getRole().equals("worker")) {
			File oldImage = new File(dbLocation + profilePicturesDir + oldUser.getUsername() + ".jpg");
			File newImage = new File(dbLocation + profilePicturesDir + newUsername + ".jpg");
		    oldImage.renameTo(newImage);
		} else {
			File oldCampaigns = new File(dbLocation + campaignPicturesDir + "\\" + oldUser.getUsername());
			File newCampaigns = new File(dbLocation + campaignPicturesDir + "\\" + newUsername);
			oldCampaigns.renameTo(newCampaigns);
		}
	}

}
