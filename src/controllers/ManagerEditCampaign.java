package controllers;

import java.io.IOException;
import java.sql.Connection;
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
		
		String action = (String) request.getParameter("action");
		CampaignDAO cmp = new CampaignDAO(connection);
		Campaign campaign = (Campaign) request.getSession().getAttribute("campaign");
		
		if(action != null) {
			try {
				campaign = cmp.updateCampaign(campaign, action);
				request.getSession().setAttribute("campaign", campaign);
				} catch (SQLException e) {
				System.out.println(e);
			}
		} 
		String path = "/InfoCampaign";
		response.sendRedirect(request.getContextPath() + path);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		CampaignDAO cmp = new CampaignDAO(connection);
		ImageDAO img = new ImageDAO();
		Campaign campaign = (Campaign) request.getSession().getAttribute("campaign");
		Image newImage = new Image(0, "");
		
		//TODO Aggiungere parametri a immagine e salvarli su database, dopodichè mostrarli sulla schermata dell'immagine
		
		newImage.setLatitude(request.getParameter("latitude"));
		newImage.setLongitude(request.getParameter("longitude"));
		newImage.setResolution(request.getParameter("resolution"));
		newImage.setRegion(request.getParameter("region"));
		newImage.setSource(request.getParameter("source"));
		newImage.setCity(request.getParameter("city"));
		
		Part image = request.getPart("image");
		if(image != null ) {
			img.addCampaignImage(campaign, image);
			try {
				cmp.newImage(campaign, newImage);
			} catch (SQLException e) {
				System.out.println(e);
			}
		}
		doGet(request,response);
	}

}
