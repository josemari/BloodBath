package org.jomaveger.tiger.core.graphics;

import org.apache.log4j.Logger;
import org.jomaveger.tiger.core.state.GameState;

/**
 * @author jmvegas.gertrudix
 */
public enum GraphicsServer {
    
    INSTANCE();
    
    private final static Logger LOGGER = Logger.getLogger(GraphicsServer.class);
    
    private Boolean initialized;

    private GraphicsServer() {
        this.initialized = Boolean.FALSE;
    }
    
    public Boolean Init(Boolean isFullScreen, Integer width, Integer height, String title, GameState gameState) {
        this.initialized = this.Open(isFullScreen, width, height, title, gameState);
        
        if (!this.initialized) {
            this.Release();
        }

        return this.initialized;
    }
    
    private Boolean Open(Boolean isFullScreen, Integer width, Integer height, String title, GameState gameState) {
        return RenderWindow.INSTANCE.Init(isFullScreen, width, height, title, gameState);
    }
    
    public void Release() {        
        RenderWindow.INSTANCE.Release();        
        this.initialized = Boolean.FALSE;
    }

    public void Update(Double elapsedTimeInSeconds) {
        RenderWindow.INSTANCE.Update(elapsedTimeInSeconds);
    }

    public void Render() {
        RenderWindow.INSTANCE.Render();
    }
}
