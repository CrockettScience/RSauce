package com.sauce.core;

import com.demo.scenes.Demo;
import com.sauce.asset.audio.AudioThread;
import com.sauce.core.engine.Engine;
import com.sauce.core.engine.SceneManager;
import com.sauce.util.ogl.OGLCoordinateSystem;
import com.util.RSauceLogger;
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
import static com.sauce.core.Preferences.*;

/**
 * Created by John Crockett.
 * Setup, initialization, and loop object. Serves as application entry point.
 */

public class Main{

    // The window handle
    private static long window;

    private static boolean running = true;

    public static void main(String[] args) {
        RSauceLogger.println("LWJGL " + Version.getVersion() + "!");
        RSauceLogger.println("RSauce " + ENGINE_VERSION + "!");

        initGLFW();
        initOpenGL();
        initOpenAL();

        RSauceLogger.printDebugln("ACTIVE");

        try {
            loop(initEngine());
        }
        finally {
            AudioThread.killAudioThread();

            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);
            glfwSetErrorCallback(null).free();
            glfwTerminate();
        }

    }

    private static void initGLFW() {
        GLFWErrorCallback.createPrint(System.out).set();

        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        window = glfwCreateWindow(Preferences.getCurrentScreenWidth(), Preferences.getCurrentScreenHeight(), Preferences.NAME, Preferences.isFullscreen() ? GLFW.glfwGetPrimaryMonitor() : NULL, NULL);

        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        try (MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            glfwGetWindowSize(window, pWidth, pHeight);

            glfwSetWindowPos(
                    window,
                    (Preferences.getWindowScreenWidth() - pWidth.get(0)) / 2,
                    (Preferences.getWindowScreenHeight() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);
    }

    private static void initOpenGL(){
        GL.createCapabilities();

        glClearColor(0.0f, 0.0f, 0.5f, 0.0f);

        glViewport(0, 0, Preferences.getCurrentScreenWidth(), Preferences.getCurrentScreenHeight());
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        OGLCoordinateSystem.setCoordinateState(0, 0, Preferences.getCurrentScreenWidth(), Preferences.getCurrentScreenHeight());
    }

    private static void initOpenAL(){
        Thread audio = AudioThread.getAudioThread();
        audio.start();
    }

    private static Engine initEngine(){
        SceneManager.setScene(new Demo());

        Preferences.setFullscreen(Preferences.isFullscreen());

        Preferences.setWindowedSize(Preferences.getWindowScreenWidth(), Preferences.getWindowScreenHeight());

        Preferences.setFullscreenMode(SupportedVideoModes.getClosestVidMode(Preferences.getFullscreenWidth(), Preferences.getFullscreenHeight(), Preferences.getFullscreenRefreshRate()));

        return Engine.getEngine();
    }

    private static void loop(Engine engine) {
        double current = (double) System.currentTimeMillis() / 1000.0;
        double last;

        while ( !glfwWindowShouldClose(window) && running) {
            glClear(GL_COLOR_BUFFER_BIT); // clear the framebuffer

            last = current;
            current = (double) System.currentTimeMillis() / 1000.0;
            engine.update(current - last);

            glfwPollEvents();
        }
    }

    public static long getWindowHandle(){
        return window;
    }

    public static void quitAtEndOfCycle(){
        running = false;
    }

}
