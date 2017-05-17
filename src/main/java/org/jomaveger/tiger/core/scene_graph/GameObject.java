package org.jomaveger.tiger.core.scene_graph;

/**
 * @author jmvegas.gertrudix
 */
public class GameObject extends Node {
    
    public Boolean isDead;
    
    public GameObject() {
        this.isDead = Boolean.FALSE;
    }
    
    public void Load() {
        
    }
    
    public void Unload() {
        
    }
    
    public void OnAnimate(Double elapsedTimeInSeconds) {
        
    }
    
    public void OnRender() {
        
    }
    
    public void OnCollision(GameObject collisionNode) {
        
    }
    
    public void OnPrepare() {
        ProcessCollisions(SceneGraph.INSTANCE.FindRoot(this));
    }
    
    public void Prepare() {
        if (!this.isDead) {
            this.OnPrepare();   // prepare this object

            if (this.HasChild()) // prepare children
            {
                ((GameObject) this.childNode).Prepare();
            }

            if (this.HasParent() && !this.IsLastChild()) // prepare siblings
            {
                ((GameObject) this.nextNode).Prepare();
            }
        }
    }
    
    public void ProcessCollisions(GameObject obj) {
        this.OnCollision(obj);   // perform this object's collision with obj

	// test child collisions with obj
	if (this.HasChild())
            ((GameObject)this.childNode).ProcessCollisions(obj);

	// test sibling collisions with obj
	if (this.HasParent() && !this.IsLastChild())
            ((GameObject)this.nextNode).ProcessCollisions(obj);

	// if obj has children, check collisions with these children
	if (obj.HasChild())
            ProcessCollisions((GameObject)(obj.childNode));

	// if obj has siblings, check collisions with these siblings
	if (obj.HasParent() && !obj.IsLastChild())
            ProcessCollisions((GameObject)(obj.nextNode));
    }
    
    public void Animate(Double elapsedTimeInSeconds) {
        if (!this.isDead) {
            this.OnAnimate(elapsedTimeInSeconds);         // animate this object

            // animate children
            if (this.HasChild()) {
                ((GameObject) this.childNode).Animate(elapsedTimeInSeconds);
            }

            // animate siblings
            if (this.HasParent() && !this.IsLastChild()) {
                ((GameObject) this.nextNode).Animate(elapsedTimeInSeconds);
            }
        }
    }
    
    public void Render() {
        if (!this.isDead) {
            this.OnRender();    // draw this object
		
            // draw children
            if (this.HasChild())
                ((GameObject)this.childNode).Render();

            // draw siblings
            if (this.HasParent() && !this.IsLastChild())
                ((GameObject)this.nextNode).Render();
        }
    }
}
