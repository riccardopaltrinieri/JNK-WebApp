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

/**
 * An Object that can be used by the user to extract all the info about 
 * {@link Campaign} from a database. It uses the connection passed in the 
 * constructor.
 */
public class CampaignDAO {

	private Connection connection;

	/**
	 * Construct the DAO connecting it to the database saving the parameter connection.
	 * @param connection to a specific database
	 */
	public CampaignDAO(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Insert a new campaign in the database table with the name and customer passed
	 * and using the user id as external key
	 * @param user who created the campaign
	 * @param name of the campaign
	 * @param customer who requested the campaign
	 * @throws SQLException
	 */
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

	/**
	 * Gets all the campaign created by a manager (with all the relative info) and save
	 * them in the returned list
	 * @param user who ask for the List
	 * @return an {@link ArrayList}
	 * @throws SQLException
	 */
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

	/**
	 * Get all the info about the campaign named like the parameter (campaign names are unique)
	 * @param campaignName
	 * @param user 
	 * @return the {@link Campaign} filled with the info
	 * @throws SQLException
	 */
	public Campaign getCampaign(String campaignName, User user) throws SQLException {
		String query = "SELECT c.id, c.name, u.username, customer, state, num_images "
					 + "FROM jnk.jnk_campaigns as c JOIN jnk.jnk_users as u ON c.id_owner = u.id "
					 + "WHERE c.name = ? AND c.id_owner = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {

			pstatement.setString(1, campaignName);
			pstatement.setInt(2, user.getId());
			pstatement.execute();

			try (ResultSet result = pstatement.executeQuery();) {
				if(result.next()) {
					String customer = result.getString("customer");
					String owner = result.getString("u.username");
					State state = State.valueOf(result.getString("state"));
					int numImages = result.getInt("num_images");
					Campaign campaign = new Campaign(campaignName, customer, owner, state, numImages);
					campaign.setId(result.getInt("id"));
					return campaign;
				}
				else throw new SQLException();
			}
		}
	}

	/**
	 * Gets all the campaigns where the user already noted something on an image if "annotated"
	 * is true, or the campaign where the user never noted anything if "annotated" is false
	 * @param user who wants to see the campaigns
	 * @param annotated flag used to choose between campaigns already noted by the user and those empty yet
	 * @return an {@link ArrayList} of campaigns
	 * @throws SQLException
	 */
	public List<Campaign> getUserCampaigns(User user, boolean annotated) throws SQLException {

		List<Campaign> cmps = new ArrayList<>();
		String query;
		
		if(annotated)
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

	/**
	 * Update the number of images of a campaign when an image is added by a manager and 
	 * add it to the images table in the database
	 * @param campaign where to add the image
	 * @param newImage should contains all the info about the new image
	 * @throws SQLException
	 */
	public void newImage(Campaign campaign, Image newImage) throws SQLException {
		String query = "UPDATE jnk_campaigns SET num_images = ? WHERE name = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {

			pstatement.setInt(1, campaign.getNumImages());
			pstatement.setString(2, campaign.getName());
			pstatement.executeUpdate();
			
		}
		
		query = "INSERT INTO jnk.jnk_images (name, id_campaign, latitude, longitude, city, region, source, resolution, date) "
				+ "VALUES (?, ?, ?, ? ,?, ?, ?, ?, ?)";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {

			pstatement.setInt(1, campaign.getNumImages());
			pstatement.setInt(2, campaign.getId());
			pstatement.setString(3, newImage.getLatitude());
			pstatement.setString(4, newImage.getLongitude());
			pstatement.setString(5, newImage.getCity());
			pstatement.setString(6, newImage.getRegion());
			pstatement.setString(7, newImage.getSource());
			pstatement.setString(8, String.valueOf(newImage.getResolution()));
			pstatement.setDate(9, newImage.getDate());
			pstatement.executeUpdate();
			
		}
	}

	/**
	 * Update the state of the campaign from "Created" to "Started" and from "Started" to "Closed"
	 * @param campaign to update
	 * @param action should be "Start" or "Close"
	 * @throws SQLException
	 */
	public void updateCampaign(Campaign campaign, String action) throws SQLException {
		String query = "UPDATE jnk_campaigns SET state = ? WHERE id = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {

			// The campaign can pass to the Started state only from the Created one
			// but can pass to the Closed state from all the states
			if(action.equals("Start") && campaign.getState().equals(State.Created))	{
				
				pstatement.setString(1, State.Started.toString());
				campaign.setState(State.Started);
				pstatement.setInt(2, campaign.getId());
				pstatement.executeUpdate();
				
			} else if(action.equals("Close"))	{
				
				pstatement.setString(1, State.Closed.toString());
				campaign.setState(State.Closed);
				pstatement.setInt(2, campaign.getId());
				pstatement.executeUpdate();
			}
		}
	}

	/**
	 * Gets from the database all the data needed and return an object {@link CampaignStats}
	 * with all the statistics asked for the campaign
	 * @param campaign of which you need the statistics
	 * @return a {@link CampaignStats} filled with all the statistics
	 * @throws SQLException
	 */
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
		
		query = "SELECT count(distinct a1.id_image) "
			  + "FROM jnk_annotations as a1, jnk_annotations as a2 "
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
		
		return stats;
	}

}
