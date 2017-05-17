package org.jomaveger.tiger.core.state;

/**
 * @author jmvegas.gertrudix
 */
public abstract class GameState {

    public GameState() {
        
    }
    
    public void ChangeState(GameState newState) {
        StateManager.INSTANCE.ChangeState(newState);
    }
    
    public abstract void EnterState();
    
    public abstract void LeaveState();

    public abstract void Update(Double elapsedTimeInSeconds);

    public abstract void Render();
}
