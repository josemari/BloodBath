package org.jomaveger.ztest;

import java.io.*;
import org.jomaveger.core.GameCore;
import org.jomaveger.input.GameAction;
import org.jomaveger.model.ModelLoader;
import org.jomaveger.model.ModelType;
import org.jomaveger.model.md3.ModelMD3;
import org.jomaveger.model.md3.ModelQuake3;
import org.lwjgl.input.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

/**
 * @author jmvegas.gertrudix
 */
public class TestMD3Animation extends GameCore {

    public static void main(String[] args) {
        new TestMD3Animation().run();
    }

    private static final String MODEL_PATH = "lara";
    private static final String MODEL_NAME = "lara";
    private static final String GUN_NAME = "railgun";

    public GameAction moveLeft;
    public GameAction moveRight;
    public GameAction moveUp;
    public GameAction moveDown;

    public GameAction changeUpper;
    public GameAction changeLower;

    public GameAction drawMode;
    public GameAction modTex;
    public GameAction debug;

    int g_ViewMode = GL_TRIANGLES;	// We want the default drawing mode to be normal
    boolean g_bLighting = true;		// Turn lighting on initially
    float g_RotateX = 0.0f;		// This is the current value at which the model is rotated
    float g_RotationSpeed = 0.0005f;            // This is the speed that our model rotates.  (-speed rotates left)
    float g_TranslationZ = -120.0f;		// This stores our distance away from the model
    boolean g_RenderMode = true;		// This tells us if we are in wire frame mode or not

    private int modo = GL_REPLACE;

    public ModelQuake3 g_World;

    // This tells us if we want to display the yellow debug lines for our nodes (Space Bar)
    boolean g_bDisplayNodes = false;

