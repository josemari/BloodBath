package org.jomaveger.tiger.test;

import org.jomaveger.tiger.core.scene_graph.GameObject;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex3i;

/**
 * @author jmvegas.gertrudix
 */
public class NodeTriangles extends GameObject {
    
    @Override
    public void OnRender() {
        glBegin(GL_TRIANGLES);
	glColor3f(1.0f, 0.0f, 0.0f);  glVertex3i(400, 350, 0);
	glColor3f(0.0f, 1.0f, 0.0f);  glVertex3i(500, 200, 0);
	glColor3f(0.0f, 0.0f, 1.0f);  glVertex3i(600, 350, 0);
	glEnd();
    }    
}
