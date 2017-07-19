package org.jomaveger.ztest;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.ARBMultitexture.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.io.IOException;
import org.jomaveger.input.Camera;
import org.jomaveger.core.GameCore;
import org.jomaveger.input.GameAction;
import org.jomaveger.math.Vector;
import org.jomaveger.model.ModelLoader;
import org.jomaveger.model.ModelType;
import org.jomaveger.model.bsp.Quake3BSP;
import org.jomaveger.timer.FPSCounter;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * @author jmvegas.gertrudix
 */
public class TestBSP extends GameCore {

    public static void main(String[] args) {
        new TestBSP().run();
    }

    // This is how fast our camera moves
    private final float SPEED = 300.0f;

    public GameAction moveLeft;
    public GameAction moveRight;
    public GameAction moveUp;
    public GameAction moveDown;
    public GameAction drawMode;
    public GameAction modTex;

    boolean g_bLighting = true;							// Turn lighting on initially

    // This is the speed that our model rotates.  (-speed rotates left)
    private final int modo = GL_MODULATE;

    // This will store our 3ds scene that we will pass into our octree
    public Quake3BSP level = new Quake3BSP();

    private Camera camera;
    private boolean movedBack;

    @Override
    public void init() throws IOException {
        super.init();

        glMatrixMode(GL_PROJECTION);		// Select The Projection Matrix
        glLoadIdentity();			// Reset The Projection Matrix
        // Calculate The Aspect Ratio Of The Window
        // FOV		// Ratio				//  The farthest distance before it stops drawing)
        gluPerspective(70.0f, screen.getWidth() / screen.getHeight(), 10.0f, 4000.0f);

        glMatrixMode(GL_MODELVIEW);							// Select The Modelview Matrix
        glLoadIdentity();									// Reset The Modelview Matrix

        screen.setTitle("BSP Loader");

        // Create the camera with mouse look enabled.
        camera = new Camera(true);
        
        level = ModelLoader.INSTANCE.loadQuake3BSPModel("Config.ini", ModelType.BSP);

        createGameActions();

        // Position the camera to the starting point since we have
        // not read in the entities yet, which gives the starting points.
        camera.setPosition(80, 320, 55, 80, 320, 155, 0, 1, 0);

        // Turn on depth testing and texture mapping
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        // Enable front face culling, since that's what Quake3 does
        glCullFace(GL_FRONT);
        glEnable(GL_CULL_FACE);
        // To make our model render somewhat faster, we do some front back culling.
        // It seems that Quake2 orders their polygons clock-wise.

        // Seleciona o modo de aplicao da textura
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, modo);

        Mouse.setGrabbed(true);
    }

    @Override
    public void update(float elapsedTime) {
        checkSystemInput();
        
        if (!isPaused()) {
            if (!Mouse.isGrabbed()) {
                Mouse.setGrabbed(true);
            }
            camera.update();
            checkGameInput(elapsedTime);

        } else {
            Mouse.setGrabbed(false);
        }
    }

    public void checkGameInput(float elapsedTime) {
        super.checkGameInput(); // Checamos se as teclas foram pressionadas ou no.
        movedBack = false;
        
        // Once we have the frame interval, we find the current speed
        float speed = (SPEED * elapsedTime);

        // Before we move our camera we want to store the old position.  We then use 
        // this data to test collision detection.
        Vector oldPosition = new Vector(camera.getPosition());
        Vector oldView = new Vector(camera.getView());

        if (moveLeft.isPressed()) {
            camera.strafe(-speed);
        }

        if (moveRight.isPressed()) {
            camera.strafe(speed);
        }

        if (moveUp.isPressed()) {
            camera.move(speed);
        }

        if (moveDown.isPressed()) {
            camera.move(-speed);
            movedBack = true;
        }

        if (modTex.isPressed()) {
            level.setHasTextures(!level.isTextures());
            if (!level.isTextures()) // If we don't want lightmaps
            {
                glActiveTextureARB(GL_TEXTURE0_ARB);		// Turn the second texture off
                glDisable(GL_TEXTURE_2D);
            } else {
                glEnable(GL_TEXTURE_2D);
            }
        }

        if (drawMode.isPressed()) {
            level.setHasTextures(!level.isTextures());
            level.setHasLightmaps(false);
            // Change the rendering mode to and from lines or triangles
            if (level.isTextures()) {
                // Render the triangles in fill mode		
                glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            } else {
                // Render the triangles in wire frame mode
                glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            }
        }
        
        // Now that we moved, let's get the current position and test our movement
        // vector against the level data to see if there is a collision.
        Vector currentPosition = new Vector(camera.getPosition());

        // Here we call our function TraceSphere() to check our movement vector (last
        // and current position) against the world.  We pass in a radius for our sphere
        // of 25.  If there is anything in the range of our sphere, then we collide.
        Vector newPosition = level.traceBox(oldPosition, currentPosition, new Vector(-20, -50, -20), new Vector(20, 50, 20));

        if (level.isM_bCollided() && movedBack) {
            if(newPosition.x == oldPosition.x || newPosition.z == oldPosition.z)
                camera.setView(oldView);
        }
        // Set the new position that was returned from our trace function
        camera.setPosition(newPosition);
    }

    @Override
    public void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);	// Clear The Screen And The Depth Buffer
        glLoadIdentity();									// Reset The matrix

        // Give OpenGL our camera coordinates to look at
        camera.look();

        // Since we are using frustum culling to only draw the visible BSP leafs,
        // we need to calculate the frustum every frame.  This needs to happen
        // right after we position our camera.  Now the frustum planes can be defined.
        GameCore.gFrustum.calculateFrustum();

        // Easy as pie - just call our render function.  We pass in the camera
        // because in later tutorials we will need it's position when we start
        // dealing with the BSP nodes and leafs.
        level.renderLevel(camera.getPosition());

        screen.setTitle("FPS: " + FPSCounter.get() + " VisibleFaces: " + Quake3BSP.visibleFaces);
    }

    @Override
    public void createGameActions() {
        super.createGameActions();

        moveLeft = new GameAction("moveLeft", GameAction.NORMAL, Keyboard.KEY_LEFT);
        moveRight = new GameAction("moveRight", GameAction.NORMAL, Keyboard.KEY_RIGHT);
        moveUp = new GameAction("moveUp", GameAction.NORMAL, Keyboard.KEY_UP);
        moveDown = new GameAction("moveDown", GameAction.NORMAL, Keyboard.KEY_DOWN);
        drawMode = new GameAction("drawMode", GameAction.DETECT_INITIAL_PRESS_ONLY, Keyboard.KEY_T);
        modTex = new GameAction("modTex", GameAction.DETECT_INITIAL_PRESS_ONLY, Keyboard.KEY_M);
    }
}