    @Override
    public void init() throws IOException {
        super.init();

        screen.setTitle("MD3 Loader");

        createGameActions();

        g_World = ModelLoader.INSTANCE.loadMD3Model(MODEL_PATH, MODEL_NAME, GUN_NAME, ModelType.MD3);

////////////*** NEW *** ////////// *** NEW *** ///////////// *** NEW *** ////////////////////
        // When we get here, the character should have everything loaded.  Before going on,
        // we want to set the current animation for the torso and the legs.
        // Set the standing animation for the torso
        g_World.setTorsoAnimation("TORSO_STAND");

        // Set the walking animation for the legs
        g_World.setLegsAnimation("LEGS_WALK");

        //////////// *** NEW *** ////////// *** NEW *** ///////////// *** NEW *** ////////////////////
        glEnable(GL_CULL_FACE);								// Turn back face culling on
        glCullFace(GL_FRONT);								// Quake3 uses front face culling apparently

        glEnable(GL_TEXTURE_2D);							// Enables Texture Mapping
        glEnable(GL_DEPTH_TEST);							// Enables Depth Testing

        // To make our model render somewhat faster, we do some front back culling.
        // It seems that Quake2 orders their polygons clock-wise.
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, modo);
    }

    @Override
    public void update(float elapsedTime) {
        checkSystemInput();

        if (!isPaused()) {
            checkGameInput();

        }
    }

    @Override
    public void checkGameInput() {
        if (changeUpper.isPressed()) {
            increaseCharacterAnimation(g_World, ModelQuake3.kUpper);
        }

        if (changeLower.isPressed()) {
            increaseCharacterAnimation(g_World, ModelQuake3.kLower);
        }

        if (moveUp.isPressed()) {
            g_TranslationZ += .2f;
        }

        if (moveDown.isPressed()) {
            g_TranslationZ -= .2f;
        }

        if (moveLeft.isPressed()) {
            g_RotationSpeed -= 0.001f;
        }

        if (moveRight.isPressed()) {
            g_RotationSpeed += 0.001f;
        }

        if (modTex.isPressed()) {
            if (modo == GL_REPLACE) {
                modo = GL_MODULATE;
            } else {
                modo = GL_REPLACE;
            }
            // Ajusta o modo de aplicao da textura
            glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, modo);
        }
        if (drawMode.isPressed()) {
            g_RenderMode = !g_RenderMode;	// Change the rendering mode

            // Change the rendering mode to and from lines or triangles
            if (g_RenderMode) {
                // Render the triangles in fill mode		
                glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            } else {
                // Render the triangles in wire frame mode
                glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            }

        }

        if (debug.isPressed()) {
            g_bDisplayNodes = !g_bDisplayNodes;
        }

    }

    @Override
    public void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);	// Clear The Screen And The Depth Buffer
        glLoadIdentity();									// Reset The matrix

        //////////// *** NEW *** ////////// *** NEW *** ///////////// *** NEW *** ////////////////////
        // As you can see below, to draw the model it's a piece of cake with our CModelMD3 class.
        // You'll notice that we added a translation variable to our camera's z position.  This
        // allows us to zoom in and zoom out without adding some camera code.
        // Give OpenGL our position,			then view,			then up vector
        gluLookAt(0, 5.5f, g_TranslationZ, 0, 5.5f, 0, 0, 1, 0);

        // We want the model to rotate around the axis so we give it a rotation
        // value, then increase/decrease it. You can rotate right or left with the arrow keys.
        glRotatef(g_RotateX, 0, 1.0f, 0);			// Rotate the object around the Y-Axis
        g_RotateX += g_RotationSpeed;				// Increase the speed of rotation

        // Now comes the moment we have all been waiting for!  Below we draw our character.
        g_World.draw();

    }

    ////////////*** NEW *** ////////// *** NEW *** ///////////// *** NEW *** ////////////////////
    //////////////////////////INCREASE CHARACTER ANIMATION \\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This function increases the model's torso or legs animation
    /////
    //////////////////////////INCREASE CHARACTER ANIMATION \\\\\\\\\\\\\\\\\\\\\\\\\\*
    public void increaseCharacterAnimation(ModelQuake3 pCharacter, int whichPart) {

        ModelMD3 pModel, pUpper, pLower;

        // This function doesn't have much to do with the character animation, but I
        // created it so that we can cycle through each of the animations to see how
        // they all look.  You can press the right and left mouse buttons to cycle through
        // the torso and leg animations.  If the current animation is the end animation,
        // it cycles back to the first animation.  This function takes the character you
        // want, then the define (kLower, kUpper) that tells which part to change.
        // Here we store pointers to the legs and torso, so we can display their current anim name
        pLower = pCharacter.getModel(ModelQuake3.kLower);
        pUpper = pCharacter.getModel(ModelQuake3.kUpper);

        // This line gives us a pointer to the model that we want to change
        pModel = pCharacter.getModel(whichPart);

        // To cycle through the animations, we just increase the model's current animation
        // by 1.  You'll notice that we also mod this result by the total number of
        // animations in our model, to make sure we go back to the beginning once we reach
        // the end of our animation list.  
        // Increase the current animation and mod it by the max animations
        pModel.setCurrentAnim((pModel.getCurrentAnim() + 1) % (pModel.getNumOfAnimations()));

        // Set the current frame to be the starting frame of the new animation
        pModel.setNextFrame(pModel.getAnimations(pModel.getCurrentAnim()).getStartFrame());

        // (* NOTE *) Currently when changing animations, the character doesn't immediately
        // change to the next animation, but waits till it finishes the current animation
        // and slowly blends into the next one.  If you want an immediate switch, change
        // the pModel-nextFrame to pModel->currentFrame.
        // Display the current animations in our window's title bar
        screen.setTitle("Animation: Lower: " + pLower.getAnimations(pLower.getCurrentAnim()).getAnimName()
                + "  Upper: " + pUpper.getAnimations(pUpper.getCurrentAnim()).getAnimName());

        // Set the window's title bar to our new string of animation names
    }

    ////////////*** NEW *** ////////// *** NEW *** ///////////// *** NEW *** ////////////////////
    @Override
    public void createGameActions() {
        super.createGameActions();
        moveLeft = new GameAction("moveLeft", GameAction.NORMAL, Keyboard.KEY_LEFT);
        moveRight = new GameAction("moveRight", GameAction.NORMAL, Keyboard.KEY_RIGHT);
        moveUp = new GameAction("moveUp", GameAction.NORMAL, Keyboard.KEY_UP);
        moveDown = new GameAction("moveDown", GameAction.NORMAL, Keyboard.KEY_DOWN);

        changeUpper = new GameAction("changeUpper", GameAction.DETECT_INITIAL_PRESS_ONLY, Keyboard.KEY_U);
        changeLower = new GameAction("changeLower", GameAction.DETECT_INITIAL_PRESS_ONLY, Keyboard.KEY_L);

        drawMode = new GameAction("drawMode", GameAction.DETECT_INITIAL_PRESS_ONLY, Keyboard.KEY_T);
        modTex = new GameAction("modTex", GameAction.DETECT_INITIAL_PRESS_ONLY, Keyboard.KEY_M);
        debug = new GameAction("debug", GameAction.DETECT_INITIAL_PRESS_ONLY, Keyboard.KEY_SPACE);
    }
}
