package org.jomaveger.q3render;

import java.util.Arrays;

/**
 * @author jmvegas.gertrudix
 */
public class QPlane {
	float[] normal;
	float dist;
	
	public QPlane( float[] normal, float dist ) {
		this.normal = normal;
		this.dist = dist;
	}
	
        @Override
	public String toString() {
		return Arrays.toString( normal ) + "\t" + dist;
	}
}
