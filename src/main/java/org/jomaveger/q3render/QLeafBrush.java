package org.jomaveger.q3render;

/**
 * @author jmvegas.gertrudix
 */
public class QLeafBrush {
	int brush;
	
	public QLeafBrush( int brush ) {
		this.brush = brush;
	}
	
        @Override
	public String toString() {
		return String.valueOf( brush );
	}
}
