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
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.Campaign;
import beans.Image;
import dao.CampaignDAO;
import dao.ImageDAO;

/**
 * Servlet implementation class InfoWorkerCampaign
 */
@WebServlet("/WorkerCampaign")
public class InfoWorkerCampaign extends HttpServlet {
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
		Image image = (Image) request.getAttribute("image");
		CampaignDAO cmp = new CampaignDAO(connection);
		ImageDAO img = new ImageDAO();
		
		if(campaignName != null) {
			try {
				request.getSession().setAttribute("campaign", cmp.getCampaign(campaignName));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} 
		Campaign campaign = (Campaign) request.getSession().getAttribute("campaign");
		
		if(image == null && campaign.getNumImages() > 0) {
			try {
				image = img.getImageInfo(campaign, "1", connection);
				request.setAttribute("image", image);
			} catch (SQLException e) {
				System.out.println(e);
			}
		}
		
		List<Image> campaignImages = img.getCampaignImages(campaign);
		request.setAttribute("campaignImages", campaignImages);
		
		String path = "/WEB-INF/WorkerCampaign.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		doGet(request, response);
	}

}
