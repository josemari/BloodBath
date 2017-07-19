package org.jomaveger.core;

import org.apache.log4j.Logger;
import org.jomaveger.texture.Texture;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author jmvegas.gertrudix
 */
public final class ScreenManager {
    
    private final static Logger LOGGER = Logger.getLogger(ScreenManager.class);

    private DisplayMode mode;

    public ScreenManager(int width, int height, int bpp) {
        setDisplayMode(width, height, bpp);
    }

    public ScreenManager() {
        setDisplayMode(getDesktopDisplayMode().getWidth(), getDesktopDisplayMode().getHeight(), getDesktopDisplayMode().getBitsPerPixel());
    }

    public DisplayMode[] getAvailableDisplayModes() throws LWJGLException {
        return Display.getAvailableDisplayModes();
    }

    public void setTitle(String title) {
        Display.setTitle(title);
    }

    public void setDisplayMode(int width, int height, int bpp) {
        try {
            mode = findDisplayMode(width, height, bpp);

            if (mode == null) {
                LOGGER.info("Error - " + width + "x" + height + "x" + bpp + " display mode unavailable");
                return;
            }

            Display.setDisplayMode(mode);

        } catch (LWJGLException ex) {
            LOGGER.info("Failed to find and set a display mode to the window", ex);
        }
    }

    public void setFullScreen(boolean t) {
        try {
            Display.setFullscreen(t);
        } catch (LWJGLException ex) {
            LOGGER.info("Failed to set fullscreen mode to value " + t, ex);
        }
    }

    public void restoreScreen() {
        try {
            Display.setFullscreen(false);
        } catch (LWJGLException ex) {
            LOGGER.info("Failed to go back from fullscreen mode", ex);
        }
    }

    public void create() {
        try {
            Display.create();
        } catch (LWJGLException ex) {
            LOGGER.info("Failed to create a window", ex);        }
    }

    public DisplayMode getCurrentDisplayMode() {
        return Display.getDisplayMode();
    }

    public DisplayMode getDesktopDisplayMode() {
        return Display.getDesktopDisplayMode();
    }

    public void update() {
        Display.update();
    }

    public boolean isCloseRequested() {
        if (Display.isCloseRequested()) {
            return true;
        }
        return false;
    }

    private DisplayMode findDisplayMode(int width, int height, int bpp) throws LWJGLException {
        DisplayMode[] modes = Display.getAvailableDisplayModes();
        DisplayMode mode1 = null;

        for (int i = 0; i < modes.length; i++) {
            if ((modes[i].getBitsPerPixel() == bpp) || (mode1 == null)) {
                if ((modes[i].getWidth() == width) && (modes[i].getHeight() == height)) {
                    mode1 = modes[i];
                }
            }
        }

        return mode1;
    }

    public boolean isFullscreen() {
        return Display.isFullscreen();
    }

    public boolean isDirty() {
        return Display.isDirty();
    }

    public void setVSyncEnabled(boolean t) {
        Display.setVSyncEnabled(t);
    }
    
    public int getWidth() {
        return mode.getWidth();
    }

    public int getHeight() {
        return mode.getHeight();
    }

    public void enterOrtho() {
        glPushAttrib(GL_ALL_ATTRIB_BITS);

        glPushMatrix(); 
        glLoadIdentity();
        glMatrixMode(GL_PROJECTION); 
        glPushMatrix();

        glLoadIdentity();
        glOrtho(0, getWidth(), getHeight(), 0, -1, 1); 
        glDisable(GL_DEPTH_TEST); 
        glDisable(GL_LIGHTING); 
    }

    public void leaveOrtho() {
        glPopMatrix(); 
        glMatrixMode(GL_MODELVIEW);
        glPopMatrix(); 
        glPopAttrib();
    }
	
    public void drawBackground(Texture background, int x, int y) {
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();						

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);   

        enterOrtho(); 
        glPushMatrix();

        background.bind();

        glBegin(GL_QUADS); 
        glTexCoord2f(0, 0);
        glVertex2i(0, 0); 
        glTexCoord2f(0, background.getHeight());
        glVertex2i(0, getHeight()); 
        glTexCoord2f(background.getWidth(), background.getHeight());
        glVertex2i(getWidth(), getHeight());
        glTexCoord2f(background.getWidth(), 0);
        glVertex2i(getWidth(), 0); 
        glEnd(); 

        glPopMatrix();

        leaveOrtho();
    }
}
