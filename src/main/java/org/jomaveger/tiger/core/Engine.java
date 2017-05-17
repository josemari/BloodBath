package org.jomaveger.tiger.core;

import org.apache.log4j.Logger;
import org.jomaveger.tiger.core.graphics.GraphicsServer;
import org.jomaveger.tiger.core.graphics.RenderWindow;
import org.jomaveger.tiger.core.input.InputManager;
import org.jomaveger.tiger.core.state.GameState;
import org.jomaveger.tiger.core.timer.Timer;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

/**
 * @author jmvegas.gertrudix
 */
public enum Engine {
    
    INSTANCE();
    
    private final static Logger LOGGER = Logger.getLogger(Engine.class);
    
    private Boolean initialized;
    private Boolean exit;
    private Timer clock;
    
    private Engine() {
        this.initialized = Boolean.FALSE;
        this.exit = Boolean.FALSE;
        this.clock = null;
    }
    
    public void ExitRequest() {
        this.exit = Boolean.TRUE;
    }
    
    public Boolean ExitRequested() {
        return this.exit;
    }
    
    public Boolean Init(Boolean isFullScreen, Integer width, Integer height, String title, GameState gameState) {
        this.initialized = this.Open(isFullScreen, width, height, title, gameState);
        
        if (!this.initialized) {
            this.Release();
        }
        
        return this.initialized;
    }
    
    private Boolean Open(Boolean isFullScreen, Integer width, Integer height, String title, GameState gameState) {
        return GraphicsServer.INSTANCE.Init(isFullScreen, width, height, title, gameState);
    }

    private void Release() {
        this.clock = null;

        GraphicsServer.INSTANCE.Release();        
	
        this.initialized = Boolean.FALSE;
    }

    public void Run() {
        this.clock = new Timer();
        InputManager.INSTANCE.Init(RenderWindow.INSTANCE.GetWindowHandle());

         // - While window is alive
        while (!ExitRequested() && !RenderWindow.INSTANCE.WindowShouldClose()) {
            
            // - Measure time
            this.clock.UpdateTime();
            
            // - Only update at 60 frames / s
            while (this.clock.GetDeltaTime() >= 1.0) {
                this.Update(this.clock.GetInterval());
                this.clock.Refresh();
            }
            
            // - Render at maximum possible frames
            this.Render();
            this.clock.IncrementNumberOfFrames();
            
            // - Reset after one second
            this.clock.ResetAfterOneSecond();
        }
        
        this.Stop();
    }

    public void Update(Double elapsedTimeInSeconds) {
	GraphicsServer.INSTANCE.Update(elapsedTimeInSeconds);
    }

    public void Render() {
        GraphicsServer.INSTANCE.Render();
    }

    public void Stop() {
        this.Release();
    }
}