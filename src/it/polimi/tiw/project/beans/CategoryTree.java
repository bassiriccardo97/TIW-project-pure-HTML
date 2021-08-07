package it.polimi.tiw.project.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * A <CODE>CategoryTree</CODE> contains a <CODE>Node</CODE> which is the <CODE>root</CODE> of the tree
 */
public class CategoryTree {

	private Node root;
	private boolean lonelyChild = true;
	private boolean movable = false;
	private int generation = 0;
	
	
	/* GETTERS */
	
	/**
	 * Getter for <CODE>CategoryTree</CODE> <CODE>root</CODE>
	 * @return <CODE>CategoryTree</CODE> <CODE>root</CODE>
	 */
	public Node getRoot() {
		return root;
	}

	/**
	 * Getter for <CODE>lonelyChild</CODE> attribute
	 * @return lonelyChild (<CODE>true</CODE> if <CODE>root</CODE> has only one child, <CODE>false</CODE> otherwise)
	 */
	public boolean isLonelyChild() {
		return lonelyChild;
	}

	/* SETTERS */
	
	/**
	 * Setter for <CODE>CategoryTree</CODE> <CODE>root</CODE>
	 * @param root	<CODE>root</CODE> of the tree
	 */
	public void setRoot(Node root) {
		this.root = root;
	}

	/**
	 * Setter for <CODE>lonelyChild</CODE> attribute
	 * @param lonelyChild	<CODE>true</CODE> if <CODE>root</CODE> has only one child, <CODE>false</CODE> otherwise
	 */
	public void setLonelyChild(boolean lonelyChild) {
		this.lonelyChild = lonelyChild;
	}
	
	/**
	 * Traverses all the <CODE>CategoryTree</CODE> and returns it as a list
	 * @return <CODE>CategoryTree</CODE> as a list
	 */
	public ArrayList<Node> treeToList() {
		ArrayList<Node> treeList = new ArrayList<Node>();
		traverseAll(treeList, this.root);
		return treeList;
	}
	
	private void traverseAll(List<Node> treeList, Node child){
		if (child.getCategory() != null) {
			treeList.add(child);
		}
	    for(Node each : child.getChildren()){
	        traverseAll(treeList, each);
	    }
	}
	
	/**
	 * Traverses all the <CODE>CategoryTree</CODE> and returns it as a list, but appends as first element a <CODE>Node</CODE> with a <CODE>Category</CODE> which <CODE>name</CODE> is "New Category" (also in it, es, de, and fr, depending on the language set by the user)
	 * @param language	the language set by the user
	 * @return <CODE>CategoryTree</CODE> as list of <CODE>Category</CODE>
	 * @see <CODE>treeToList</CODE>
	 */
	public ArrayList<Node> treeToListCreateCategory(String language) {
		ArrayList<Node> treeToListCreateCategory = new ArrayList<Node>();
		Category c = new Category();
		String newCategory;
		switch (language) {
			case "it":
				newCategory = "Nuova Categoria";
				break;
			case "fr":
				newCategory = "Nouvelle Cat\u00E9gorie";
				break;
			case "es":
				newCategory = "Nueva Categor\u00EDa";
				break;
			case "de":
				newCategory = "Neue Kategorie";
				break;
			default:
				newCategory = "New Category";
				break;
		}
		c.setName(newCategory);
		c.setId(0);
		treeToListCreateCategory.add(new Node(c));
		traverseAllCreateCategory(treeToListCreateCategory, this.root);
		return treeToListCreateCategory;
	}
	
	private void traverseAllCreateCategory(List<Node> treeToListCreateCategory, Node child){
		if (child.getCategory() != null) {
			treeToListCreateCategory.add(child);
		}
	    for(Node each : child.getChildren()){
	    	traverseAllCreateCategory(treeToListCreateCategory, each);
	    }
	}
	
