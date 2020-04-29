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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.Annotation;
import beans.Campaign;
import beans.Image;
import beans.User;
import dao.AnnotationDAO;
import dao.ImageDAO;

/**
 * Servlet implementation class InfoImage
 */
@WebServlet("/InfoImage")
public class InfoImage extends HttpServlet {
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
	
		User user = (User) request.getSession().getAttribute("user");
		String imageName = request.getParameter("image");
		Campaign campaign = (Campaign) request.getSession().getAttribute("campaign");
		AnnotationDAO ant = new AnnotationDAO(connection);
		ImageDAO img = new ImageDAO();
		
		Image image;
		try {
			image = img.getImageInfo(campaign, imageName, connection);
			request.setAttribute("image", image);
			
			if(user.getRole().equals("manager")) {
				List<Annotation> annotations = ant.getAnnotationsManager(image);
				request.setAttribute("annotations", annotations);
				String path = "/ManageCampaign";
				request.getRequestDispatcher(path).forward(request, response);
			} else {
				String path = "/WorkerCampaign";
				request.getRequestDispatcher(path).forward(request, response);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
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
