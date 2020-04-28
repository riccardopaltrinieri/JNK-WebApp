package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Campaign;
import beans.Image;
import beans.User;
import enumerations.State;

public class CampaignDAO {

	private Connection connection;
	
	public CampaignDAO(Connection connection) {
		this.connection = connection;
	}

	public Campaign createNewCampaign(User user, String name, String customer) throws SQLException {
		String query = "INSERT INTO jnk.jnk_campaigns (name, customer, state, num_images, id_owner) VALUES (?, ?, ?, ?, "
				+ "(SELECT id FROM jnk.jnk_users WHERE username = ?))";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {

			pstatement.setString(1, name);
			pstatement.setString(2, customer);
			pstatement.setString(3, State.Created.toString());
			pstatement.setInt(4, 0);
			pstatement.setString(5, user.getUsername());
			pstatement.executeUpdate();
			
			return new Campaign(name, customer, user.getUsername(), State.Created, 0);
		}
		
	}

	public List<Campaign> getManagerCampaigns(User user) throws SQLException {
		
		List<Campaign> cmps = new ArrayList<>();
		
		String query = "SELECT name, customer, state, num_images FROM jnk.jnk_campaigns WHERE id_owner = (SELECT id FROM jnk.jnk_users WHERE username = ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, user.getUsername());
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					String name = result.getString("name");
					String customer = result.getString("customer");
					String owner = user.getUsername();
					State state = State.valueOf(result.getString("state"));
					int numImages = result.getInt("num_images");
					Campaign campaign = new Campaign(name, customer, owner, state, numImages);
					cmps.add(campaign);
				}
				return cmps;
			}
		}
	}

	public Campaign getCampaign(String campaign) throws SQLException {
		String query = "SELECT c.name, u.name, customer, state, num_images FROM jnk.jnk_campaigns as c JOIN jnk.jnk_users as u ON c.id_owner = u.id WHERE c.name = ? ";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {

			pstatement.setString(1, campaign);
			pstatement.execute();

			try (ResultSet result = pstatement.executeQuery();) {
				result.next();
				String customer = result.getString("customer");
				String owner = result.getString("u.name");
				State state = State.valueOf(result.getString("state"));
				int numImages = result.getInt("num_images");
				return new Campaign(campaign, customer, owner, state, numImages);
			}
		}
	}

	public List<Campaign> getUserCampaigns(User user, boolean alreadyAnnotated) throws SQLException {

		List<Campaign> cmps = new ArrayList<>();
		String query;
		
		if(alreadyAnnotated)
			query = "SELECT name, customer, num_images FROM jnk.jnk_campaigns WHERE state = ? and "
					+ "id IN ("
					+ "SELECT id_camp FROM jnk_annotations WHERE id_user = ("
					+ "SELECT id FROM jnk_users WHERE username = ? ))";
		else
			query = "SELECT name, customer, num_images FROM jnk.jnk_campaigns WHERE state = ? and "
					+ "id NOT IN ("
					+ "SELECT id_camp FROM jnk_annotations WHERE id_user = ("
					+ "SELECT id FROM jnk_users WHERE username = ? ))";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, "Started");
			pstatement.setString(2, user.getUsername());
			
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					String name = result.getString("name");
					String customer = result.getString("customer");
					int numImages = result.getInt("num_images");
					Campaign campaign = new Campaign(name, customer, "", State.Started, numImages);
					cmps.add(campaign);
				}
				return cmps;
			}
		}
	}


	public void newImage(Campaign campaign, Image newImage) throws SQLException {
		String query = "UPDATE jnk_campaigns SET num_images = ? WHERE name = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {

			pstatement.setInt(1, campaign.getNumImages());
			pstatement.setString(2, campaign.getName());
			pstatement.executeUpdate();
			
		}
		
		query = "INSERT INTO jnk.jnk_images (name, latitude, longitude, city, region, source, resolution,  "
				+ "id_campaign) VALUES (?, ?, ?, ? ,?, ?, ?, (SELECT id FROM jnk.jnk_campaigns WHERE name = ?))";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {

			pstatement.setInt(1, campaign.getNumImages());
			pstatement.setString(2, newImage.getLatitude());
			pstatement.setString(3, newImage.getLongitude());
			pstatement.setString(4, newImage.getCity());
			pstatement.setString(5, newImage.getRegion());
			pstatement.setString(6, newImage.getSource());
			pstatement.setString(7, String.valueOf(newImage.getResolution()));
			pstatement.setString(8, campaign.getName());
			pstatement.executeUpdate();
			
		}
	}

	public Campaign updateCampaign(Campaign campaign, String action) throws SQLException {
		String query = "UPDATE jnk_campaigns SET state = ? WHERE name = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {

			if(action.equals("Start"))	{
				pstatement.setString(1, State.Started.toString());
				campaign.setState(State.Started);
			}
			if(action.equals("Close"))	{
				pstatement.setString(1, State.Closed.toString());
				campaign.setState(State.Closed);
			}
			pstatement.setString(2, campaign.getName());
			pstatement.executeUpdate();
		}
		return campaign;
	}
}