	/**
	 * Traverses all the <CODE>CategoryTree</CODE> and returns a list of all <CODE>Node</CODE> not children of one <CODE>Category</CODE>
	 * @param catToMoveId	<CODE>id</CODE> of the <CODE>Category</CODE> which children must not be added to the list
	 * @return list of <CODE>Node</CODE> not children of one <CODE>Category</CODE>
	 */
	public ArrayList<Node> getAllButChildren(String catToMoveId) {
		ArrayList<Node> allButChildren = new ArrayList<Node>();
		for (Node each : this.root.getChildren()) {
			traverseAllButChildren(allButChildren, catToMoveId, each);	
		}
		return allButChildren;
	}
	
	private void traverseAllButChildren(List<Node> allButChildren, String catToMoveId, Node child) {
		if (child.getCategory().getFatherId().equals(catToMoveId) || catToMoveId.equals(String.valueOf(child.getCategory().getId()))) {
			return;
		}
		allButChildren.add(child);
		for(Node each : child.getChildren()) {
			traverseAllButChildren(allButChildren, catToMoveId, each);
		}
	}

	/**
	 * Traverses all the <CODE>CategoryTree</CODE> and sets if a <CODE>Node</CODE> can be moved or not, setting the <CODE>movable</CODE> attribute respectively <CODE>true</CODE> or <CODE>false</CODE>
	 */
	public void setMovableNodes() {
		/*
		 * if root has only one child (this.lonelyChild = true) this.movable is left false and that single child is set Node.movable = false and since it is the first generation child, this.generation is incremented of 1.
		 * if root has more than one child this.movable is set to true. 
		 */
		if (this.lonelyChild) {
			this.root.getChildren().get(0).setMovable(false);
			this.generation++;
			traverseAllSetMovables(this.root.getChildren().get(0));
		} else {
			this.movable = true;
			this.generation++;
			for (Node n : this.root.getChildren()) {
				n.setMovable(true);
				traverseAllSetMovables(n);
			}
		}
	}
	
	private void traverseAllSetMovables(Node n) {
		/*
		 * if all the tree is movable, or n has more than one child or n is the second generation from root, then all the children are movable.
		 * if none of the above is true (n is the only child of root and has only one child itself), the child of n is set not movable, then all next children are movable because the first if will be always true, since generation > 1
		 */
		if (this.movable || (!this.movable && n.getChildren().size() > 1) || this.generation > 1) {
			this.movable = true;
			this.generation++;
			for (Node n1 : n.getChildren()) {
				n1.setMovable(true);
				traverseAllSetMovables(n1);
			}
		} else {
			if (!n.getChildren().isEmpty()) {
				this.generation++;
				n.getChildren().get(0).setMovable(false);
				traverseAllSetMovables(n.getChildren().get(0));
			}
		}
	}
	
	/**
	 * Creates a list of <CODE>Integer</CODE> which are the <CODE>id</CODE> of all the fathers of a <CODE>Category</CODE> 
	 * @param catId	<CODE>id</CODE> of the <CODE>Category</CODE> for which search all the fathers
	 * @return list of <CODE>id</CODE> of all the fathers
	 */
	public ArrayList<Integer> getFathersListOfChild(int catId) {
		/*
		 * Gets the tree as list, then starting form the node which category has id = catId, searches the father of the current category considered (the last father found).
		 * it stops when the father is the root node (id = 0)
		 * in the end there is the list containing the chain of fathers of the category with id = catId.
		 */
		ArrayList<Integer> fathers = new ArrayList<Integer>();
		ArrayList<Node> treeList = this.treeToList();
		boolean stop = false;
		Integer tempId = catId;
		
		if (catId == 0) {
			return fathers;
		}
		
		while(!stop) {
			for (Node n : treeList) {
				if (n.getCategory().getId() == tempId) {
					if (Integer.parseInt(n.getCategory().getFatherId()) == 0) {
						fathers.add(0);
						stop = true;
					} else {
						fathers.add(Integer.parseInt(n.getCategory().getFatherId()));
						tempId = Integer.parseInt(n.getCategory().getFatherId());
					}
				}
			}
		}
		
		return fathers;
	}
}
