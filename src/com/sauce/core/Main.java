package com.sauce.core;

import com.sauce.asset.graphics.DrawBatch;
import com.sauce.asset.graphics.Image;
import com.sauce.asset.graphics.Sprite;
import com.sauce.input.InputEvent;
import com.sauce.input.InputServer;
import com.sauce.input.InputClient;
import com.structures.nonsaveable.ArrayGrid;
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

/**
 * Created by John Crockett.
 */

public class Main implements InputClient {

    // The window handle
    private long window;
    private boolean running = true;

    // The game loop handle
    public static Main LOOP = new Main();

    // Eggy handles
    int eggyX = WIDTH / 2;
    int eggyY = HEIGHT / 2;
    Sprite eggy;

    // Temporary settings values
    public static int WIDTH = 800;
    public static int HEIGHT = 600;

    private void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        System.out.println("Hello RSauce " + Project.ENGINE_VERSION + "!");

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

        // Setup a key callback. Forward raw input data to InputServer to be
        // processed and sent to InputClients.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            InputServer.recieveRawInputEvent(new RawInputEvent(key, scancode, action, mods));

        });

        InputServer.bind(this);

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

        // Mr. Eggy

        // Setup the "ID Matrix" to identify separate frame loops.
        ArrayGrid<String> eggyMatrix = new ArrayGrid<>(4, 4);
        createEggyMatrix(eggyMatrix);

        eggy = new Sprite(Project.ASSET_ROOT + "eggy.png",
                4,
                4,
                eggyMatrix,
                true);
        DrawBatch batch = new DrawBatch();

        eggy.setXScale(4f);
        eggy.setYScale(4f);


        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) && running) {
            glClear(GL_COLOR_BUFFER_BIT); // clear the framebuffer

            eggy.update();
            batch.add(eggy, eggyX, eggyY);
            batch.renderBatch();

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    private void createEggyMatrix(ArrayGrid<String> eggy) {
        String idle = "idle";
        String down = "down";
        String up = "up";
        String left = "left";
        String right = "right";

        eggy.set(0, 0, idle);
        eggy.set(1, 0, down);
        eggy.set(2, 0, down);
        eggy.set(3, 0, left);

        eggy.set(0, 1, left);
        eggy.set(1, 1, right);
        eggy.set(2, 1, right);
        eggy.set(3, 1, up);

        eggy.set(0, 2, up);
    }

    public void quitAtEndOfCycle(){
        running = false;
    }

    public static void main(String[] args) {
        LOOP.run();
    }

    @Override
    public void receivedInputEvent(InputEvent event) {
        switch (event.key()){
            case GLFW_KEY_ESCAPE:
                if(event.action() == GLFW_RELEASE)
                    glfwSetWindowShouldClose(window, true);
                break;

            case GLFW_KEY_W:
                if(event.action() == GLFW_REPEAT) {
                    eggyY++;
                    eggy.setAnimationState("up");
                } else
                    eggy.setAnimationState("idle");
                break;

            case GLFW_KEY_S:
                if(event.action() == GLFW_REPEAT) {
                    eggyY--;
                    eggy.setAnimationState("down");
                } else
                    eggy.setAnimationState("idle");
                break;

            case GLFW_KEY_A:
                if(event.action() == GLFW_REPEAT) {
                    eggyX--;
                    eggy.setAnimationState("left");
                } else
                    eggy.setAnimationState("idle");
                break;

            case GLFW_KEY_D:
                if(event.action() == GLFW_REPEAT) {
                    eggyX++;
                    eggy.setAnimationState("right");
                } else
                    eggy.setAnimationState("idle");
                break;
        }
    }

    public static class RawInputEvent{

        int key;
        int scancode;
        int action;
        int mods;

        private RawInputEvent(int key, int scancode, int action, int mods){
            this.key = key;
            this.scancode = scancode;
            this.action = action;
            this.mods = mods;
        }

        public int key(){
            return key;
        }

        public int action(){
            return action;
        }
    }

}
