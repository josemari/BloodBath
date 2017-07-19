package org.jomaveger.core;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.jomaveger.timer.Timer;
import org.jomaveger.graphics.Frustum;
import org.jomaveger.input.GameAction;
import org.jomaveger.timer.FPSCounter;

/**
 * @author jmvegas.gertrudix
 */
public abstract class GameCore {

    private final static Logger LOGGER = Logger.getLogger(GameCore.class);
    
    protected ScreenManager screen; 
    protected boolean isRunning;    
    protected float elapsedTime;
     
    public GameAction pause;
    public GameAction exit;
    public GameAction fullScreen;
    public static Frustum gFrustum = new Frustum();
    private boolean paused; 

    public void run() {
        try {
            init();
            gameLoop();
        } catch (IOException ex) {
            LOGGER.info("Exception running the simulation - ", ex);
        }
    }

    protected void createGameActions() {
        pause = new GameAction("Pause", GameAction.DETECT_INITIAL_PRESS_ONLY, Keyboard.KEY_P);
        exit = new GameAction("Exit", GameAction.DETECT_INITIAL_PRESS_ONLY, Keyboard.KEY_ESCAPE);
        fullScreen = new GameAction("FullScreen", GameAction.DETECT_INITIAL_PRESS_ONLY, Keyboard.KEY_F1);
    }

    protected void init() throws IOException {
        screen = new ScreenManager();
        setFullScreen(false); 
        screen.create(); 

        glShadeModel(GL_SMOOTH); // Habilita Smooth Shading		

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 

        glClearDepth(1.0); 			
        glEnable(GL_DEPTH_TEST); 	
        glDepthFunc(GL_LEQUAL); 	

        glMatrixMode(GL_PROJECTION); 
        glLoadIdentity(); 		

        gluPerspective(45.0f, screen.getWidth() / screen.getHeight(), 0.1f, 3000.0f);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity(); 		

        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); 		

        isRunning = true;

        createGameActions();
    }

    protected void stop() {
        isRunning = false;
        Display.destroy();
        System.exit(0);
    }

    protected void setFullScreen(boolean fullscreen) {
        screen.setFullScreen(fullscreen);
    }

    protected boolean isFullScreen() {
        return screen.isFullscreen();
    }

    protected boolean isPaused() {
        return paused;
    }

    protected void setPaused() {
        paused = !paused;
    }
		
    public void gameLoop() {
        Timer timer = new Timer();
        timer.Init();
	
        while (isRunning) {
            elapsedTime = (float) timer.GetElapsedSeconds();

            FPSCounter.update(elapsedTime);

            update(elapsedTime);

            render();

            screen.update();

            if (screen.isCloseRequested()) {
                stop();
            }
        }

    }

    protected void update(float elapsedTime) {
        checkSystemInput();
        checkGameInput();
    }

    protected void checkSystemInput() {
        if (pause.isPressed()) {
            setPaused();
        }
        if (exit.isPressed()) {
            stop();
        }
    }

    protected void checkGameInput() {
        if (fullScreen.isPressed()) {
            setFullScreen(!isFullScreen());
        }
    }

    protected abstract void render();
}
