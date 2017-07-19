package org.jomaveger.model.md2;

import java.util.Vector;
import org.apache.log4j.Logger;
import org.jomaveger.graphics.AnimationInfo;
import org.jomaveger.graphics.Model3D;

/**
 * @author jmvegas.gertrudix
 */
public class ModelMD2 extends Model3D {
    
    private final static Logger LOGGER = Logger.getLogger(ModelMD2.class);

    private int numOfAnimations;				// The number of animations in this model 
    private int currentAnim;					// The current index into pAnimations list (NEW)
    private int currentFrame;					// The current frame of the current animation (NEW)
    private final String textureName;

    private Vector<AnimationInfo> animations; // The list of animations 

    private LoaderMD2 loaderMD2;

    public ModelMD2(String textureName) {
        super();

        numOfAnimations = 0;
        animations = new Vector<>();
        this.textureName = textureName;
    }

    public void setNumOfAnimations(int numOfAnimations) {
        this.numOfAnimations = numOfAnimations;
    }

    @Override
    public int getNumOfAnimations() {
        return numOfAnimations;
    }

    @Override
    public void setAnimations(Vector<AnimationInfo> pAnimations) {
        this.animations = pAnimations;
    }

    @Override
    public void addAnimations(AnimationInfo animation) {
        animations.add(animation);
    }

    @Override
    public AnimationInfo getAnimations(int index) {
        return animations.get(index);
    }

    @Override
    public Vector<AnimationInfo> getAnimations() {
        return animations;
    }

    @Override
    public void setCurrentAnim(int currentAnim) {
        this.currentAnim = currentAnim;
    }

    @Override
    public int getCurrentAnim() {
        return currentAnim;
    }

    @Override
    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    @Override
    public int getCurrentFrame() {
        return currentFrame;
    }

    @Override
    public boolean load(String fileName) {
        loaderMD2 = new LoaderMD2();
        try {
            loaderMD2.importMD2(this, fileName, textureName);
            return true;
        } catch (Exception ex) {
            LOGGER.info("Fail loading md2 model - ", ex);
            return false;
        }

    }
}
