package it.polimi.tiw.project.utils;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The <CODE>ErrorDispatcher</CODE> allows to dispatch <CODE>Servlet</CODE>'s errors forwarding the errors to the <CODE>ErrorHandler</CODE> <CODE>Servlet</CODE>
 */
public class ErrorDispatcher {

	/**
	 * Forwards the error to the <CODE>ErrorHandler</CODE> <CODE>Servlet</CODE>
	 * @param context	the <CODE>ServletContext</CODE> from which the error is generated
	 * @param request	the <CODE>HttpServletRequest</CODE> of the <CODE>Servlet</CODE> from which the error is generated
	 * @param response	the <CODE>HttpServletResponse</CODE> of the <CODE>Servlet</CODE> from which the error is generated
	 * @param code	the <CODE>HttpServletResponse</CODE> code of the error
	 * @param message	the error message
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void forward(ServletContext context, HttpServletRequest request, HttpServletResponse response, int code, String message) throws ServletException, IOException {
		RequestDispatcher dispatcher = context.getRequestDispatcher("/ErrorHandler");
    	request.setAttribute("errorCode", code);
    	request.setAttribute("errorMessage", message);
        dispatcher.forward(request, response);
	}
	
}
