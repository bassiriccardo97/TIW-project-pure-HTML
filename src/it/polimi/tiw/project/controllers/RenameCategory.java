package it.polimi.tiw.project.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.project.utils.ErrorDispatcher;
import it.polimi.tiw.project.utils.SharedPropertyMessageResolver;
import it.polimi.tiw.project.utils.TemplateEngineStarter;

/**
 * Servlet implementation class RenameCategory
 */
@WebServlet("/RenameCategory")
public class RenameCategory extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RenameCategory() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String name = null;
		Integer catId = null;
		request.setCharacterEncoding("UTF-8");
		
		name = request.getParameter("category-to-rename-name");
		
		if (name == null || name.isEmpty()) {
			ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
			return;
		}
		
		try {
			catId = Integer.parseInt(request.getParameter("category-to-rename-id"));
			if (catId < 0) {
			    ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
			    return;
			}
		} catch (NumberFormatException | NullPointerException e) {
			ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
			return;
		}

		TemplateEngine templateEngine = TemplateEngineStarter.getEngine(getServletContext());
		templateEngine.setMessageResolver(new SharedPropertyMessageResolver(getServletContext(), "templates", "rename", request.getSession(false)));
		
		String path = "/WEB-INF/templates/rename.html";
		final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
		ctx.setVariable("oldName", name);
		ctx.setVariable("catId", catId);
		ctx.setVariable("language", request.getSession(false).getAttribute("language"));
		response.setCharacterEncoding("UTF-8");
		templateEngine.process(path, ctx, response.getWriter());
	}

}
