package it.polimi.tiw.project.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import it.polimi.tiw.project.beans.Category;
import it.polimi.tiw.project.beans.CategoryTree;
import it.polimi.tiw.project.beans.Node;

/**
 * DAO for categories
 */
public class CategoriesDAO {
	private Connection connection;

	/**
	 * Constructor for the DAO
	 * @param connection	the <CODE>Connection</CODE> to the database
	 */
	public CategoriesDAO(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Gets all the categories as a <CODE>CategoryTree</CODE>
	 * @return the <CODE>CategoryTree</CODE>
	 * @throws SQLException
	 */
	public CategoryTree getCategories() throws SQLException {
		CategoryTree ct = new CategoryTree();
		Node root = new Node(null);
		TreeMap<Integer, TreeMap<Integer, Category>> categoriesMap = new TreeMap<Integer, TreeMap<Integer, Category>>();
		String query = "SELECT * FROM category";
		ResultSet result = null;
		PreparedStatement pStatement = null;
		try {
			pStatement = connection.prepareStatement(query);
			ResultSet rs = pStatement.executeQuery();
			/*
			 * retrieve all the categories in the db.
			 * create a treeMap (preserves key order) which keys are the ids of every category that is father of at least one child.
			 * the values of the treeMap are treeMaps containing all the categories children of the category which id is the key related to the value.
			 * 
			 */
			while (rs.next()) {
				Category c = new Category();
				c.setId(rs.getInt("category_id"));
				c.setName(rs.getString("name"));
				c.setIndex(rs.getString("index"));
				c.setFatherId(rs.getString("father_id"));
				TreeMap<Integer, Category> value = categoriesMap.get(Integer.parseInt(c.getFatherId()));
				// value already existing (the category with id = c.fatherId has at least one child already)
				if (value != null) {
					// since c is at least the second child, if c.fatherId = 0 then lonelyChild = false
					if ("0".equals(c.getFatherId())) {
						ct.setLonelyChild(false);
					}
					value.put(Integer.parseInt(c.getIndex()), c);
				} else {
					value =  new TreeMap<Integer, Category>();
					value.put(Integer.parseInt(c.getIndex()), c);
					categoriesMap.put(Integer.parseInt(c.getFatherId()), value);
				}
			}
			// create the CategoryTree if root has children
			if (categoriesMap.containsKey(0)) {
				List<Category> temp = new ArrayList<Category>(categoriesMap.get(0).values());
				for (Category c : temp) {
					root.addChild(new Node(c));
				}
				categoriesMap.remove(0);
				for (Node n : root.getChildren()) {
					populate(n, categoriesMap);
				}
			}
		} catch (SQLException e) {
			throw new SQLException("Failed to retrieve the categories.");
		} finally {
			try {
				if (result != null) {
					result.close();
				}
			} catch (Exception e1) {
				throw new SQLException("Cannot close result.");
			}
			try {
				if (pStatement != null) {
					pStatement.close();
				}
			} catch (Exception e1) {
				throw new SQLException("Cannot close statement.");
			}
		}
	
		ct.setRoot(root);
		ct.setMovableNodes();
		return ct;
	}
  
  	private void populate(Node father, TreeMap<Integer, TreeMap<Integer, Category>> categoriesMap){
  		// if the Node father is father, for each one of its children evaluate the index and remove the value from the treeMap, then add the children to the CategoryTree recursively
  		if (categoriesMap.containsKey(father.getCategory().getId())) {
  			List<Category> temp = new ArrayList<Category>(categoriesMap.get(father.getCategory().getId()).values());
  			for (Category c : temp) {
  				Node t = new Node(c);
  				String index = father.getCategory().getIndex();
  				if (father.getCategory() != null) {
  					index += "." + c.getIndex();
  				}
  				t.getCategory().setIndex(index);
  				father.addChild(t);
  			}
  			categoriesMap.remove(father.getCategory().getId());
  			for (Node n : father.getChildren()) {
  				populate(n, categoriesMap);
  			}
  		}
  	}
  
  	/**
  	 * Creates a new category in the database
  	 * @param name	name of the new category
  	 * @param fatherId	id of the father of the new category
  	 * @throws SQLException
  	 */
  	public void createCategory(String name, int fatherId) throws SQLException {
  		String query = "INSERT INTO category (name, `index`, father_id) VALUES (?, ?, ?)";

  		try {
  	  		int index = getNextIndex(fatherId);
  			PreparedStatement pstatement = connection.prepareStatement(query);
  			pstatement.setString(1, name);
  			pstatement.setInt(2, index);
  			pstatement.setObject(3, fatherId);
  			pstatement.executeUpdate();
  		} catch (SQLException e) {
  			throw new SQLException("Failed to create the category.");
  		}
  	}
    
  	/**
  	 * Gets the next index for a new child of a father
  	 * @param fatherId	the father id for which calculate the next index for a new child 
  	 * @return the next index to assign
  	 * @throws SQLException
  	 */
  	private int getNextIndex(int fatherId) throws SQLException {
  		// count the children of the category with id = fatherId and return the index for the next child to add
  		int count = 0;
  		try {
  			PreparedStatement pstatement = connection.prepareStatement("SELECT * FROM category WHERE father_id = ?");
  			pstatement.setInt(1, fatherId);
  			ResultSet result = pstatement.executeQuery();
			while (result.next()) {
				count++;
			}
  		} catch (SQLException e) {
  			throw new SQLException(e);
  		}
  		return count;
  	}

  	/**
  	 * Moves a category and its children under a new father
  	 * @param catId	id of the category to be moved
  	 * @param oldFatherId	id of the old father of the category to be moved
  	 * @param newFatherId	id of the new father under which move the category
  	 * @throws SQLException
  	 * @throws Exception
  	 */
  	public void moveCategory(int catId, int oldFatherId, int newFatherId) throws SQLException, Exception {
  		
  		CategoryTree ct = this.getCategories();
  		ArrayList<Node> nodes = ct.treeToList();
  		ArrayList<Integer> fathers;
  		
	
  		Node catToMove = null;
  		Node oldFather = null;
  		
  		int count;
  		if (oldFatherId == 0 && newFatherId == 0) {
  			// move a child of root as a last child of root (reorder)
  			count = 2;
  			oldFather = ct.getRoot();
  		} else if (oldFatherId == 0) {
  			// move a child of root
  			count = 1;
  			oldFather = ct.getRoot();
  		} else if (newFatherId == 0) {
  			// move a category as last child of root
  			count = 1;
  		} else {
  			// standard move operation
  			count = 0;
  		}
  		// find the nodes to move
  		for(Node n: nodes) {
  			if (count == 3) {
  				break;
  			}
  			if(n.getCategory().getId() == catId) {
  				catToMove= n;
  				count++;
  			} else if(n.getCategory().getId() == oldFatherId) {
  				oldFather = n;
  				count++;
  			} else if(n.getCategory().getId() == newFatherId) {
  				count++;
  			}
  		}

  		// retrieve the list of fathers of the category to move
  		fathers = ct.getFathersListOfChild(newFatherId);
  		
  		// if some node is not present or the user is trying to move a category inside one of its children, return
  		if (count < 3 || fathers.contains(catId)) {
  			throw new Exception("Failed to move the category.");
  		}
  		
  		try {
  			moveOrDeletePerform("move", ct, nodes, catToMove, oldFather, newFatherId, null);
  		} catch (SQLException e) {
  			throw new SQLException("Failed to move the category.");
  		} catch (Exception e) {
  			throw new Exception("Invalid action.");
  		}
		
  	}
  	
  	private void moveOrDeletePerform(String action, CategoryTree ct, List<Node> nodes, Node catToModify, Node oldFather, int newFatherId, List<Category> childrenToDelete) throws SQLException, Exception {

  		String query = "";
  		switch (action) {
  			case "move":
  				query = "UPDATE category SET father_id = ?, `index` = ? WHERE category_id = ?;";
  				break;
  			case "delete":
  				query = "DELETE FROM category WHERE category_id = ?;";
  				break;
			default:
				throw new Exception();
  		}
  		connection.setAutoCommit(false);
  		
  		int newIndex = getNextIndex(newFatherId);
  		if (newFatherId == 0 && oldFather.getCategory() == null || action.equals("delete")) {
  			newIndex--;
  		}
  		boolean rootChild = false;
  		int oldIndex;
  		// gets the current index of the category to move
  		if (catToModify.getCategory().getIndex().contains(".")) {
  			// the category to move is not direct child of root
  			oldIndex = Integer.parseInt(catToModify.getCategory().getIndex().substring(catToModify.getCategory().getIndex().lastIndexOf(".") + 1));
  		} else {
  			// the category to move is direct child of root
  			rootChild = true;
  			oldIndex = Integer.parseInt(catToModify.getCategory().getIndex());
  		}
  		
  		String queryChildren = "UPDATE category SET `index` = ? WHERE category_id = ?";
  		PreparedStatement pstatement = null;
  		try {
			pstatement = connection.prepareStatement(query);
  			if (action.equals("move")) {
  				// move the category
				pstatement.setInt(1, newFatherId);
				pstatement.setInt(2, newIndex);
				pstatement.setInt(3, catToModify.getCategory().getId());
				pstatement.executeUpdate();
  			} else if (action.equals("delete")) {
  				// delete the category
  	  			pstatement.setInt(1, catToModify.getCategory().getId());
  	  			pstatement.executeUpdate();
  	      
  	  			for (Category c : childrenToDelete) {
  	  				// delete all category's children
  	  				pstatement = connection.prepareStatement(query);
  	  				pstatement.setInt(1, c.getId());
  	  				pstatement.executeUpdate();
  	  			}
  			}
      
  			oldFather.getChildren().remove(catToModify);

  			for(Node n : oldFather.getChildren()) {
  				// update the indexes of the brothers that come after the category moved or deleted
  				if(!rootChild) {
	  				if(Integer.parseInt(n.getCategory().getIndex().substring(n.getCategory().getIndex().lastIndexOf(".") + 1)) > oldIndex) {
	  					pstatement = connection.prepareStatement(queryChildren);
	  					pstatement.setInt(1, Integer.parseInt(n.getCategory().getIndex().substring(n.getCategory().getIndex().lastIndexOf(".") + 1)) - 1);
	  					pstatement.setInt(2, n.getCategory().getId());
	  					pstatement.executeUpdate();    	
	  				}
  				} else {
  					if(Integer.parseInt(n.getCategory().getIndex()) > oldIndex) {
	  					pstatement = connection.prepareStatement(queryChildren);
	  					pstatement.setInt(1, Integer.parseInt(n.getCategory().getIndex()) - 1);
	  					pstatement.setInt(2, n.getCategory().getId());
	  					pstatement.executeUpdate();    	
	  				}
  				}
  			}
  			connection.commit();
  		} catch (SQLException e) {
  			connection.rollback();
  			throw new SQLException();
  		} finally {
  			try {
  				pstatement.close();
  			} catch (Exception e1) { }
  		}   
  	}
  	
  	/**
  	 * Deletes a category and all its children from the database
  	 * @param catId	id of the category to be deleted
  	 * @return list of <CODE>Category</CODE> which are the children to be deleted
  	 * @throws SQLException
  	 * @throws Exception
  	 */
  	public List<Category> deleteCategory(int catId, int fatherId) throws SQLException, Exception {
  		if (catId == 0) {
  			throw new Exception("Failed to delete the category.");
  		}
  		
  		CategoryTree ct = this.getCategories();
  		ArrayList<Node> nodes = ct.treeToList();
  		
  		connection.setAutoCommit(false);
	
  		Node catToDelete = null;
		Node father = null;
  		int count = 0;
  		if (fatherId == 0) {
  			// the father of the category to delete is the root
  			father = ct.getRoot();
  			count++;
  		}
  		// search the category to delete and its father (if not root)
  		for (Node n : nodes) {
  			if (count == 2) {
  				break;
  			}
  			if (n.getCategory().getId() == catId) {
  				catToDelete = n;
  				count++;
  			} else if (fatherId != 0 && n.getCategory().getId() == fatherId) {
  				father = n;
  				count++;
  			}
  		}
  		
  		if (catToDelete == null || father == null) {
  			throw new Exception("Failed to delete the category.");
  		}
  		
  		ArrayList<Category> childrenToDelete = new ArrayList<Category>();
  		ArrayList<Node> allButChildren = ct.getAllButChildren(String.valueOf(catToDelete.getCategory().getId()));
  		// collect all the children to be deleted
  		for (Node n : nodes) {
  			if (!allButChildren.contains(n)) {
  				childrenToDelete.add(n.getCategory());
  			}
  		}
  		
  		try {
  			moveOrDeletePerform("delete", ct, nodes, catToDelete, father, fatherId, childrenToDelete);
  		} catch (SQLException e) {
  			throw new SQLException("Failed to move the category.");
  		} catch (Exception e) {
  			throw new Exception("Invalid action.");
  		}
  		
  	  	return childrenToDelete;   
  	}
  	
  	/**
  	 * Renames a category in the database
  	 * @param catId	id of the category to be renamed
  	 * @param newName	new name for the category
  	 * @throws SQLException
  	 */
  	public void renameCategory(int catId, String newName) throws SQLException{
  		String query = "UPDATE category SET name = ? WHERE category_id = ?;";

  		try {
  			PreparedStatement pstatement = connection.prepareStatement(query);
  			pstatement.setString(1, newName);
  			pstatement.setInt(2, catId);
  			pstatement.executeUpdate();
  		} catch (SQLException e) {
  			throw new SQLException("Failed to rename the category.");
  		}
  	}
  	
  	/**
  	 * Checks if a category is present on the database
  	 * @param id	category id to check
  	 * @return 1 if present, -1 otherwise
  	 * @throws SQLException
  	 */
  	public int checkCategoryExists(int id) throws SQLException {
  		String query = "SELECT * FROM category WHERE category_id = ?";
  		
  		ResultSet rs = null;
		PreparedStatement pStatement = null;
		try {
			pStatement = connection.prepareStatement(query);
			pStatement.setInt(1, id);
			rs = pStatement.executeQuery();
			if (!rs.isBeforeFirst()) {
				return -1;
			} else {
				return 1;
			}
		} catch (SQLException e) {
			throw new SQLException("Failed to check if category exists.");
		}
  	}
}
