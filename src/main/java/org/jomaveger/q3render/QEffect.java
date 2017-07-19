package org.jomaveger.q3render;

/**
 * @author jmvegas.gertrudix
 */
public class QEffect {
	String name;
	int brush;
	int unknown;
	
	public QEffect( String name, int brush, int unknown ) {
		this.name = name;
		this.brush = brush;
		this.unknown = unknown;
	}
	
        @Override
	public String toString() {
		return name + "\t" + brush + "\t" + unknown;
	}
}
