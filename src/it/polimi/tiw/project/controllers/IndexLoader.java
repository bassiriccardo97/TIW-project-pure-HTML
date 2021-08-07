package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.project.utils.SharedPropertyMessageResolver;
import it.polimi.tiw.project.utils.TemplateEngineStarter;

/**
 * Servlet implementation class GetUsers
 */
@WebServlet("/IndexLoader")
public class IndexLoader extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public IndexLoader() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	TemplateEngine templateEngine = TemplateEngineStarter.getEngine(getServletContext());
		templateEngine.setMessageResolver(new SharedPropertyMessageResolver(getServletContext(), "templates", "index", request.getSession(false)));
		response.setCharacterEncoding("UTF-8");
		
		String path = "/WEB-INF/index.html";
		ServletContext context = getServletContext();
		final WebContext ctx = new WebContext(request, response, context, request.getLocale());
		if (request.getSession(false) != null) {
			ctx.setVariable("language", request.getSession(false).getAttribute("language"));
		} else {
			ctx.setVariable("language", Locale.getDefault().getLanguage());
		}
		templateEngine.process(path, ctx, response.getWriter());
	}
}
