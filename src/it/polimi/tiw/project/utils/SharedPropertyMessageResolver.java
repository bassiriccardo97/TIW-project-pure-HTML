package it.polimi.tiw.project.utils;

import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.ServletContextTemplateResource;

/**
 * The <CODE>MultiPathMessageResolver</CODE> class extends <CODE>StandardMessageResolver</CODE> in order
 * to customize the place where we can search for the property files for
 * customization by overriding the method <CODE>resolveMessagesForTemplate</CODE>.
 */
public class SharedPropertyMessageResolver extends StandardMessageResolver {

	private ServletContext context;
	private String directory;
	private String fileName;
	private HttpSession session;

	/**
	 * Constructor of <CODE>SharedPropertyMessageResolver</CODE>
	 * @param context	the <CODE>ServletContext</CODE> for which the <CODE>SharedPropertyMessageResolver</CODE> is needed 
	 * @param path	the <CODE>directory</CODE> in which the HTML files are contained
	 * @param fileName	the <CODE>fileName</CODE> of the HTML file
	 * @param session	the <CODE>HttpSession</CODE> of the client
	 */
	public SharedPropertyMessageResolver(ServletContext context, String path, String fileName, HttpSession session) {
		super();
		this.context = context;
		this.directory = path;
		this.fileName = fileName;
		this.session = session;
	}

	/**
	 * @see StandardMessageResolver#resolveMessagesForTemplate(String template, ITemplateResource templateResource, Locale locale)
	 */
	@Override
	protected Map<String, String> resolveMessagesForTemplate(String template, ITemplateResource templateResource, Locale locale) {
		String finalpath = "/WEB-INF/" + directory + "/" + fileName + ".html";
		templateResource = new ServletContextTemplateResource(context, finalpath, null);
		
		// if the user logs out the language selected is still valid, while if the user has never logged in or changed the language, it is the locale one
		if(this.session != null && this.session.getAttribute("language") != null) {
		    Locale new_locale = new Locale((String) this.session.getAttribute("language"));
		    Locale.setDefault(new_locale);
		    return super.resolveMessagesForTemplate(finalpath, templateResource, new_locale);
		}
		return super.resolveMessagesForTemplate(finalpath, templateResource, locale);
	}
		
}
