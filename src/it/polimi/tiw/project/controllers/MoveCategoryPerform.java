package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.project.daos.CategoriesDAO;
import it.polimi.tiw.project.utils.ConnectionHandler;
import it.polimi.tiw.project.utils.ErrorDispatcher;

/**
 * Servlet implementation class MoveCategoryPerform
 */
@WebServlet("/MoveCategoryPerform")
public class MoveCategoryPerform extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MoveCategoryPerform() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	} 

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Integer catToMoveId = null;
		Integer oldFatherId = null;
		Integer newFatherId = null;
		
		try {
			catToMoveId = Integer.parseInt(request.getParameter("category-to-move-id"));
			newFatherId = Integer.parseInt(request.getParameter("category-to-move-new-father"));
			oldFatherId = Integer.parseInt(request.getParameter("category-to-move-old-father"));
			if(catToMoveId < 0 || newFatherId < 0 || oldFatherId < 0) {
			    ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
			    return;
			}
		} catch (NumberFormatException | NullPointerException e) {
			ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
			return;
		}
		
	    CategoriesDAO cDAO = new CategoriesDAO(connection);
	    try {
	    	cDAO.moveCategory(catToMoveId, oldFatherId, newFatherId);
	    } catch (Exception e) {
	    	ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
	    }

	    response.sendRedirect("GetCategories");
	}
	
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
