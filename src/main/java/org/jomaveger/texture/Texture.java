package org.jomaveger.texture;

import org.lwjgl.opengl.GL11;

/**
 * @author jmvegas.gertrudix
 */
public class Texture {

    private final int target;

    private final int textureID;

    private int height;

    private int width;

    private int texWidth;

    private int texHeight;

    private float widthRatio;

    private float heightRatio;

    private String name;

    private boolean useAnisotropic = false;

    public Texture(int target, int textureID) {
        this.target = target;
        this.textureID = textureID;
    }

    public void bind() {
        GL11.glBindTexture(target, textureID);
    }

    public void bind(int target, int textureID) {
        GL11.glBindTexture(target, textureID);
    }

    public void setHeight(int height) {
        this.height = height;
        setHeight();
    }

    public int getTexID() {
        return textureID;
    }

    public void setWidth(int width) {
        this.width = width;
        setWidth();
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

    public int getImageWidth() {
        return width;
    }

    public float getHeight() {
        return heightRatio;
    }

    public float getWidth() {
        return widthRatio;
    }

    public void setTextureHeight(int texHeight) {
        this.texHeight = texHeight;
        setHeight();
    }

    public void setTextureWidth(int texWidth) {
        this.texWidth = texWidth;
        setWidth();
    }

    private void setHeight() {
        if (texHeight != 0) {
            heightRatio = ((float) height) / texHeight;
        }
    }

    private void setWidth() {
        if (texWidth != 0) {
            widthRatio = ((float) width) / texWidth;
        }
    }

    public void setUseAnisotropic(boolean useAnisotropic) {
        this.useAnisotropic = useAnisotropic;
    }

    public boolean isUseAnisotropic() {
        return useAnisotropic;
    }
}
