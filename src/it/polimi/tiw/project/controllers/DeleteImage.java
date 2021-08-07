package it.polimi.tiw.project.controllers;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.project.utils.ErrorDispatcher;

/**
 * Servlet implementation class DeleteImage
 */
@WebServlet("/DeleteImage")
public class DeleteImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String folderPath = "";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteImage() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException {
      folderPath = System.getenv("outputpath");
	}  

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = null;
		
		name = request.getParameter("filename");
		if (name == null || name.isEmpty()) {
			ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
			return;
		}
		
		try {
			File f = new File(folderPath + name);
			f.delete();
		} catch (Exception e) {
			ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete a file.");
			return;
		}

		response.sendRedirect(request.getHeader("referer"));
	}
}
