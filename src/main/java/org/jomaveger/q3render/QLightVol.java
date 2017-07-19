package org.jomaveger.q3render;

import java.util.Arrays;

/**
 * @author jmvegas.gertrudix
 */
public class QLightVol {
	short[] ambient;
	short[] directional;
	short[] dir;
	
	public QLightVol( short[] ambient, short[] directional, short[] dir ) {
		this.ambient = ambient;
		this.directional = directional;
		this.dir = dir;
	}
	
        @Override
	public String toString() {
		return Arrays.toString( ambient ) + "\t" + Arrays.toString( directional ) + "\t" + Arrays.toString( dir );
	}
}
