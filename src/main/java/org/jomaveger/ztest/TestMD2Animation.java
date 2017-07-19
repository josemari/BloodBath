package org.jomaveger.ztest;

import java.io.*;
import org.jomaveger.core.GameCore;
import org.jomaveger.input.GameAction;
import org.jomaveger.model.ModelLoader;
import org.jomaveger.model.ModelType;
import org.jomaveger.model.md2.ModelQuake2;
import org.lwjgl.input.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

/**
 * @author jmvegas.gertrudix
 */
public class TestMD2Animation extends GameCore {

    public static void main(String[] args) {
        new TestMD2Animation().run();
    }

    private static final String FILE_NAME = "models/Ogros.md2";
    private static final String TEXTURE_NAME = "models/igdosh.jpg";

    public static final float kAnimationSpeed = 5.0f;

    public GameAction moveLeft;
    public GameAction moveRight;
    public GameAction moveUp;
    public GameAction moveDown;
    public GameAction zoomIn;
    public GameAction zoomOut;
    public GameAction drawMode;
    public GameAction modTex;
    public GameAction debug;

    int g_ViewMode = GL_TRIANGLES;	// We want the default drawing mode to be normal
    boolean g_bLighting = true;		// Turn lighting on initially
    float g_RotateX = 0.0f;		// This is the current value at which the model is rotated
    float g_RotationSpeed = 0.0f;	// This is the speed that our model rotates.  (-speed rotates left)
    private int modo = GL_MODULATE;
    boolean g_RenderMode = true;	// This tells us if we are in wire frame mode or not

    // This will store our 3ds scene that we will pass into our octree
    //public ModelQuake2 g_World = new ModelQuake2(TEXTURE_NAME);
    public ModelQuake2 g_World;

    // This tells us if we want to display the yellow debug lines for our nodes (Space Bar)
    boolean g_bDisplayNodes = false;

    @Override
    public void init() throws IOException {
        super.init();

        screen.setTitle("MD2 Animation");

        createGameActions();

        // Here, we turn on a lighting and enable lighting.  We don't need to
        // set anything else for lighting because we will just take the defaults.
        // We also want color, so we turn that on
        // Habilita Z-Buffer
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_LIGHT0);			// Turn on a light with defaults set
        glEnable(GL_LIGHTING);			// Turn on lighting
        glEnable(GL_COLOR_MATERIAL);		// Allow color

        // To make our model render somewhat faster, we do some front back culling.
        // It seems that Quake2 orders their polygons clock-wise.
        // Seleciona o modo de aplicao da textura
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, modo);

        glEnable(GL_TEXTURE_2D);
        g_World = ModelLoader.INSTANCE.loadMD2Model(FILE_NAME, TEXTURE_NAME, ModelType.MD2);
    }

    @Override
    protected void update(float elapsedTime) {

        checkSystemInput();

        if (!isPaused()) {
            checkGameInput();

        }
    }

    @Override
    public void checkGameInput() {
        super.checkGameInput();

        if (moveUp.isPressed()) {
            // To cycle through the animations, we just increase the model's current animation
            // by 1.  You'll notice that we also mod this result by the total number of
            // animations in our model, to make sure we go back to the beginning once we reach
            // the end of our animation list.  

            // Increase the current animation and mod it by the max animations
            g_World.getModel().setCurrentAnim((g_World.getModel().getCurrentAnim() + 1) % (g_World.getModel().getAnimations().size()));

            // Set the current frame to be the starting frame of the new animation
            g_World.getModel().setCurrentFrame(g_World.getModel().getAnimations(g_World.getModel().getCurrentAnim()).getStartFrame());

            screen.setTitle("Animation: " + g_World.getModel().getAnimations(g_World.getModel().getCurrentAnim()).getAnimName());
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
        glLoadIdentity();					// Reset The matrix

        // Give OpenGL our position, then view,	then up vector
        gluLookAt(0, 1.5f, 100, 0, 0.5f, 0, 0, 1, 0);

        glRotatef(g_RotateX, 0, 1.0f, 0);					// Rotate the object around the Y-Axis
        g_RotateX += g_RotationSpeed;						// Increase the speed of rotation if any

        //////////// *** NEW *** ////////// *** NEW *** ///////////// *** NEW *** ////////////////////
        // This is where we call our animation function to draw and animate our character.
        // You can pass in any model into here and it will draw and animate it.  Of course,
        // it would be a good idea to stick this function in your model class.
        g_World.draw();

        // Render the cubed nodes to visualize the octree (in wire frame mode)
        if (g_bDisplayNodes) {
            for (int j = 0; j < g_World.getModel().getObject().size(); j++) {
                g_World.getModel().getObject(j).drawBoundingBox();
            }
        }
    }

    @Override
    public void createGameActions() {
        super.createGameActions();
        moveLeft = new GameAction("moveLeft", GameAction.NORMAL, Keyboard.KEY_LEFT);
        moveRight = new GameAction("moveRight", GameAction.NORMAL, Keyboard.KEY_RIGHT);
        moveUp = new GameAction("moveUp", GameAction.DETECT_INITIAL_PRESS_ONLY, Keyboard.KEY_UP);
        moveDown = new GameAction("moveDown", GameAction.NORMAL, Keyboard.KEY_DOWN);
        zoomIn = new GameAction("zoomIn", GameAction.NORMAL, Keyboard.KEY_HOME);
        zoomOut = new GameAction("zoomOut", GameAction.NORMAL, Keyboard.KEY_END);

        drawMode = new GameAction("drawMode", GameAction.DETECT_INITIAL_PRESS_ONLY, Keyboard.KEY_T);
        modTex = new GameAction("modTex", GameAction.DETECT_INITIAL_PRESS_ONLY, Keyboard.KEY_M);
        debug = new GameAction("debug", GameAction.DETECT_INITIAL_PRESS_ONLY, Keyboard.KEY_SPACE);
    }
}
