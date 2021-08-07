package it.polimi.tiw.project.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * A <CODE>Node</CODE> is an element of the <CODE>CategoryTree</CODE>, contains a <CODE>Category</CODE> and it is linked to its children
 */
public class Node {
	private List<Node> children = null;
    private Category cat;
	private boolean movable;

	/**
	 * Constructor
	 * @param c	<CODE>Category</CODE> related to the <CODE>Node</CODE>
	 */
    public Node(Category c){
        this.children = new ArrayList<>();
        this.cat = c;
    }

    /* GETTERS */
    
    /**
     * Getter for the <CODE>children</CODE> list
     * @return list of children categories
     */
    public List<Node> getChildren() {
    	return this.children;
    }
    
    /**
     * Getter for <CODE>cat</CODE>
     * @return the <CODE>Category</CODE> related to the <CODE>Node</CODE>
     */
    public Category getCategory() {
    	return this.cat;
    }
	
    /**
     * Getter for <CODE>movable</CODE>
     * @return <CODE>movable</CODE> (<CODE>true</CODE> if the <CODE>Category</CODE> can be moved, <CODE>false</CODE> otherwise)
     */
	public boolean isMovable() {
		return movable;
	}
    
    /* SETTERS */
    
	/**
	 * Setter for the <CODE>children</CODE> list
	 * @param nodeList	list of the children of the <CODE>Category</CODE>
	 */
    public void setChildren(List<Node> nodeList) {
    	this.children = nodeList;
    }
    
    /**
     * Setter for <CODE>cat</CODE>
     * @param c	the <CODE>Category</CODE> related to the <CODE>Node</CODE>
     */
    public void setCategory(Category c) {
    	this.cat = c;
    }

    /**
     * Setter for <CODE>movable</CODE>
     * @param movable	<CODE>true</CODE> if the <CODE>Category</CODE> can be moved, <CODE>false</CODE> otherwise
     */
	public void setMovable(boolean movable) {
		this.movable = movable;
	}
	
    /**
     * Adds a child <CODE>Node</CODE> to the list of children categories
     * @param child	the child <CODE>Node</CODE>
     */
    public void addChild(Node child){
        children.add(child);
    }
}
