package org.jomaveger.tiger.core.scene_graph;

/**
 *
 * @author jmvegas.gertrudix
 */
public enum SceneGraph {
    
    INSTANCE();
    
    private GameObject root;
    
    private SceneGraph() {
        this.root = null;
    }

    public GameObject FindRoot(GameObject obj) {
        // if the parameter has a parent node, return the root of the parent node
	if (obj.parentNode != null)
            return this.FindRoot((GameObject)obj.parentNode);

	this.root = obj;
	return this.root;
    }
    
    public void Update(Double elapsedTimeInSeconds) {
        if (this.root != null) {
            this.root.Prepare();
            this.root.Animate(elapsedTimeInSeconds);
        }
    }

    public void Render() {
        if (this.root != null) {
            this.root.Render();
        }
    }
}
