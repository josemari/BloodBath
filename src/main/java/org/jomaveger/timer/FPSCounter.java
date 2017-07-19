package org.jomaveger.timer;

/**
 * @author jmvegas.gertrudix
 */
public final class FPSCounter {

    public static float frameInterval = 0;

    private static int fps = 0;

    private static int old_fps = 0;

    private static float timeAccum = 0;

    private FPSCounter() {
    }

    public final static void update(float elapsedTime) {

        timeAccum += elapsedTime;

        fps++;

        if (timeAccum > 1) {
            timeAccum = 0;
            old_fps = fps;
            fps = 0;
        }
    }

    public final static int get() {
        return old_fps;
    }
}
