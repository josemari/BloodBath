package org.jomaveger.graphics;

import java.io.IOException;
import java.util.Vector;

/**
 * @author jmvegas.gertrudix
 */
public class Model3D {

    protected int numOfObjects;					// The number of objects in the model
    protected int numOfMaterials;				// The number of materials for the model

    protected Vector<MaterialInfo> materials;	// The list of material information (Textures and colors)
    protected Vector<Object3D> object;			// The object list for our model
    protected Vector<AnimationInfo> animations; // The list of animations 

    private int currentAnim;					// The current index into pAnimations list (NEW)
    private int currentFrame;					// The current frame of the current animation (NEW)
    private int nextFrame;						// The next frame of animation to interpolate too
    private float ratioTime;					// The ratio of 0.0f to 1.0f between each key frame
    private float lastTime;						// This stores the last time that was stored

    public Model3D() {
        numOfObjects = 0;
        numOfMaterials = 0;

        materials = new Vector<>();
        object = new Vector<>();
        animations = new Vector<>();
        setNextFrame(0);
        setRatioTime(0f);
        setLastTime(0);
    }

    public boolean load(String fileName) throws IOException {
        return false;
    }

    public void setNumOfObjects(int numOfObjects) {
        this.numOfObjects = numOfObjects;
    }

    public void addNumOfObjects(int numOfObjects) {
        this.numOfObjects += numOfObjects;
    }

    public int getNumOfObjects() {
        return numOfObjects;
    }

    public void setNumOfMaterials(int numOfMaterials) {
        this.numOfMaterials = numOfMaterials;
    }

    public void addNumOfMaterials(int numOfMaterials) {
        this.numOfMaterials += numOfMaterials;
    }

    public int getNumOfMaterials() {
        return numOfMaterials;
    }

    public void setMaterials(Vector<MaterialInfo> pMaterials) {
        this.materials = pMaterials;
    }

    public void addMaterials(MaterialInfo pMaterials) {
        this.materials.add(pMaterials);
    }

    public Vector<MaterialInfo> getMaterials() {
        return materials;
    }

    public MaterialInfo getMaterials(int index) {
        return materials.get(index);
    }

    public void setObject(Vector<Object3D> pObject) {
        this.object = pObject;
    }

    public void addObject(Object3D pObject) {
        this.object.add(pObject);
    }

    public Vector<Object3D> getObject() {
        return object;
    }

    public Object3D getObject(int index) {
        return object.get(index);
    }

    public void setAnimations(Vector<AnimationInfo> pAnimations) {
        this.animations = pAnimations;
    }

    public void addAnimations(AnimationInfo animation) {
        animations.add(animation);
    }

    public AnimationInfo getAnimations(int index) {
        return animations.get(index);
    }

    public Vector<AnimationInfo> getAnimations() {
        return animations;
    }

    public int getNumOfAnimations() {
        return animations.size();
    }

    public void setCurrentAnim(int currentAnim) {
        this.currentAnim = currentAnim;
    }

    public int getCurrentAnim() {
        return currentAnim;
    }

    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public int findMaterial(String nome) {
        for (int i = 0; i < materials.size(); i++) {
            if (nome.equalsIgnoreCase(getMaterials(i).getName())) {
                return i;
            }
        }
        return -1;
    }

    public void setNextFrame(int nextFrame) {
        this.nextFrame = nextFrame;
    }

    public int getNextFrame() {
        return nextFrame;
    }

    public void setRatioTime(float t) {
        this.ratioTime = t;
    }

    public float getRatioTime() {
        return ratioTime;
    }

    public void setLastTime(float lastTime) {
        this.lastTime = lastTime;
    }

    public float getLastTime() {
        return lastTime;
    }
}
