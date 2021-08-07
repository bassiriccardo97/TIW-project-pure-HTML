package it.polimi.tiw.project.controllers;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.project.utils.ErrorDispatcher;

/**
 * Servlet implementation class RegisterOrLogin
 */
@WebServlet("/RegisterOrLogin")
public class RegisterOrLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterOrLogin() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		String servlet;
		
		if ("Signup".equals(action) || "Registrati".equals(action) || "Registrarse".equals(action) || "Einloggen".equals(action) || "S'identifier".equals(action)) {
			servlet = "Register";
		} else if ("Login".equals(action) || "Accedi".equals(action) || "Acceso".equals(action) || "Anmeldung".equals(action) || "Connexion".equals(action)) {
			servlet = "Login";
		} else {
			ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_BAD_REQUEST, "Need to login or register.");
			return;
		}

		RequestDispatcher requestDispatcher = request.getRequestDispatcher(servlet);
		requestDispatcher.forward(request, response);
	}

}
