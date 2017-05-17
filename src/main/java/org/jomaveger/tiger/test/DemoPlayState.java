package org.jomaveger.tiger.test;

import org.jomaveger.tiger.core.scene_graph.SceneGraph;
import org.jomaveger.tiger.core.state.GameState;

/**
 * @author jmvegas.gertrudix
 */
public class DemoPlayState extends GameState {

    @Override
    public void EnterState() {
        NodeQuads node1 = new NodeQuads();
	NodeTriangles node2 = new NodeTriangles();
	node1.Attach(node2);
	SceneGraph.INSTANCE.FindRoot(node2);        
    }

    @Override
    public void LeaveState() {
    }

    @Override
    public void Update(Double elapsedTimeInSeconds) {
        SceneGraph.INSTANCE.Update(elapsedTimeInSeconds);
    }

    @Override
    public void Render() {
        SceneGraph.INSTANCE.Render();
    }
    
}
