package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Campaign;
import beans.User;
import enumerations.State;

public class CampaignDAO {

	private Connection connection;
	
	public CampaignDAO(Connection connection) {
		this.connection = connection;
	}

	public Campaign createNewCampaign(User user, String name, String customer) throws SQLException {
		String query = "INSERT INTO jnk.jnk_campaigns (name, customer, state, id_owner) VALUES (?, ?, ?, "
				+ "(SELECT id FROM jnk.jnk_users WHERE username = ?))";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {

			pstatement.setString(1, name);
			pstatement.setString(2, customer);
			pstatement.setString(3, State.Created.toString());
			pstatement.setString(4, user.getUsername());
			pstatement.executeUpdate();
			
			return new Campaign(name, customer, user.getUsername(), State.Created);
		}
		
	}

	public List<Campaign> getManagerCampaigns(User user) throws SQLException {
		
		List<Campaign> cmps = new ArrayList<>();
		
		String query = "SELECT name, customer, state FROM jnk.jnk_campaigns WHERE id_owner = (SELECT id FROM jnk.jnk_users WHERE username = ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, user.getUsername());
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					String name = result.getString("name");
					String customer = result.getString("customer");
					String owner = user.getUsername();
					State state = State.valueOf(result.getString("state"));
					Campaign campaign = new Campaign(name, customer, owner, state);
					cmps.add(campaign);
				}
				return cmps;
			}
		}
	}

	public Campaign getCampaign(String campaign) throws SQLException {
		String query = "SELECT name, customer, state FROM jnk.jnk_campaigns WHERE name = ? ";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {

			pstatement.setString(1, campaign);
			pstatement.execute();

			try (ResultSet result = pstatement.executeQuery();) {
				result.next();
				String customer = result.getString("customer");
				String owner = result.getString("customer");
				State state = State.valueOf(result.getString("state"));
				return new Campaign(campaign, customer, owner, state);
			}
		}
	}

}
