package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.Campaign;
import beans.User;
import dao.CampaignDAO;


/**
 * Servlet implementation class HomePage
 * Main servlet for the web-app, it show the homepage and 
 * it should be accessed only when logged in to recognise
 * the user role
 */
@WebServlet({"/Home", "/CreateCampaign"})
public class HomePage extends HttpServlet {
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
		
		// Thymeleaf initialization
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		CampaignDAO cmp = new CampaignDAO(connection);
		List<Campaign> campaigns;
		
		if(user.getRole().equals("manager")) {
			try {
				campaigns = cmp.getManagerCampaigns(user);
				request.setAttribute("campaigns", campaigns);
			} catch (SQLException e) {
				System.out.println(e);
			}
		} else {
			try {
				// the campaigns are split in the opted ones and 
				// the ones which are not opted
				campaigns = cmp.getUserCampaigns(user, false);
				List<Campaign> annotatedCampaigns = cmp.getUserCampaigns(user, true);
				request.setAttribute("campaigns", campaigns);
				request.setAttribute("annCampaigns", annotatedCampaigns);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		String path = "/index.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, ctx, response.getWriter());
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		CampaignDAO cmp = new CampaignDAO(connection);
		
		String name = (String) request.getParameter("name");
		String customer = (String) request.getParameter("customer");
		User user = (User) request.getSession().getAttribute("user");
		
		// The user create a new campaign
		try {
			cmp.createNewCampaign(user, name, customer);
		} catch (SQLException e) {
			// When the name is already used an error message is shown on the page
			System.out.println(e);
			request.setAttribute("notValid", "true");
		}
		
		// Redirect to the home where the user can select the campaign
		// and get its ID from the database
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