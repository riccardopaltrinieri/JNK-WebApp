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
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


public class ImageDAO {

	private static final String dbLocation = "C:\\Users\\ricky\\Documents\\GitHub\\tiwproject2020\\WebContent\\WEB-INF\\";
	private static final String profilePicturesDir = "images\\profilePictures\\";
	private static final String campaignPicturesDir = "images\\campaigns\\";
	private static final String webProfilePicturesDir = "/images/profilePictures/";
	private static final String webCampaignPicturesDir = "/images/campaigns/";
	
	public ImageDAO() {
		super();
	}
	
	
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
	

	public List<Image> getCampaignImages(Campaign campaign){
		
		List<Image> campaignImages = new ArrayList<>();
		
		for(int num = 1; num <= campaign.getNumImages(); num++) {
			String pathImage = webCampaignPicturesDir + campaign.getOwner() + "/" + campaign.getName() + "/" + num + ".jpg";
			campaignImages.add(new Image(num, pathImage));
		}
		return campaignImages;
	}
	
	
	public Image getImageInfo(Campaign campaign, String imageName, Connection connection) throws SQLException {

		String pathImage = webCampaignPicturesDir + campaign.getOwner() + "/" + campaign.getName() + "/" + imageName + ".jpg";
		Image image = new Image(Integer.parseInt(imageName), pathImage);
		
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
	
	
	public String getUserImage(User user) {
		
		File file = new File(webProfilePicturesDir + user.getUsername() + ".jpg");
		if(file.exists()) return file.toString();
		else {
			throw new NoSuchElementException("There isn't any profile picture for the user");
		}
	}

}
