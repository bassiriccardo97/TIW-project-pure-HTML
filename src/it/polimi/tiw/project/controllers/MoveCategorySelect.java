package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.project.beans.CategoryTree;
import it.polimi.tiw.project.daos.CategoriesDAO;
import it.polimi.tiw.project.utils.ConnectionHandler;
import it.polimi.tiw.project.utils.ErrorDispatcher;
import it.polimi.tiw.project.utils.SharedPropertyMessageResolver;
import it.polimi.tiw.project.utils.TemplateEngineStarter;

/**
 * Servlet implementation class MoveCategory
 */
@WebServlet("/MoveCategorySelect")
public class MoveCategorySelect extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MoveCategorySelect() {
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
	    
	    String catToMoveId = null;
	    String catToMoveFatherId = null;
		
	    catToMoveId = request.getParameter("category-to-move-id");
	    catToMoveFatherId = request.getParameter("category-to-move-father");
	    if (catToMoveId == null || catToMoveId.isEmpty() || catToMoveFatherId == null || catToMoveFatherId.isEmpty()) {
			ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
			return;
	    }
		
	    try {
	    	if (Integer.parseInt(catToMoveFatherId) < 0 || Integer.parseInt(catToMoveId) < 0 ) {
				ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
				return;
	    	}
	    } catch (NumberFormatException nfe) {
			ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
			return;
	    }
	    
	    CategoryTree categoriesTree = null;
	    CategoriesDAO cDAO = new CategoriesDAO(connection);
	    try {
	    	categoriesTree = cDAO.getCategories();
	    } catch (SQLException e) {
	    	ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
	    }

		TemplateEngine templateEngine = TemplateEngineStarter.getEngine(getServletContext());
	    templateEngine.setMessageResolver(new SharedPropertyMessageResolver(getServletContext(), "templates", "moveCategory", request.getSession(false)));
	    
	    String path = "/WEB-INF/templates/moveCategory.html";
	    final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
	    ctx.setVariable("categoriesTree", categoriesTree);
	    ctx.setVariable("treeList", categoriesTree.getAllButChildren(catToMoveId));
	    ctx.setVariable("catToMoveId", catToMoveId);
	    ctx.setVariable("catToMoveFather", catToMoveFatherId);
		ctx.setVariable("language", request.getSession(false).getAttribute("language"));
	    response.setCharacterEncoding("UTF-8");
	    templateEngine.process(path, ctx, response.getWriter());
	}
	
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
