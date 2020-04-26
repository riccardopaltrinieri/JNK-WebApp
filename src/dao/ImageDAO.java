package dao;

import beans.User;

import javax.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.NoSuchElementException;


public class ImageDAO {

	private String dbLocation = "C:\\Users\\ricky\\Documents\\GitHub\\tiwproject2020\\WebContent\\images\\profilePictures\\";
	
	public ImageDAO() {
		super();
	}
	
	public void setUserImage(User user, Part image) {

		File file = new File(dbLocation + user.getUsername() + ".jpg");
		if(file.exists()) file.delete();
		
		if(image != null) {
			try (InputStream input = image.getInputStream()) {
			    Files.copy(input, file.toPath());
			    
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				File defaultAvatar = new File(dbLocation + "Default.jpg");
			    Files.copy(defaultAvatar.toPath(), file.toPath());
			    
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String getUserImage(User user) {
		
		File file = new File(dbLocation + user.getUsername() + ".jpg");
		if(file.exists()) return file.toString();
		else {
			throw new NoSuchElementException("There isn't any profile picture for the user");
		}
	}

}
