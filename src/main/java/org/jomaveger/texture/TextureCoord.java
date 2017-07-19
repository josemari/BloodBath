package org.jomaveger.texture;

import org.jomaveger.math.Vector;

/**
 * @author jmvegas.gertrudix
 */
public class TextureCoord {

    public float s;
    public float t;

    public TextureCoord() {
        setS(0);
        setT(0);
    }

    public TextureCoord(Vector tex) {
        this(tex.x, tex.y);
    }

    public TextureCoord(float u, float v) {
        setS(u);
        setT(v);
    }

    public void setS(float s) {
        this.s = s;
    }

    public void setT(float t) {
        this.t = t;
    }
}
