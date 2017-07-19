package org.jomaveger.q3render;

/**
 * @author jmvegas.gertrudix
 */
public class QLeafFace {
	int face;
	
	public QLeafFace( int face ) {
		this.face = face;
	}
	
        @Override
	public String toString() {
		return String.valueOf( face );
	}
}
