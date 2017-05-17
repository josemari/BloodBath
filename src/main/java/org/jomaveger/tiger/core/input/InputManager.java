package org.jomaveger.tiger.core.input;

import java.util.ArrayList;
import java.util.List;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

/**
 * @author jmvegas.gertrudix
 */
public enum InputManager {
    
    INSTANCE();
    
    private Long windowHandle;
    private final Integer KEYBOARD_SIZE = 512;
    private final Integer MOUSE_SIZE = 16;

    private List<Integer> keyStates = new ArrayList(KEYBOARD_SIZE);
    private List<Boolean> activeKeys = new ArrayList(KEYBOARD_SIZE);

    private List<Integer> mouseButtonStates = new ArrayList(MOUSE_SIZE);
    private List<Boolean> activeMouseButtons = new ArrayList(MOUSE_SIZE);
    private Long lastMouseNS = 0L;
    private final Long mouseDoubleClickPeriodNS = 1000000000 / 5L; //5th of a second for double click.

    private final Integer NO_STATE = -1;

    private InputManager() {
        
    }
    
    public GLFWKeyCallback keyboard = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            activeKeys.add(key, action != GLFW_RELEASE);
            keyStates.add(key, action);
        }
    };

    public GLFWMouseButtonCallback mouse = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            activeMouseButtons.add(button, action != GLFW_RELEASE);
            mouseButtonStates.add(button, action);
        }
    };

    public void Init(Long windowHandle) {
        this.windowHandle = windowHandle;

        ResetKeyboard();
        ResetMouse();
    }

    public void Update() {
        ResetKeyboard();
        ResetMouse();

        glfwPollEvents();
    }

    private void ResetKeyboard() {
        for (Integer i = 0; i < KEYBOARD_SIZE; i++) {
            keyStates.add(i, NO_STATE);
            activeKeys.add(i, Boolean.FALSE);
        }
    }

    private void ResetMouse() {
        for (Integer i = 0; i < MOUSE_SIZE; i++) {
            mouseButtonStates.add(i, NO_STATE);
            activeMouseButtons.add(i, Boolean.FALSE);
        }
        
        Long now = System.nanoTime();

        if (now - lastMouseNS > mouseDoubleClickPeriodNS) {
            lastMouseNS = 0L;
        }
    }

    public Boolean KeyDown(Integer key) {
        return activeKeys.get(key);
    }

    public Boolean KeyPressed(Integer key) {
        return keyStates.get(key) == GLFW_PRESS;
    }

    public Boolean KeyReleased(Integer key) {
        return keyStates.get(key) == GLFW_RELEASE;
    }

    public Boolean MouseButtonDown(Integer button) {
        return activeMouseButtons.get(button);
    }

    public Boolean MouseButtonPressed(Integer button) {
        return mouseButtonStates.get(button) == GLFW_PRESS;
    }

    public Boolean MouseButtonReleased(Integer button) {
        Boolean flag = mouseButtonStates.get(button) == GLFW_RELEASE;

        if (flag) {
            lastMouseNS = System.nanoTime();
        }

        return flag;
    }

    public Boolean MouseButtonDoubleClicked(Integer button) {
        Long last = lastMouseNS;
        Boolean flag = MouseButtonReleased(button);

        Long now = System.nanoTime();

        if (flag && now - last < mouseDoubleClickPeriodNS) {
            lastMouseNS = 0L;
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }
}
