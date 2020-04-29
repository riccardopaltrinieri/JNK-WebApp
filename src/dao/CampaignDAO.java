package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Campaign;
import beans.CampaignStats;
import beans.Image;
import beans.User;
import enumerations.State;

public class CampaignDAO {

	private Connection connection;
	
	public CampaignDAO(Connection connection) {
		this.connection = connection;
	}

	public void createNewCampaign(User user, String name, String customer) throws SQLException {
		String query = "INSERT INTO jnk.jnk_campaigns (name, customer, state, num_images, id_owner) VALUES (?, ?, ?, ?, ?)";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {

			pstatement.setString(1, name);
			pstatement.setString(2, customer);
			pstatement.setString(3, State.Created.toString());
			pstatement.setInt(4, 0);
			pstatement.setInt(5, user.getId());
			pstatement.executeUpdate();
		}
		
	}

	public List<Campaign> getManagerCampaigns(User user) throws SQLException {
		
		List<Campaign> cmps = new ArrayList<>();
		
		String query = "SELECT id, name, customer, state, num_images FROM jnk.jnk_campaigns WHERE id_owner = ? ";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, user.getId());
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					String name = result.getString("name");
					String customer = result.getString("customer");
					String owner = user.getUsername();
					State state = State.valueOf(result.getString("state"));
					int numImages = result.getInt("num_images");
					Campaign campaign = new Campaign(name, customer, owner, state, numImages);
					campaign.setId(result.getInt("id"));
					cmps.add(campaign);
				}
				return cmps;
			}
		}
	}

	public Campaign getCampaign(String campaignName) throws SQLException {
		String query = "SELECT c.id, c.name, u.name, customer, state, num_images FROM jnk.jnk_campaigns as c JOIN jnk.jnk_users as u ON c.id_owner = u.id WHERE c.name = ? ";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {

			pstatement.setString(1, campaignName);
			pstatement.execute();

			try (ResultSet result = pstatement.executeQuery();) {
				result.next();
				String customer = result.getString("customer");
				String owner = result.getString("u.name");
				State state = State.valueOf(result.getString("state"));
				int numImages = result.getInt("num_images");
				Campaign campaign = new Campaign(campaignName, customer, owner, state, numImages);
				campaign.setId(result.getInt("id"));
				return campaign;
			}
		}
	}

	public List<Campaign> getUserCampaigns(User user, boolean alreadyAnnotated) throws SQLException {

		List<Campaign> cmps = new ArrayList<>();
		String query;
		
		if(alreadyAnnotated)
			query = "SELECT id, name, customer, num_images FROM jnk.jnk_campaigns WHERE state = ? and "
					+ "id IN ("
					+ "SELECT id_camp FROM jnk_annotations WHERE id_user = ? )";
		else
			query = "SELECT id, name, customer, num_images FROM jnk.jnk_campaigns WHERE state = ? and "
					+ "id NOT IN ("
					+ "SELECT id_camp FROM jnk_annotations WHERE id_user = ? )";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, "Started");
			pstatement.setInt(2, user.getId());
			
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					String name = result.getString("name");
					String customer = result.getString("customer");
					int numImages = result.getInt("num_images");
					Campaign campaign = new Campaign(name, customer, "", State.Started, numImages);
					campaign.setId(result.getInt("id"));
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
		
		query = "INSERT INTO jnk.jnk_images (name, latitude, longitude, city, region, source, resolution, id_campaign) "
				+ "VALUES (?, ?, ?, ? ,?, ?, ?, ?)";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {

			pstatement.setInt(1, campaign.getNumImages());
			pstatement.setString(2, newImage.getLatitude());
			pstatement.setString(3, newImage.getLongitude());
			pstatement.setString(4, newImage.getCity());
			pstatement.setString(5, newImage.getRegion());
			pstatement.setString(6, newImage.getSource());
			pstatement.setString(7, String.valueOf(newImage.getResolution()));
			pstatement.setInt(8, campaign.getId());
			pstatement.executeUpdate();
			
		}
	}

	public Campaign updateCampaign(Campaign campaign, String action) throws SQLException {
		String query = "UPDATE jnk_campaigns SET state = ? WHERE id = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {

			if(action.equals("Start"))	{
				pstatement.setString(1, State.Started.toString());
				campaign.setState(State.Started);
			}
			if(action.equals("Close"))	{
				pstatement.setString(1, State.Closed.toString());
				campaign.setState(State.Closed);
			}
			pstatement.setInt(2, campaign.getId());
			pstatement.executeUpdate();
		}
		return campaign;
	}

	public CampaignStats getStats(Campaign campaign) throws SQLException {
		
		CampaignStats stats = new CampaignStats();
		stats.setNumImages(campaign.getNumImages());
		
		String query = "SELECT count(id) FROM jnk_annotations WHERE id_camp = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, campaign.getId());
			
			try (ResultSet result = pstatement.executeQuery();) {
				result.next();
				stats.setAnnotations(result.getInt("count(id)"));
			}
		}
		stats.setAverage();
		
		query = "SELECT count(a1.id_image) "
			  + "FROM jnk_annotations as a1, jnk_annotations as a2  "
			  + "WHERE a1.id_image = a2.id_image "
			  + "AND a1.validity <> a2.validity "
			  + "AND a1.id_camp = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, campaign.getId());
			
			try (ResultSet result = pstatement.executeQuery();) {
				result.next();
				stats.setConflicts(result.getInt(1));
			}
		}
		System.out.println(stats.getAnnotations() + stats.getAverage() + stats.getNumImages() + stats.getConflicts());
		
		return stats;
	}
}
