package com.sauce.core;

import com.Project;
import com.demo.scenes.DemoScene;
import com.sauce.asset.audio.AudioThread;
import com.sauce.core.engine.Engine;
import com.sauce.core.scene.SceneManager;
import com.sauce.core.scene.Camera;
import com.sauce.util.ogl.OGLCoordinateSystem;
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
import static com.Project.*;

/**
 * Created by John Crockett.
 * Setup, initialization, and loop object. Serves as application entry point.
 */

public class Main{

    // The window handle
    public static long window;

    private static boolean running = true;

    public static void main(String[] args) {
        System.out.println("LWJGL " + Version.getVersion() + "!");
        System.out.println("RSauce " + ENGINE_VERSION + "!");

        initGLFW();
        initOpenGL();
        initOpenAL();

        loop(initEngine());

        AudioThread.killAudioThread();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwSetErrorCallback(null).free();
        glfwTerminate();

    }

    private static void initGLFW() {
        GLFWErrorCallback.createPrint(System.out).set();

        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);



        window = glfwCreateWindow(Project.SCREEN_WIDTH, Project.SCREEN_HEIGHT, Project.NAME, NULL, NULL);

        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            glfwGetWindowSize(window, pWidth, pHeight);

            glfwSetWindowPos(
                    window,
                    (Project.SCREEN_WIDTH - pWidth.get(0)) / 2,
                    (Project.SCREEN_HEIGHT - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);
    }

    private static void initOpenGL(){
        GL.createCapabilities();

        glClearColor(0.0f, 0.0f, 0.5f, 0.0f);

        glViewport(0, 0, Project.SCREEN_WIDTH, Project.SCREEN_HEIGHT);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        OGLCoordinateSystem.setCoordinateState(0, 0, Project.SCREEN_WIDTH, Project.SCREEN_HEIGHT);
    }

    private static void initOpenAL(){
        Thread audio = AudioThread.getAudioThread();
        audio.start();
    }

    private static Engine initEngine(){
        SceneManager.setCamera(new Camera(0, 0, Project.SCREEN_WIDTH, Project.SCREEN_HEIGHT, 0, 0));
        SceneManager.setScene(new DemoScene());

        return Engine.getEngine();
    }

    private static void loop(Engine engine) {
        double current = (double) System.currentTimeMillis() / 1000.0;
        double last = 0;

        while ( !glfwWindowShouldClose(window) && running) {
            glClear(GL_COLOR_BUFFER_BIT); // clear the framebuffer

            last = current;
            current = (double) System.currentTimeMillis() / 1000.0;
            engine.update(current - last);

            glfwPollEvents();
        }
    }

    public static void quitAtEndOfCycle(){
        running = false;
    }

}
