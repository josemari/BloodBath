package org.jomaveger.tiger.core.exception;

/**
 * @author jmvegas.gertrudix
 */
public class EngineException extends RuntimeException {
    
    public EngineException() {
        super();
    }
    
    public EngineException(String message) {
        super(message);
    }
    
    public EngineException(String message, Throwable exception) {
        super(message, exception);
    }
}
