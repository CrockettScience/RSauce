package sauce.core.engine;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import sauce.asset.audio.AudioManager;
import sauce.core.coreutil.ogl.OGLCoordinateSystem;
import util.RSauceLogger;

import java.lang.System;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static sauce.core.engine.Preferences.*;

/**
 * Created by John Crockett.
 * Setup, initialization, and loop object. Serves as application entry point.
 */

public class Main{

    private static long window;
    private static Thread mainThread;

    private static boolean running = true;

    public static void main(String[] args) {
        RSauceLogger.println("LWJGL " + Version.getVersion() + "!");
        RSauceLogger.println("RSauce " + ENGINE_VERSION + "!");

        mainThread = Thread.currentThread();

        initGLFW();
        initOpenGL();
        initOpenAL();

        RSauceLogger.printDebugln("ACTIVE");

        int[] w = new int[1];
        int[] h = new int[1];
        glfwGetWindowSize(window, w, h);

        RSauceLogger.printDebugln(w[0] + "x" + h[0]);

        try {
            loop();
        }
        finally {
            AudioManager.killAudioThread();

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

        window = glfwCreateWindow(getCurrentScreenWidth(), getCurrentScreenHeight(), NAME, isFullscreen() ? glfwGetPrimaryMonitor() : NULL, NULL);

        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        try (MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            glfwGetWindowSize(window, pWidth, pHeight);

            glfwSetWindowPos(
                    window,
                    (getWindowScreenWidth() - pWidth.get(0)) / 2,
                    (getWindowScreenHeight() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);


    }

    private static void initOpenGL(){
        GL.createCapabilities();

        glClearColor(1, 1, 1, 1);

        glViewport(0, 0, getCurrentScreenWidth(), getCurrentScreenHeight());

        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        OGLCoordinateSystem.setCoordinateState(0, 0, getCurrentScreenWidth(), getCurrentScreenHeight());

    }

    private static void initOpenAL(){
        Thread audio = AudioManager.getAudioThread();
        audio.start();
    }

    private static void loop() {
        double current = glfwGetTime();
        double last;

        while ( !glfwWindowShouldClose(window) && running) {
            last = current;
            current = glfwGetTime();
            Engine.update(current - last);

            glfwPollEvents();
        }
    }

    public static long getWindowHandle(){
        return window;
    }

    public static void quitAtEndOfCycle(){
        running = false;
    }

    public static Thread getMainThread(){
        return mainThread;
    }

}
