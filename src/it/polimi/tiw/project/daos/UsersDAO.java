package it.polimi.tiw.project.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.project.beans.User;

/**
 * DAO for users
 */
public class UsersDAO {
	private Connection connection;

	/**
	 * Constructor for the DAO
	 * @param connection	the <CODE>Connection</CODE> to the database
	 */
	public UsersDAO(Connection connection) {
		this.connection = connection;
	}
	
	/**
	 * Performs the login for a user
	 * @param username	username of the user
	 * @param hash	the hashed password of the user
	 * @return the user as a <CODE>User</CODE> object, <CODE>null</CODE> if the login fails
	 * @throws SQLException
	 */
	public User loginUser(String username, byte[] hash) throws SQLException {
		User user = null;
		String query = "SELECT * from user WHERE username = ?";
		try {
			PreparedStatement pstatement = this.connection.prepareStatement(query);
			pstatement.setString(1, username);
			ResultSet result = pstatement.executeQuery();
			result.next();
			byte[] temp = result.getBytes("password");
			// compare hashes of provided password and stored one
			for (int i = 0; i < temp.length; i++) {
				if (!(temp[i] == hash[i])) {
					return user;
				}
			}
			user = new User();
			user.setId(result.getInt("userid"));
			user.setUsername(result.getString("username"));
			user.setPassword(result.getString("password"));
		} catch (SQLException e) {
			throw new SQLException("Failed to login the user.");
		}
		return user;
	}
	
	/**
	 * Performs the registration for a new user
	 * @param username	the username of the new user
	 * @param password	the hashed password of the new user
	 * @return the new user as a <CODE>User</CODE> object, <CODE>null</CODE> otherwise
	 * @throws SQLException
	 */
	public User registerUser(String username, byte[] password) throws SQLException {		
		User newUser = null;
		
		String query = "INSERT INTO user (username, password) VALUES (?, ?)";
		try {
			PreparedStatement pstatement = this.connection.prepareStatement(query);
			
			pstatement.setString(1, username);
			pstatement.setBytes(2, password);
			pstatement.executeUpdate();
			newUser = new User();
			newUser.setUsername(username);
		} catch (SQLException e) {
		  throw new SQLException("Failed to register the user.");
		}
		return newUser;
	}
}
