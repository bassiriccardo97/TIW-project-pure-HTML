package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.daos.UsersDAO;
import it.polimi.tiw.project.utils.ConnectionHandler;
import it.polimi.tiw.project.utils.ErrorDispatcher;


/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login() {
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
	    int sessionTimeout = 0;
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		try {
			sessionTimeout = Integer.parseInt(getServletContext().getInitParameter("sessionTimeout"));
		} catch (NumberFormatException e) {
			ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid sessionTimeout format.");
			return;
		}

		if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
			ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
			return;
		}

		UsersDAO uDAO = new UsersDAO(connection);
		User user = null;
		try {
			// hash the password
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
			user = uDAO.loginUser(username, hash);
		} catch (SQLException | NoSuchAlgorithmException e) {
			ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}

		if (user == null) {
			ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_BAD_REQUEST, "User or password not correct.");
			return;
		}
		HttpSession session = request.getSession(true);
		session.setAttribute("user", user);
		session.setMaxInactiveInterval(sessionTimeout);
		session.setAttribute("language", Locale.getDefault().getLanguage());
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
