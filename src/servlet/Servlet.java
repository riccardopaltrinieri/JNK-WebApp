package servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beans.User;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

/**
 * Servlet implementation class Servlet
 */
@WebServlet("/LogIn")
public class Servlet extends HttpServlet {

	
	private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;  


    /**
     * @see HttpServlet#HttpServlet()
     */
    public Servlet() {
        super();
    }
    
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
    	
    	String url = "/index.html"; 
    	String action = request.getParameter("action"); 
    	
    	if (action == null) action = "join";
    	if (action.equals("join")) url = getServletContext().getContextPath()+"/index.html";
    	//request.setAttribute("user", user); 
    	//url = ""/thanks.jsp";
    	/*getServletContext().
    		getRequestDispatcher(url)
    		.forward(request, response);*/
    	response.sendRedirect(url);
    	
    	}

    
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doPost(request, response);
		
		/*HttpSession session = request.getSession();
		String path = "/index.html";
		
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		response.setContentType("text/html");
		RequestDispatcher view = request.getRequestDispatcher(path);
		view.forward(request,response);
		
		
		return;*/
		
	}

}
