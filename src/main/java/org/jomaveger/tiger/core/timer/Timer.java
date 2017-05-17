package org.jomaveger.tiger.core.timer;

import org.apache.log4j.Logger;
import static org.lwjgl.glfw.GLFW.glfwGetTime;

/**
 * @author jmvegas.gertrudix
 */
public final class Timer {

    private final static Logger LOGGER = Logger.getLogger(Timer.class);
    
    private static final Double LIMIT_FPS = 60.0;
    private static final Double INTERVAL = 1.0 / LIMIT_FPS;
    
    private Double lastTime;
    private Double timer;
    private Double deltaTime;
    private Double nowTime;
    private Integer frames;
    private Integer updates;
    
    public Timer() {
        this.lastTime = glfwGetTime();
        this.timer = this.lastTime;
        this.deltaTime = 0.0;
        this.nowTime = 0.0;
        this.frames = 0;
        this.updates = 0;
    }

    public void UpdateTime() {
        nowTime = glfwGetTime();
        deltaTime += (nowTime - lastTime) / INTERVAL;
        lastTime = nowTime;
    }

    public Double GetDeltaTime() {
        return deltaTime;
    }
    
    public void Refresh() {
        this.updates++;
        this.deltaTime--;
    }
    
    public void IncrementNumberOfFrames() {
        this.frames++;
    }

    public void ResetAfterOneSecond() {
        if (glfwGetTime() - timer > 1.0) {
            this.timer++;
            LOGGER.info("FPS: " + frames + " Updates: " + updates + "\n");
            this.updates = 0;
            this.frames = 0;
        }
    }
    
    public Double GetInterval() {
        return Timer.INTERVAL;
    }
}
