package org.jomaveger.model.md2;

import org.apache.log4j.Logger;
import org.jomaveger.graphics.AnimationInfo;
import org.jomaveger.graphics.Object3D;
import org.jomaveger.math.Vector;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex3f;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.Sys;


/**
 * @author jmvegas.gertrudix
 */
public class ModelQuake2 {
    
    private final static Logger LOGGER = Logger.getLogger(ModelQuake2.class);

    private ModelMD2 model;
    private static float animationSpeed;
    private float lastTime = 0.0f;

    public ModelQuake2(String textureName) {
        model = new ModelMD2(textureName);
        animationSpeed = 5.0f;
    }

    public ModelMD2 getModel() {
        return model;
    }

    public void setModel(ModelMD2 model) {
        this.model = model;
    }

    public void load(String fileName) {
        boolean load = model.load(fileName);
        if (!load) {
            LOGGER.info("It was not possible to load the md2 model. The application will shut down.");
            System.exit(-1);
        }
    }

    ////////////*** NEW *** ////////// *** NEW *** ///////////// *** NEW *** ////////////////////
    ///////////////////////////////// RETURN CURRENT TIME \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This returns time t for the interpolation between the current and next key frame
    /////
    ///////////////////////////////// RETURN CURRENT TIME \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    private float getCurrentTime(int nextFrame) {
        float elapsedTime = 0.0f;
        // This function is very similar to finding the frames per second.
        // Instead of checking when we reach a second, we check if we reach
        // 1 second / our animation speed. (1000 ms / kAnimationSpeed).
        // That's how we know when we need to switch to the next key frame.
        // In the process, we get the t value for how we are at to going to the
        // next animation key frame.  We use time to do the interpolation, that way
        // it runs the same speed on any persons computer, regardless of their specs.
        // It might look chopier on a junky computer, but the key frames still be
        // changing the same time as the other persons, it will just be not as smooth
        // of a transition between each frame.  The more frames per second we get, the
        // smoother the animation will be.

        // Get the current time in milliseconds
        float time = (float) Sys.getTime();

        // Find the time that has elapsed since the last time that was stored
        elapsedTime = time - lastTime;

        // To find the current t we divide the elapsed time by the ratio of 1 second / our anim speed.
        // Since we aren't using 1 second as our t = 1, we need to divide the speed by 1000
        // milliseconds to get our new ratio, which is a 5th of a second.
        float t = elapsedTime / (1000.0f / animationSpeed);
        
        // If our elapsed time goes over a 5th of a second, we start over and go to the next key frame
        if (elapsedTime >= (1000.0f / animationSpeed)) {
            // Set our current frame to the next key frame (which could be the start of the anim)
            model.setCurrentFrame(nextFrame);

            // Set our last time to the current time just like we would when getting our FPS.
            lastTime = time;
        }
        // Return the time t so we can plug this into our interpolation.
        // Set the t for the model to be used in interpolation
        return t;
    }

    ///////////////////////////////// ANIMATE MD2 MODEL \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This draws and animates the .md2 model by interpoloated key frame animation
    /////
    ///////////////////////////////// ANIMATE MD2 MODEL \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    public void draw() {
        // Now comes the juice of our tutorial.  Fear not, this is actually very intuitive
        // if you drool over it for a while (stay away from the keyboard though...).
        // What's going on here is, we are getting our current animation that we are
        // on, finding the current frame of that animation that we are on, then interpolating
        // between that frame and the next frame.  To make a smooth constant animation when
        // we get to the end frame, we interpolate between the last frame of the animation 
        // and the first frame of the animation.  That way, if we are doing the running 
        // animation let's say, when the last frame of the running animation is hit, we don't
        // have a huge jerk when going back to the first frame of that animation.  Remember,
        // because we have the texture and face information stored in the first frame of our
        // animation, we need to reference back to this frame every time when drawing the
        // model.  The only thing the other frames store is the vertices, but no information
        // about them.

        // Make sure we have valid objects just in case. (size() is in the vector class)
        if (model.getObject().size() <= 0) {
            return;
        }

        // Here we grab the current animation that we are on from our model's animation list
        AnimationInfo anim = model.getAnimations(model.getCurrentAnim());

        // This gives us the current frame we are on.  We mod the current frame plus
        // 1 by the current animations end frame to make sure the next frame is valid.
        // If the next frame is past our end frame, then we go back to zero.  We check this next.
        int nextFrame = (model.getCurrentFrame() + 1) % anim.getEndFrame();

        // If the next frame is zero, that means that we need to start the animation over.
        // To do this, we set nextFrame to the starting frame of this animation.
        if (nextFrame == 0) {
            nextFrame = anim.getStartFrame();
        }

        // Get the current key frame we are on
        Object3D frameObject = model.getObject(model.getCurrentFrame());

        // Get the next key frame we are interpolating too
        Object3D nextFrameObject = model.getObject(nextFrame);

        // Get the first key frame so we have an address to the texture and face information
        Object3D firstFrameObject = model.getObject(0);

        // Next, we want to get the current time that we are interpolating by.  Remember,
        // if t = 0 then we are at the beginning of the animation, where if t = 1 we are at the end.
        // Anything from 0 to 1 can be thought of as a percentage from 0 to 100 percent complete.
        //float t = returnCurrentTime(model, nextFrame);
        float t = getCurrentTime(nextFrame);

        // Start rendering lines or triangles, depending on our current rendering mode (Lft Mouse Btn)
        glBegin(GL_TRIANGLES);

        // Go through all of the faces (polygons) of the current frame and draw them
        for (int j = 0; j < firstFrameObject.getNumFaces(); j++) {
            // Go through each corner of the triangle and draw it.
            for (int whichVertex = 0; whichVertex < 3; whichVertex++) {
                // Get the index for each point of the face
                int vertIndex = firstFrameObject.getFace(j).getVertices(whichVertex);

                // Get the index for each texture coordinate for this face
                int texIndex = firstFrameObject.getFace(j).getTexCoords(whichVertex);

                // Make sure there was a UVW map applied to the object.  Notice that
                // we use the first frame to check if we have texture coordinates because
                // none of the other frames hold this information, just the first by design.
                if (firstFrameObject.getNumTexcoords() > 0) {
                    // Pass in the texture coordinate for this vertex

                    glTexCoord2f(firstFrameObject.getTexcoords(texIndex).s, firstFrameObject.getTexcoords(texIndex).t);
                }

                // Now we get to the interpolation part! (*Bites his nails*)
                // Below, we first store the vertex we are working on for the current
                // frame and the frame we are interpolating too.  Next, we use the
                // linear interpolation equation to smoothly transition from one
                // key frame to the next.
                // Store the current and next frame's vertex
                Vector point1 = new Vector(frameObject.getVertices(vertIndex));
                Vector point2 = new Vector(nextFrameObject.getVertices(vertIndex));

                // By using the equation: p(t) = p0 + t(p1 - p0), with a time t
                // passed in, we create a new vertex that is closer to the next key frame.
                glVertex3f(point1.x + t * (point2.x - point1.x), // Find the interpolated X
                        point1.y + t * (point2.y - point1.y), // Find the interpolated Y
                        point1.z + t * (point2.z - point1.z));// Find the interpolated Z
            }
        }

        // Stop rendering the triangles
        glEnd();
    }

    public static float getAnimationSpeed() {
        return animationSpeed;
    }

    public static void setAnimationSpeed(float animationSpeed) {
        ModelQuake2.animationSpeed = animationSpeed;
    }
}
