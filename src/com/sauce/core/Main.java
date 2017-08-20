package com.sauce.core;

import com.sauce.asset.video.DrawBatch;
import com.sauce.asset.video.Image;
import com.sauce.input.InputEvent;
import com.sauce.input.InputServer;
import com.sauce.input.InputClient;
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

    // Teapot coordinate references
    private int teaX = 0;
    private int teaY = 0;
    private Image teapot;

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
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Setup the Viewport
        glViewport(0, 0, WIDTH, HEIGHT);

        // Setup our Matrix
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0, WIDTH, HEIGHT, 0.0, -1.0, 1.0);
        glMatrixMode(GL_MODELVIEW);

        // Test our Teapot
        teapot = new Image(Project.ASSET_ROOT + "tea.jpg");
        DrawBatch batch = new DrawBatch();

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) && running) {
            glClear(GL_COLOR_BUFFER_BIT); // clear the framebuffer

            // Draw our Teapots
            batch.add(teapot, teaX, teaY);

            batch.renderBatch();

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
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
                teaY -= 3;
                break;

            case GLFW_KEY_A:
                teaX -= 3;
                break;

            case GLFW_KEY_S:
                teaY += 3;
                break;

            case GLFW_KEY_D:
                teaX += 3;
                break;

            case GLFW_KEY_UP:
                teapot.setScale(teapot.getScale() - 0.1f);
                break;

            case GLFW_KEY_DOWN:
                teapot.setScale(teapot.getScale() + 0.1f);
                break;

            case GLFW_KEY_LEFT:
                teapot.setAngle(teapot.getAngle() - 1.0f);
                break;

            case GLFW_KEY_RIGHT:
                teapot.setAngle(teapot.getAngle() + 1.0f);
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
