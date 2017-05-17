package org.jomaveger.tiger.core.graphics;

import org.apache.log4j.Logger;
import org.jomaveger.tiger.core.input.InputManager;
import org.jomaveger.tiger.core.state.GameState;
import org.jomaveger.tiger.core.state.StateManager;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_GREATER;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glAlphaFunc;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glShadeModel;
import static org.lwjgl.opengl.GL11.glVertex3i;
import static org.lwjgl.opengl.GL11.glViewport;
import org.lwjgl.opengl.GLCapabilities;
import static org.lwjgl.system.MemoryUtil.NULL;


/**
 * @author jmvegas.gertrudix
 */
public enum RenderWindow {
    
    INSTANCE();
    
    private final static Logger LOGGER = Logger.getLogger(RenderWindow.class);
    
    private Boolean isFullScreen;
    private Boolean initialized;
    private String title;
    private Integer width;
    private Integer height;
    
    private Long windowHandle;
    private Boolean glfwIsInitialized;
    
    private GameState initialGameState;
    
    private GLCapabilities capabilities;
    private Boolean resized;
    
    private RenderWindow() {
        this.isFullScreen = Boolean.FALSE;
        this.initialized = Boolean.FALSE;
        this.title = null;
        this.width = 0;
        this.height = 0;
        this.initialGameState = null;
        this.windowHandle = null;
        this.glfwIsInitialized = Boolean.FALSE;
        this.resized = Boolean.FALSE;
    }
    
    public Boolean Init(Boolean isFullScreen, Integer width, Integer height, String title, GameState gameState) {
        this.initialized = this.Open(isFullScreen, width, height, title, gameState);
        
        if (!this.initialized) {
            this.Release();
        }

        return this.initialized;
    }
    
    private Boolean Open(Boolean isFullScreen, Integer width, Integer height, String title, GameState gameState) {        
        this.isFullScreen = isFullScreen;
        this.title = title;
        this.width = width;
        this.height = height;
        this.initialGameState = gameState;
        
        if (glfwInit() != Boolean.TRUE) {
            LOGGER.info("GLFW initialization failed!");
            return Boolean.FALSE;
        }
        
        this.glfwIsInitialized = Boolean.TRUE;

        glfwDefaultWindowHints();
        if (isFullScreen) {
            glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        } else {
            glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        }
        
        if (isFullScreen) {
            this.windowHandle = glfwCreateWindow(width, height, title, glfwGetPrimaryMonitor(), NULL);
        } else {
            this.windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
        }
        
        glfwSetFramebufferSizeCallback(this.windowHandle, (window, w, h) -> {
            this.OnResize(w, h, Boolean.TRUE);
        });
        
        if (this.windowHandle == NULL) {
            LOGGER.info("Failed to create the window!");
            glfwTerminate();
            return Boolean.FALSE;
        }
        
        glfwSetKeyCallback(this.windowHandle, InputManager.INSTANCE.keyboard);
        glfwSetMouseButtonCallback(this.windowHandle, InputManager.INSTANCE.mouse);
        
        GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(this.windowHandle, (videoMode.width() - width) / 2, (videoMode.height() - height) / 2);
        
        glfwMakeContextCurrent(this.windowHandle);
        glfwSwapInterval(1);
        glfwShowWindow(this.windowHandle);
        
        capabilities = GL.createCapabilities();
        
        this.InitOpenGL();
        
        this.OnResize(width, height, Boolean.FALSE);
        
        StateManager.INSTANCE.ChangeState(this.initialGameState);

	return Boolean.TRUE;
    }
    

    public void Release() {
        if (this.windowHandle != NULL) {
            glfwDestroyWindow(this.windowHandle);
        }
        if (this.glfwIsInitialized) {
            glfwTerminate();
        }
        this.initialized = Boolean.FALSE;
        this.isFullScreen = Boolean.FALSE;
        this.title = null;
        this.width = 0;
        this.height = 0;
        this.initialGameState = null;
        this.windowHandle = null;
        this.glfwIsInitialized = Boolean.FALSE;
        this.resized = Boolean.FALSE;
    }

    public void Update(Double elapsedTimeInSeconds) {
        InputManager.INSTANCE.Update();
        StateManager.INSTANCE.Update(elapsedTimeInSeconds);
    }

    public void Render() {
        StateManager.INSTANCE.Render();
        glfwSwapBuffers(this.windowHandle);
    }
    
    public Long GetWindowHandle() {
        return this.windowHandle;
    }
    
    public Boolean IsResized() {
        return this.resized;
    }

    public void SetResized(Boolean resized) {
        this.resized = resized;
    }

    public String GetTitle() {
        return this.title;
    }

    public Integer GetWidth() {
        return this.width;
    }

    public Integer GetHeight() {
        return this.height;
    }
    
    public Boolean WindowShouldClose() {
        return glfwWindowShouldClose(this.windowHandle);
    }
    
    private void InitOpenGL() {        
        glEnable(GL_TEXTURE_2D);
        glShadeModel(GL_SMOOTH);
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, 0.0f);
    }

    private void OnResize(Integer w, Integer h, Boolean isResized) {
        this.width = w;
        this.height = h;
        this.SetResized(isResized);
        
        glViewport(0, 0, this.width, this.height);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0, this.width, this.height, 0.0, -1.0, 1.0);
        glMatrixMode(GL_MODELVIEW);
    }
}
