package org.jomaveger.timer;

import org.apache.log4j.Logger;
import org.lwjgl.Sys;

/**
 * @author jmvegas.gertrudix
 */
public final class Timer {
    
    private static final Logger LOGGER = Logger.getLogger(Timer.class);

    private long startTime;
    private long currentTime;

    /**
     * Initializes the timer.
     */
    public void Init() {
        startTime = Sys.getTime();
	currentTime = startTime;
    }

    /**
     * Returns the time that have passed since the last loop.
     *
     * @return Delta time in seconds
     */
    public float GetElapsedSeconds() {
        float elapsedTime = (float)(Sys.getTime() - currentTime);
	elapsedTime /= 1000;
        currentTime = Sys.getTime();
        return elapsedTime;
    }
}