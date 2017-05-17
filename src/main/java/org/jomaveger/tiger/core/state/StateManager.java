package org.jomaveger.tiger.core.state;

/**
 * @author jmvegas.gertrudix
 */
public enum StateManager {
    
    INSTANCE;

    private GameState activeState;
    
    private StateManager() {
        this.activeState = null;
    }
    
    public GameState GetActiveState() {
        return this.activeState;
    }
    
    public void ChangeState(GameState newState) {
        if (this.activeState != null) {
            this.activeState.LeaveState();
        }
        this.activeState = newState;
        this.activeState.EnterState();
    }
    
    public void Update(Double elapsedTimeInSeconds) {
        if (this.activeState != null) {
            this.activeState.Update(elapsedTimeInSeconds);
        }
    }

    public void Render() {
        if (this.activeState != null) {
            this.activeState.Render();
        }
    }
}
