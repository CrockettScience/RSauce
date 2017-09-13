package com.sauce.core;

import com.demo.scenes.DemoScene;
import com.sauce.core.engine.Engine;
import com.sauce.core.scene.SceneManager;
import com.sauce.core.scene.View;
import com.sauce.input.InputServer;
import com.util.Vector2D;
import com.util.Vector2DDouble;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import static com.sauce.core.Project.*;

/**
 * Created by John Crockett.
 */

public class Main{

    // The window handle
    public static long window;
    private static boolean running = true;

    // The game loop handle
    public static Main LOOP = new Main();

    // Temporary settings values
    private static int WIDTH = 4096;
    private static int HEIGHT = 2160;

    private void run() {
        System.out.println("LWJGL " + Version.getVersion() + "!");
        System.out.println("RSauce " + ENGINE_VERSION + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.out).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be resizable

        // Get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, Project.NAME, NULL, NULL);

        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup callbacks. Forward raw input data to InputServer to be
        // processed and sent to InputClients.
        setInputCallbacks();

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.5f, 0.0f);

        // Setup the Viewport
        glViewport(0, 0, WIDTH, HEIGHT);

        // Set up blending function
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Setup our Matrix
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0, WIDTH, 0.0, HEIGHT, -1.0, 1.0);
        glMatrixMode(GL_MODELVIEW);


        // Set the view, scene, and engine
        SceneManager.setView(new View(0, 0, WIDTH, HEIGHT, 0, 0));
        SceneManager.setScene(new DemoScene());

        Engine engine = Engine.getEngine(FRAME_LIMIT);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        long current;
        long last = System.nanoTime();

        while ( !glfwWindowShouldClose(window) && running) {
            current = System.nanoTime();
            int delta = (int) ((current - last) / 1000000);
            glClear(GL_COLOR_BUFFER_BIT); // clear the framebuffer

            engine.update(delta);

            glfwPollEvents();

            last = current;
        }
    }

    public static void quitAtEndOfCycle(){
        running = false;
    }

    public static void main(String[] args) {
        LOOP.run();
    }

    private void setInputCallbacks(){
        glfwSetKeyCallback(window, (window, key, scanCode, action, mods) -> {
            InputServer.recieveRawInputEvent(new RawInputEvent(RawInputEvent.EVENT_TYPE_KEY, key, action, mods, null));
        });

        glfwSetCharCallback(window, (window, character) -> {
            InputServer.recieveRawInputEvent(new RawInputEvent(RawInputEvent.EVENT_TYPE_TEXT, character,-1,-1, null));
        });

        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            InputServer.recieveRawInputEvent(new RawInputEvent(RawInputEvent.EVENT_TYPE_MOUSE_BUTTON, button, action, mods, null));
        });

        glfwSetScrollCallback(window, (window, x, y) -> {
            InputServer.recieveRawInputEvent(new RawInputEvent(RawInputEvent.EVENT_TYPE_MOUSE_SCROLL, -1, -1, -1, new Vector2DDouble(x, y)));
        });

        glfwSetCursorPosCallback(window, (window, x, y) -> {
            InputServer.recieveRawInputEvent(new RawInputEvent(RawInputEvent.EVENT_TYPE_MOUSE_MOVE, -1, -1, -1, new Vector2DDouble(x, y)));
        });

        glfwSetJoystickCallback((joyId, event) -> {
            InputServer.recieveRawInputEvent(new RawInputEvent(RawInputEvent.EVENT_TYPE_JOYSTICK_CONNECT, joyId, event, -1, null));

        });
    }

    public static class RawInputEvent{

        public static final int EVENT_TYPE_KEY = 0;
        public static final int EVENT_TYPE_TEXT = 1;
        public static final int EVENT_TYPE_MOUSE_BUTTON = 2;
        public static final int EVENT_TYPE_MOUSE_SCROLL = 3;
        public static final int EVENT_TYPE_MOUSE_MOVE = 4;
        public static final int EVENT_TYPE_JOYSTICK_CONNECT = 5;

        int key;
        int action;
        int mods;
        int type;
        Vector2DDouble coords;

        private RawInputEvent(int type, int key, int action, int mods, Vector2DDouble coordinates){
            this.type = type;
            this.key = key;
            this.action = action;
            this.mods = mods;
            coords = coordinates;
        }

        public int key(){
            return key;
        }

        public int action(){
            return action;
        }

        public int mods(){
            return mods;
        }

        public int type(){
            return type;
        }

        public Vector2DDouble coordinates(){
            return coords;
        }
    }

}
