package servlet;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.Worker;

/**
 * Servlet implementation class LogIn
 */
@WebServlet({"/LogIn", "/Login"})
public class LogInServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = "/WEB-INF/LogIn.html";
		RequestDispatcher dispatcher = request.getRequestDispatcher(path);
		dispatcher.forward(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Worker user = new Worker();
		user.setName(request.getParameter("name"));
		user.setMailAddress(request.getParameter("mailAddress"));
		user.setUsername(request.getParameter("username"));
		user.setPassword(request.getParameter("password"));
		
		
		String path = getServletContext().getContextPath() + "/index.jsp";
		request.getSession().setAttribute("user",user);
		response.sendRedirect(path);
	}

}
