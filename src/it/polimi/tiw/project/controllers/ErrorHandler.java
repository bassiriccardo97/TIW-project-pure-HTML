package it.polimi.tiw.project.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.project.utils.TemplateEngineStarter;

/**
 * Servlet implementation class ErrorHandler
 */
@WebServlet("/ErrorHandler")
public class ErrorHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ErrorHandler() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		TemplateEngine templateEngine = TemplateEngineStarter.getEngine(getServletContext());
	    
	    String path = "/WEB-INF/templates/error.html";
	    final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
	    ctx.setVariable("errorCode", request.getAttribute("errorCode"));
		ctx.setVariable("errorMessage", request.getAttribute("errorMessage"));
	    response.setCharacterEncoding("UTF-8");
	    templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		TemplateEngine templateEngine = TemplateEngineStarter.getEngine(getServletContext());
	    
	    String path = "/WEB-INF/templates/error.html";
	    final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
	    ctx.setVariable("errorCode", "Error code: " + request.getAttribute("errorCode"));
		ctx.setVariable("errorMessage", "Message: " + request.getAttribute("errorMessage"));
	    response.setCharacterEncoding("UTF-8");
	    templateEngine.process(path, ctx, response.getWriter());
	}

}
