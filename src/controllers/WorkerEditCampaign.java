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
import beans.Annotation;
import beans.Campaign;
import beans.User;
import dao.AnnotationDAO;

/**
 * Servlet implementation class UserEditCampaign
 */
@WebServlet("/AddAnnotation")
public class WorkerEditCampaign extends HttpServlet {
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
		
		String path = "/WorkerCampaign";
		response.sendRedirect(request.getContextPath() + path);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Campaign campaign = (Campaign) request.getSession().getAttribute("campaign");
		User user = (User) request.getSession().getAttribute("user");
		AnnotationDAO ant = new AnnotationDAO(connection);
		Annotation annotation = new Annotation();
		
		
		int imageName = Integer.parseInt(request.getParameter("image"));
		annotation.setValidity(request.getParameter("validity"));
		annotation.setNotes(request.getParameter("notes"));
		annotation.setTrust(user.getLvlExp());
		
		try {
			ant.addAnnotation(annotation, user, campaign, imageName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		doGet(request,response);
	}

}
