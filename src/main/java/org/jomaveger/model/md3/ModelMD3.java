package org.jomaveger.model.md3;

import org.jomaveger.graphics.Model3D;
import org.jomaveger.model.md3.LoaderMD3.TagMD3;

/**
 * @author jmvegas.gertrudix
 */
public class ModelMD3 extends Model3D {

    protected ModelMD3[] links;				// This stores a list of pointers that are linked to this model
    protected TagMD3[] tags;			// This stores all the tags for the model animations
    protected int numOfTags;			// This stores the number of tags in the model

    public LoaderMD3 loaderMD3;			// This object allows us to load the.md3 and .shader file

    public ModelMD3() {
        super();
        loaderMD3 = new LoaderMD3();
    }

    public void setLinks(ModelMD3[] pLinks) {
        this.links = pLinks;
    }

    public void setLinks(ModelMD3 pLinks, int index) {
        this.links[index] = pLinks;
    }

    public void setNumLinks(int total) {
        links = new ModelMD3[total];
        for (int i = 0; i < total; i++) {
            links[i] = null;
        }
    }

    public ModelMD3[] getLinks() {
        return links;
    }

    public ModelMD3 getLinks(int index) {
        return links[index];
    }

    public void setTags(TagMD3[] pTags) {
        this.tags = pTags;
    }

    public void setTags(TagMD3 pTags, int index) {
        this.tags[index] = pTags;
    }

    public void setNumTags(int total) {
        tags = new TagMD3[total];
    }

    public TagMD3[] getTags() {
        return tags;
    }

    public TagMD3 getTags(int index) {
        return tags[index];
    }

    public void setNumOfTags(int numOfTags) {
        this.numOfTags = numOfTags;
    }

    public int getNumOfTags() {
        return numOfTags;
    }

    @Override
    public boolean load(String fileName) {
        return loaderMD3.importMD3(this, fileName);
    }

    public boolean loadSkin(String fileSkin) {
        return loaderMD3.loadSkin(this, fileSkin);
    }

    public boolean loadShader(String fileShader) {
        return loaderMD3.loadShader(this, fileShader);
    }

}
