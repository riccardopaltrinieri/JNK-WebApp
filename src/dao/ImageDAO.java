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

	private final String dbLocation = "C:\\Users\\ricky\\Documents\\GitHub\\tiwproject2020\\WebContent\\images\\";
	private final String profilePicturesDir = "profilePictures\\";
	
	public ImageDAO() {
		super();
	}
	
	
	public void setUserImage(User user, Part image) {

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
		
		File file = new File(dbLocation + campaign.getOwner());
		if(!file.exists()) file.mkdir();
		int num = campaign.getNumImages();
		
		if(num == 0) {
			file = new File(file.toString() + "\\" + campaign.getName());
			file.mkdir();
			
			num++;
			file = new File(file.toString()+ "\\" + num + ".jpg");
			try (InputStream input = image.getInputStream()) {
			    Files.copy(input, file.toPath());
			    
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			num++;
			file = new File(file.toString()+ "\\" + campaign.getName() + "\\" + num + ".jpg");
			try (InputStream input = image.getInputStream()) {
			    Files.copy(input, file.toPath());
			    
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		campaign.setNumImages(num);
	}
	

	public List<Image> getCampaignImages(Campaign campaign){
		
		List<Image> campaignImages = new ArrayList<>();
		
		for(int num = 1; num <= campaign.getNumImages(); num++) {
			String pathImage = dbLocation + "\\" + campaign.getOwner() + "\\" + campaign.getName() + "\\" + num + ".jpg";
			campaignImages.add(new Image(num, pathImage));
		}
		return campaignImages;
	}
	
	
	public Image getImageInfo(Campaign campaign, String imageName, Connection connection) throws SQLException {
		
		String pathImage = dbLocation + "\\" + campaign.getOwner() + "\\" + campaign.getName() + "\\" + imageName + ".jpg";
		Image image = new Image(Integer.parseInt(imageName), pathImage);
		
		String query = "SELECT latitude, longitude, city, region, source, resolution, date "
				+ "FROM jnk_images as i JOIN jnk_campaigns as c ON i.id_campaign = c.id "
				+ "WHERE c.name = ? AND i.name = ? ";
			
			try (PreparedStatement pstatement = connection.prepareStatement(query); ) {
	
				pstatement.setString(1, campaign.getName());
				pstatement.setInt(2, Integer.parseInt(imageName));
				pstatement.execute();
	
				try (ResultSet result = pstatement.executeQuery();) {
					result.next();
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
		
		File file = new File(dbLocation + profilePicturesDir + user.getUsername() + ".jpg");
		if(file.exists()) return file.toString();
		else {
			throw new NoSuchElementException("There isn't any profile picture for the user");
		}
	}

}
