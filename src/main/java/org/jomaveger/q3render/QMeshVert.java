package org.jomaveger.q3render;

/**
 * @author jmvegas.gertrudix
 */
public class QMeshVert {
	int offset;
	
	public QMeshVert( int offset ) {
		this.offset = offset;
	}
	
        @Override
	public String toString() {
		return String.valueOf( offset );
	}
}
