package org.jomaveger.texture;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author jmvegas.gertrudix
 */
public class TextureManager {

    private final ArrayList<Texture> textures;
    private final TextureLoader loader;

    public TextureManager() {
        textures = new ArrayList<>();
        loader = new TextureLoader();
    }

    public Texture getNormalImage(String name, boolean mipmap, boolean useAnisotropicFilter) throws IOException {
        return loadImage(name, 1, 1, mipmap, useAnisotropicFilter);
    }

    public Texture getMirrorImage(String name, boolean mipmap, boolean useAnisotropicFilter) throws IOException {
        return loadImage(name, -1, 1, mipmap, useAnisotropicFilter);
    }

    public Texture getFlippedImage(String name, boolean mipmap, boolean useAnisotropicFilter) throws IOException {
        return loadImage(name, 1, -1, mipmap, useAnisotropicFilter);
    }

    private Texture loadImage(String name, int x, int y, boolean mipmap, boolean useAnisotropicFilter) throws IOException {
        Texture tex = loader.getTexture(name, x, y, mipmap, useAnisotropicFilter);
        setTexture(tex);
        return tex;
    }

    public void setTexture(Texture tex) {
        textures.add(tex);
    }

    public Texture getTexture(int index) {
        return textures.get(index);
    }

    public ArrayList<Texture> getTexture() {
        return textures;
    }

    public int getSize() {
        return textures.size();
    }
    
    public TextureLoader getLoader() {
        return loader;
    }
}
