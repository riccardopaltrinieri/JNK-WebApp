package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.User;
import beans.Worker;
import dao.UserDAO;
import dao.WorkerDAO;

/**
 * Servlet implementation class EditProfile
 */
@WebServlet("/EditProfile")
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
		
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		WorkerDAO wrk = new WorkerDAO(connection);
		User user = (User) request.getSession().getAttribute("user");
		try {
			Worker worker = wrk.getCredentials(user.getUsername());
			request.getSession().setAttribute("worker",worker);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String path = "/WEB-INF/EditProfile.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, ctx, response.getWriter());
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		WorkerDAO wrk = new WorkerDAO(connection);
		Worker worker = (Worker) request.getSession().getAttribute("worker");
		User user = (User) request.getSession().getAttribute("user");

		String username = request.getParameter("username");
		String name = request.getParameter("name");
		String mailAddress = request.getParameter("mailAddress");
		String password = request.getParameter("pwd");
		
		try {
			if(username.isEmpty()) username = worker.getUsername();
			if(name.isEmpty()) username = worker.getName();
			if(mailAddress.isEmpty()) username = worker.getMailAddress();
			if(password.isEmpty()) username = worker.getPassword();
			
			wrk.editUser(user.getUsername(), username, name, mailAddress, password);
			user.setUsername(username);
			
		} catch (SQLException e) {
			System.out.println(e);
			request.getSession().setAttribute("notvalid", true);
		}
		
		doGet(request,response);
	}

}
