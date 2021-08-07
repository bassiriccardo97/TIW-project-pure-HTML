package it.polimi.tiw.project.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import it.polimi.tiw.project.daos.CategoriesDAO;
import it.polimi.tiw.project.utils.ConnectionHandler;
import it.polimi.tiw.project.utils.ErrorDispatcher;

/**
 * Servlet implementation class AddImage
 */
@WebServlet("/AddImage")
@MultipartConfig
public class AddImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	String folderPath = "";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddImage() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
        folderPath = System.getenv("outputpath");
	}    

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Integer catId = null;
		String catName = null;
		request.setCharacterEncoding("UTF-8");
		
		catName = request.getParameter("category-name");
		if (catName == null || catName.isEmpty()) {
			ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
			return;
		}
		
		try {
			catId = Integer.parseInt(request.getParameter("father-id"));
			if (catId < 0) {
			    ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
				return;
			}
		} catch (NumberFormatException | NullPointerException e) {
			ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
			return;
		}
		
		CategoriesDAO service = new CategoriesDAO(connection);
		int res = -1;
		try {
			res = service.checkCategoryExists(catId);
		} catch (Exception e) {
			ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}
		
		if (res == -1) {
			ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_BAD_REQUEST, "The requested category does not exists.");
			return;
		}
		
		Collection<Part> filePart = request.getParts();
		
		// check file format
		for (Part p : filePart) {
            String contentType = p.getContentType();
			if (contentType != null) {				
				if (!contentType.startsWith("image/jpeg")) {
					ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_BAD_REQUEST, "File missing or format not permitted.");
					return;
				}
			}
		}
		// save each file with the name as: fatherId--fileName, ex. "7--Giove.jpg"
		for (Part p : filePart) {
            String contentType = p.getContentType();
			if (contentType != null) {
				if (contentType.startsWith("image/jpeg")) {
					String outputPath = folderPath + catId + "--" + p.getSubmittedFileName();
					File file = new File(outputPath);
					
					try (InputStream fileContent = p.getInputStream()) {
						Files.copy(fileContent, file.toPath());
					} catch (FileAlreadyExistsException faee) {
						ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_BAD_REQUEST, "Already existing file.");
						return;
					} catch (Exception e) {
						e.printStackTrace();
						ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error while saving a file.");
						return;
					}
				}
			}
		}

		String ctxpath = getServletContext().getContextPath();
		
		String path = ctxpath + "/ImagesList?category-id=" + catId + "&category-name=" + catName;
		response.sendRedirect(path);
	}
}
