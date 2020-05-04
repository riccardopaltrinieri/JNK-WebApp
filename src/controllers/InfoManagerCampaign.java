package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.Campaign;
import beans.Image;
import dao.AnnotationDAO;
import dao.CampaignDAO;
import dao.ImageDAO;

/**
 * Servlet implementation class InfoManagerCampaign
 * This is used to show to a manager all the campaigns he created 
 * and to create new ones
 */
@WebServlet("/ManageCampaign")
public class InfoManagerCampaign extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection connection = null;
    
	private TemplateEngine templateEngine;
    
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
		
		
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String campaignName = request.getParameter("campaign");
		Campaign campaign = (Campaign) request.getSession().getAttribute("campaign");
		CampaignDAO cmp = new CampaignDAO(connection);
		AnnotationDAO ant = new AnnotationDAO(connection);
		ImageDAO img = new ImageDAO();
		List<Image> images = new ArrayList<>();
		
		// If the user is on the homepage and choose to see a specific campaign
		// get it from the database and save it in a var and in the session
		if(campaignName != null) {
			try {
				campaign = cmp.getCampaign(campaignName);
				request.getSession().setAttribute("campaign", campaign);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		// Pass all the images with their infos and annotations to the page 
		// in order to show them in modals with bootstrap/javascript/thymeleaf
		try {
			Image image;
			for (int i = 1; i <= campaign.getNumImages(); i++) {
				image = img.getImageInfo(campaign, String.valueOf(i), connection);
				image.setAnnotations(ant.getAnnotationsManager(image));
				images.add(image);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		request.setAttribute("images", images);
		
		String path = "/WEB-INF/ManagerCampaign.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, ctx, response.getWriter());
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}


	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
		}
	}
}
