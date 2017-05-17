package org.jomaveger.tiger.test;

import org.apache.log4j.Logger;
import org.jomaveger.tiger.core.Engine;
import org.jomaveger.tiger.core.state.GameState;

/**
 *
 * @author jmvegas.gertrudix
 */
public class Test {
    
    private final static Logger LOGGER = Logger.getLogger(Test.class);
    
    public static void main(String[] args) {
        GameState gameState = new DemoPlayState();
	Engine.INSTANCE.Init(Boolean.FALSE, 800, 600, "Tiger", gameState);
	Engine.INSTANCE.Run();
    }
}
