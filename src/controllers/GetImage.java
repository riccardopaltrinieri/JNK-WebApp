package controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.NoSuchElementException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.Campaign;
import dao.ImageDAO;

/**
 * Servlet implementation class SendImage
 */
@WebServlet("/GetImage")
public class GetImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Campaign campaign = (Campaign) request.getSession().getAttribute("campaign");
        ImageDAO img = new ImageDAO();
        File image;

        String requestedImage = request.getParameter("image");
        
        if (requestedImage != null) {
        	//case where the requested image is from a campaign
        	image = img.getCampaignImage(requestedImage, campaign);
        } else {
        	//case where the requested image is the profile picture of a user
        	requestedImage = request.getParameter("userImage");
            image = img.getUserImage(requestedImage);
        }
        if (!image.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            throw new NoSuchElementException();
        }
        // Init servlet response.
        response.reset();
        response.setContentType("image/jpeg");
        response.setHeader("Content-Length", String.valueOf(image.length()));

        // Write image content to response.
        Files.copy(image.toPath(), response.getOutputStream());
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
