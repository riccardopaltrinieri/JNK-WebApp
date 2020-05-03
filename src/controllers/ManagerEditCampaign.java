package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import beans.Campaign;
import beans.Image;
import dao.CampaignDAO;
import dao.ImageDAO;

/**
 * Servlet implementation class ManagerEditCampaign
 * Used to modify the state of a campaign or to add new images to it
 */
@WebServlet({"/AddImage", "/UpdateCampaign" })
@MultipartConfig
public class ManagerEditCampaign extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection connection = null;
    
	@Override
	public void init() throws ServletException {
		
		ServletContext context = getServletContext();

		//MySQL database connection initialization
		try {
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e ) {
			throw new UnavailableException("Can't load db Driver");
		} catch (SQLException e ) {
			throw new UnavailableException("Couldn't connect");
		}
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String action = request.getParameter("action");
		CampaignDAO cmp = new CampaignDAO(connection);
		Campaign campaign = (Campaign) request.getSession().getAttribute("campaign");
		
		// A form with GET method is used to modify the state of the campaign
		if(action != null) {
			try {
				cmp.updateCampaign(campaign, action);
				request.getSession().setAttribute("campaign", campaign);
				} catch (SQLException e) {
				System.out.println(e);
			}
		}
		String path = "/ManageCampaign";
		response.sendRedirect(request.getContextPath() + path);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		CampaignDAO cmp = new CampaignDAO(connection);
		ImageDAO img = new ImageDAO();
		Campaign campaign = (Campaign) request.getSession().getAttribute("campaign");
		
		// a temporary image object is created to contain all the info
		Image tempImage = new Image(0);
		tempImage.setLatitude(request.getParameter("latitude"));
		tempImage.setLongitude(request.getParameter("longitude"));
		tempImage.setResolution(request.getParameter("resolution"));
		tempImage.setRegion(request.getParameter("region"));
		tempImage.setSource(request.getParameter("source"));
		tempImage.setCity(request.getParameter("city"));
		tempImage.setDate(Date.valueOf(request.getParameter("date"))); //TODO fix date input
		
		Part image = request.getPart("image");
		if(image != null && image.getSize() != 0) {
			//the real file is stored in the File System (or the database used for images)
			img.addCampaignImage(campaign, image);
			try {
				// while the info are written in the sql database
				cmp.newImage(campaign, tempImage);
			} catch (SQLException e) {
				System.out.println(e);
			}
		} else {
			// When the image file is corrupted an error message is shown on the page
			request.setAttribute("notValid", "true");
		}
		doGet(request,response);
	}

}
