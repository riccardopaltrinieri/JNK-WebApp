package controllers;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.Worker;
import dao.UserDAO;

/**
 * Servlet implementation class LogIn
 */
@WebServlet({"/LogIn", "/Login"})
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
    
	@Override
	public void init() throws ServletException {
		
		ServletContext servletContext = getServletContext();
		
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = "/WEB-INF/LogIn.html";
		RequestDispatcher dispatcher = request.getRequestDispatcher(path);
		dispatcher.forward(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Worker user = new Worker();
		UserDAO usr = new UserDAO();
		
		String usrn = request.getParameter("username");
		String pwd = request.getParameter("password");
		
		if (usr.checkCredentials(usrn, pwd)) {
			String path = getServletContext().getContextPath() + "/Home";
			request.getSession().setAttribute("valid", true);
			request.getSession().setAttribute("user",user);
			response.sendRedirect(path);
		}
		else {
			String path = getServletContext().getContextPath() + "/LogIn";
			request.getSession().setAttribute("valid", false);
			response.sendRedirect(path);
		}
	}

}
