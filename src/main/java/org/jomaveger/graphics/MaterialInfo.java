package org.jomaveger.graphics;

import java.nio.FloatBuffer;
import org.jomaveger.texture.Texture;
import org.jomaveger.util.BufferUtil;

/**
 * @author jmvegas.gertrudix
 */
public class MaterialInfo {

    private String name;	// Identificao do material
    private Texture tex;
    private String texFile = "";
    private byte[] colorByte;
    private float[] colorConverted;
    private FloatBuffer ka;	// Ambiente
    private FloatBuffer kd;	// Difuso
    private FloatBuffer ks;	// Especular
    private FloatBuffer ke;	// Emisso
    private float spec;	// Fator de especularidade
    public static final int SIZE_FLOAT = 4;
    private int texureId;				// the texture ID
    private float uTile;				// u tiling of texture  
    private float vTile;				// v tiling of texture	
    private float uOffset;			    // u offset of texture
    private float vOffset;				// v offset of texture

    public MaterialInfo() {
        setKa(new float[SIZE_FLOAT]);
        setKd(new float[SIZE_FLOAT]);
        setKs(new float[SIZE_FLOAT]);
        setKe(new float[SIZE_FLOAT]);
        setSpec(0.0f);
        setColor(new byte[3]);
        colorConverted = new float[3];
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setKa(float[] ka) {
        this.ka = BufferUtil.INSTANCE.AllocFloats(ka);
    }

    public FloatBuffer getKa() {
        return ka;
    }
    
    public void setKd(float[] kd) {
        this.kd = BufferUtil.INSTANCE.AllocFloats(kd);
    }
    
    public FloatBuffer getKd() {
        return kd;
    }
    
    public void setKs(float[] ks) {
        this.ks = BufferUtil.INSTANCE.AllocFloats(ks);
    }

    public FloatBuffer getKs() {
        return ks;
    }

    public void setKe(float[] ke) {
        this.ke = BufferUtil.INSTANCE.AllocFloats(ke);
    }

    public void setKe(float ke) {
        this.ke.put(0, ke);
        this.ke.put(1, ke);
        this.ke.put(2, ke);
    }

    public FloatBuffer getKe() {
        return ke;
    }

    public void setSpec(float spec) {
        this.spec = spec;
    }

    public void setAlpha(float spec) {
        ka.put(3, spec);
        kd.put(3, spec);
        ks.put(3, spec);
        ke.put(3, spec);
    }
    
    public float getSpec() {
        return spec;
    }

    public void setTex(Texture tex) {
        this.tex = tex;
    }

    public Texture getTex() {
        return tex;
    }

    public void setTexFile(String texFile) {
        this.texFile = texFile;
    }

    public String getTexFile() {
        return texFile;
    }

    public void setColor(byte[] color) {
        this.colorByte = color;
    }

    public void setColor() {
        this.colorConverted[0] = colorByte[0] & 0xff;
        this.colorConverted[1] = colorByte[1] & 0xff;
        this.colorConverted[2] = colorByte[2] & 0xff;
    }

    public byte[] getColor() {
        return colorByte;
    }

    public float[] getColorConverted() {
        setColor();
        this.colorConverted[0] /= 255;
        this.colorConverted[1] /= 255;
        this.colorConverted[2] /= 255;
        return colorConverted;
    }

    public void setTexureId(int texureId) {
        this.texureId = texureId;
    }

    public int getTexureId() {
        return texureId;
    }

    public void setUTile(float uTile) {
        this.uTile = uTile;
    }

    public float getUTile() {
        return uTile;
    }

    public void setVTile(float vTile) {
        this.vTile = vTile;
    }

    public float getVTile() {
        return vTile;
    }

    public void setUOffset(float uOffset) {
        this.uOffset = uOffset;
    }

    public float getUOffset() {
        return uOffset;
    }

    public void setVOffset(float vOffset) {
        this.vOffset = vOffset;
    }

    public float getVOffset() {
        return vOffset;
    }
}
