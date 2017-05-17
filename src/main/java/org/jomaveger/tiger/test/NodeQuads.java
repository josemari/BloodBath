package org.jomaveger.tiger.test;

import org.jomaveger.tiger.core.scene_graph.GameObject;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex3i;
import static org.lwjgl.opengl.GL11.glColor3f;

/**
 * @author jmvegas.gertrudix
 */
public class NodeQuads extends GameObject {
    
    @Override
    public void OnRender() {
        glBegin(GL_QUADS);
        glColor3f(1.0f, 0.0f, 0.0f);    glVertex3i(50, 200, 0);
        glColor3f(0.0f, 1.0f, 0.0f);    glVertex3i(250, 200, 0);
        glColor3f(0.0f, 0.0f, 1.0f);    glVertex3i(250, 350, 0);
        glColor3f(1.0f, 1.0f, 1.0f);    glVertex3i(50, 350, 0);
        glEnd();
    }
}
