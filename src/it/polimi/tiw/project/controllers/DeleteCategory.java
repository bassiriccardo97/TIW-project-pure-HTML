package it.polimi.tiw.project.controllers;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import it.polimi.tiw.project.beans.Category;
import it.polimi.tiw.project.daos.CategoriesDAO;
import it.polimi.tiw.project.utils.ConnectionHandler;
import it.polimi.tiw.project.utils.ErrorDispatcher;

/**
 * Servlet implementation class DeleteCategory
 */
@WebServlet("/DeleteCategory")
public class DeleteCategory extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	String folderPath = "";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteCategory() {
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
		Integer fatherId = null;
		try {
			catId = Integer.parseInt(request.getParameter("category-to-delete-id"));
			fatherId = Integer.parseInt(request.getParameter("category-to-delete-father-id"));
			if (fatherId < 0 || catId < 0) {
			    ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
				return;
			}
		} catch (NullPointerException | NumberFormatException e) {
			ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
			return;
		}
		
		CategoriesDAO service = new CategoriesDAO(connection);
		List<Category> childrenToDelete = null;
		try {
			childrenToDelete = service.deleteCategory(catId, fatherId);
		} catch (Exception e) {
			ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}
		
		// delete all file which name starts with the id of the category deleted
		try {
			File folder = new File(folderPath);
			File[] files = folder.listFiles();
			for (File file : files) {
				String folderFileName = file.getName();
				if ((folderFileName.endsWith(".jpg") || folderFileName.endsWith(".jpeg")) && folderFileName.substring(0, folderFileName.indexOf("--")).equals(catId.toString())) {
					file.delete();
				}
			}
		} catch (Exception e) {
			ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete a file.");
			return;
		}
		
		// delete (as before) all files related to each children of the category deleted
		for (Category c : childrenToDelete) {
			try {
				File folder = new File(folderPath);
				File[] files = folder.listFiles();
				for (File file : files) {
					String folderFileName = file.getName();
					if ((folderFileName.endsWith(".jpg") || folderFileName.endsWith(".jpeg")) && folderFileName.substring(0, folderFileName.indexOf("--")).equals(String.valueOf(c.getId()))) {
						file.delete();
					}
				}
			} catch (Exception e) {
				ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete a file.");
				return;
			}
		}
		
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GetCategories";
		response.sendRedirect(path);
	}
	
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
