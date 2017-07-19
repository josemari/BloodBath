package org.jomaveger.q3render;

/**
 * @author jmvegas.gertrudix
 */
public class QBrush {
	int brushside;
	int n_brushsides;
	int texture;
	
	public QBrush( int brushside, int n_brushsides, int texture ) {
		this.brushside = brushside;
		this.n_brushsides = n_brushsides;
		this.texture = texture;
	}
	
        @Override
	public String toString() {
		return brushside + "\t" + n_brushsides + "\t" + texture;
	}
}
