package filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.User;

/**
 * Servlet Filter implementation class ManagerChecker
 */
@WebFilter("/ManagerChecker")
public class ManagerChecker implements Filter {

    public ManagerChecker() {
    	super();
    }

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		// check if the client is a manager
		User user = (User) req.getSession().getAttribute("user");
		
		if (!user.getRole().equals("manager")) {
			String path = req.getServletContext().getContextPath() + "/Home";
			res.sendRedirect(path);
			return;
		}
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

}
