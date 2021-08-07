package it.polimi.tiw.project.controllers;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.project.daos.CategoriesDAO;
import it.polimi.tiw.project.utils.ConnectionHandler;
import it.polimi.tiw.project.utils.ErrorDispatcher;
import it.polimi.tiw.project.utils.SharedPropertyMessageResolver;
import it.polimi.tiw.project.utils.TemplateEngineStarter;

/**
 * Servlet implementation class ImagesList
 */
@WebServlet("/ImagesList")
public class ImagesList extends HttpServlet {
    private static final long serialVersionUID = 1L;
	private Connection connection;
    String folderPath = "";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ImagesList() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		folderPath = System.getenv("outputpath");
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		List<String> images = new ArrayList<String>();
			
		String catId = request.getParameter("category-id");
		String catName = request.getParameter("category-name");
		if (catId == null || catId.isEmpty() || catName == null || catName.isEmpty()) {
			ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
			return;
		}
		
		try {
			if (Integer.parseInt(catId) < 0) {
				ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
				return;
			}
		} catch (NumberFormatException nfe) {
			ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
			return;
		}
		
		CategoriesDAO service = new CategoriesDAO(connection);
		int res = -1;
		try {
			res = service.checkCategoryExists(Integer.valueOf(catId));
		} catch (Exception e) {
			ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}
		
		if (res == -1) {
			ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_BAD_REQUEST, "The requested category does not exists.");
			return;
		}
		
		// collect in a list all the file names related to the category (the ones which name starts with the id of the category)
		File folder = new File(folderPath);
		File[] files = folder.listFiles();
		for (File file : files) {
		    String folderFileName = file.getName();
		    if ((folderFileName.endsWith(".jpg") || folderFileName.endsWith(".jpeg")) && folderFileName.substring(0, folderFileName.indexOf("--")).equals(catId.toString())) {
		    	images.add(folderFileName);
		    }
		}
		
		Locale lan;
		String lanStr = (String) request.getSession(false).getAttribute("language");
		switch (lanStr) {
			case "de":
				lan = Locale.GERMAN;
				break;
			case "fr":
				lan = Locale.FRENCH;
				break;
			case "it":
			case "es":
				lan = Locale.ITALIAN;
				break;
			default:
				lan = Locale.ENGLISH;
		}
		
		// sort the file names in alphabetical order (depends on the language set)
		images.sort(Collator.getInstance(lan));
	
		TemplateEngine templateEngine = TemplateEngineStarter.getEngine(getServletContext());
		templateEngine.setMessageResolver(new SharedPropertyMessageResolver(getServletContext(), "templates", "imagesList", request.getSession(false)));
		
		String path = "/WEB-INF/templates/imagesList.html";
		final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
		ctx.setVariable("images", images);
		ctx.setVariable("fatherId", catId);
		ctx.setVariable("language", request.getSession(false).getAttribute("language"));
		ctx.setVariable("categoryName", catName);
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
