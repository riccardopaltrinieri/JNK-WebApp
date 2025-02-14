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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.User;
import dao.ImageDAO;
import dao.UserDAO;

/**
 * Servlet implementation class EditProfile
 * Shows the account setting page to the user and with POST method it
 * can modify user data
 */
@WebServlet("/EditProfile")
@MultipartConfig
public class EditProfile extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
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
		
		// Thymeleaf initialization
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String path = "/WEB-INF/EditProfile.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		UserDAO usr = new UserDAO(connection);
		ImageDAO img = new ImageDAO();
		User user = (User) request.getSession().getAttribute("user");

		// Get the user info
		String username = request.getParameter("username");
		String name = request.getParameter("name");
		String mailAddress = request.getParameter("mailAddress");
		String password = request.getParameter("pwd");
		Part image = request.getPart("avatar");
		
		try {
			
			// The user could modify just one thing per form so the method will use old values
			if(username.isEmpty()) username = user.getUsername();
			if(name.isEmpty()) name = user.getName();
			if(mailAddress.isEmpty()) mailAddress = user.getMailAddress();
			if(password.isEmpty()) password = user.getPassword();
			
			// The user info are stored in the sql database
			user = usr.editUser(user, username, name, mailAddress, password);
			
			if(image != null && image.getSize() != 0) {
				// And if present the new profile picture is stored in the File System database
				img.addUserImage(user, image);
			}
			
			request.getSession().setAttribute("user",user);
			
		} catch (SQLException e) {
			// When the username or the email address has already been used 
			// an error message is showed on the page
			request.setAttribute("notValid", "true");
			System.out.println(e);
		}
		
		doGet(request,response);
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
