package org.jomaveger.q3render;

import java.util.Arrays;

/**
 * @author jmvegas.gertrudix
 */
public class QNode {
	int plane;
	int[] children;
	int[] mins;
	int[] maxs;
	
	public QNode( int plane, int[] children, int[] mins, int[] maxs ) {
		this.plane = plane;
		this.children = children;
		this.mins = mins;
		this.maxs = maxs;
	}
	
        @Override
	public String toString() {
		return plane + "\t" + Arrays.toString( children ) + "\t" + Arrays.toString( mins ) + "\t" + Arrays.toString( maxs );
	}
}
