package org.jomaveger.tiger.core.scene_graph;

/**
 * @author jmvegas.gertrudix
 */
public class Node {
    
    public Node parentNode;
    public Node childNode;
    public Node prevNode;
    public Node nextNode;
    
    public Node() {
        this.parentNode = null;
	this.childNode = null;
	this.prevNode = this;
	this.nextNode = this;
    }
    
    public Node(Node node) {
        this.parentNode = null;
	this.childNode = null;
	this.prevNode = this;
	this.nextNode = this;
        this.AttachTo(node);
    }
    
    public Boolean HasParent() {
        return this.parentNode != null; 
    }
    
    public Boolean HasChild() {
        return this.childNode != null;
    }
    
    public Boolean IsFirstChild() {
        if (this.parentNode != null) {
            return (this.parentNode.childNode == this);
        } else {
            return Boolean.FALSE;
        }
    }
    
    public Boolean IsLastChild() {
        if (this.parentNode != null) {
            return (this.parentNode.childNode.prevNode == this);
        } else {
            return Boolean.FALSE;
        }			
    }
    
    // attach this node to a parent node
    public void AttachTo(Node newParent) {
        // if this node is already attached to another node, then detach
	if (this.parentNode != null) {
            Detach();
        }
			
	this.parentNode = newParent;

	if (this.parentNode.childNode != null) {
            this.prevNode = this.parentNode.childNode.prevNode;
            this.nextNode = this.parentNode.childNode;
            this.parentNode.childNode.prevNode.nextNode = this;
            this.parentNode.childNode.prevNode = this;
	} else {
            this.parentNode.childNode = this;      // this is the first child
	}
    }
    
    // attach a child to this node
    public void Attach(Node newChild) {
        // if the child node is already attached, then detach it
	if (newChild.HasParent()) {
            newChild.Detach();
        }
        newChild.parentNode = this;

        if (this.childNode != null) {
            newChild.prevNode = this.childNode.prevNode;
            newChild.nextNode = childNode;
            this.childNode.prevNode.nextNode = newChild;
            this.childNode.prevNode = newChild;
        } else {
            this.childNode = newChild;
        }
    }
    
    // detach node from parent
    public void Detach() {
        // if this node is the first child of the parent (first in list)
        // then the parent points to the next child in the list
        if (this.parentNode != null && parentNode.childNode == this) {
            if (this.nextNode != this) {
                this.parentNode.childNode = nextNode;
            } else {
                this.parentNode.childNode = null;      // no next child
            }
        }
        
        // get rid of links
        this.prevNode.nextNode = nextNode;
        this.nextNode.prevNode = prevNode;

        // now this node is not in the list
        prevNode = this;
        nextNode = this;
    }
    
    public Integer CountNodes() {
        if (this.childNode != null) {
            return this.childNode.CountNodes() + 1;
        } else {
            return 1;
        }
    }
}