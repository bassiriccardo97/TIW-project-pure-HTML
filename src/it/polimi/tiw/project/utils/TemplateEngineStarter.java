package it.polimi.tiw.project.utils;

import javax.servlet.ServletContext;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

/**
 * The <CODE>TemplateEngineStarter</CODE> allows to start the <CODE>TemplateEngine</CODE>
 */
public class TemplateEngineStarter {
	
	/**
	 * Starts the <CODE>TemplateEngine</CODE> related to a <CODE>ServletContext</CODE>
	 * @param context	the <CODE>ServletContext</CODE> for which the <CODE>TemplateEngine</CODE> is needed
	 * @return	the started <CODE>TemplateEngine</CODE>
	 */
	public static TemplateEngine getEngine(ServletContext context) {
		TemplateEngine te = new TemplateEngine();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
		templateResolver.setCharacterEncoding("UTF-8");
	    templateResolver.setTemplateMode(TemplateMode.HTML);
	    templateResolver.setSuffix(".html");
	    te.setTemplateResolver(templateResolver);
	    
	    return te;
	}
}
