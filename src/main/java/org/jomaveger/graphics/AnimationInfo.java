package org.jomaveger.graphics;

/**
 * @author jmvegas.gertrudix
 */
public class AnimationInfo {

    private String animName;			// This stores the name of the animation (Jump, Pain, etc..)
    private int startFrame;				// This stores the first frame number for this animation
    private int endFrame;				// This stores the last frame number for this animation
    private int loopingFrames;
    private int framesPerSecond;

    public AnimationInfo() {
        setAnimName(null);
        setStartFrame(0);
        setEndFrame(0);
        setLoopingFrames(0);
        setFramesPerSecond(0);
    }

    public AnimationInfo(String animFrame, int startFrame, int endFrame) {
        setAnimName(animFrame);
        setStartFrame(startFrame);
        setEndFrame(endFrame);
    }

    public void setAnimName(String animName) {
        this.animName = animName;
    }

    public String getAnimName() {
        return animName;
    }

    public void setStartFrame(int startFrame) {
        this.startFrame = startFrame;
    }

    public int getStartFrame() {
        return startFrame;
    }
    
    public void setEndFrame(int endFrame) {
        this.endFrame = endFrame;
    }

    public int getEndFrame() {
        return endFrame;
    }

    public void setLoopingFrames(int loopingFrames) {
        this.loopingFrames = loopingFrames;
    }

    public int getLoopingFrames() {
        return loopingFrames;
    }

    public void setFramesPerSecond(int framesPerSecond) {
        this.framesPerSecond = framesPerSecond;
    }

    public int getFramesPerSecond() {
        return framesPerSecond;
    }
}
