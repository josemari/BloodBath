package org.jomaveger.q3render;

/**
 * @author jmvegas.gertrudix
 */
public class QBrushSide {
	int plane;
	int texture;
	
	public QBrushSide( int plane, int texture ) {
		this.plane = plane;
		this.texture = texture;
	}
	
        @Override
	public String toString() {
		return plane + "\t" + texture;
	}
}
