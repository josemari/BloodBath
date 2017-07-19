package org.jomaveger.q3render;

import java.util.Arrays;

/**
 * @author jmvegas.gertrudix
 */
public class QVisData {
	int n_vecs;
	int sz_vecs;
	short[] vecs;
	
	public QVisData( int n_vecs, int sz_vecs, short[] vecs ) {
		this.n_vecs = n_vecs;
		this.sz_vecs = sz_vecs;
		this.vecs = vecs;
	}
	
        @Override
	public String toString() {
		return n_vecs + "\t" + sz_vecs + "\t" + Arrays.toString( vecs );
	}
}
