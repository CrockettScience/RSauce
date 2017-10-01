package com;

import org.lwjgl.glfw.GLFWVidMode;

import javax.swing.*;

import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;

/**
 * Created by John Crockett.
 * Global Project Settings Object
 */
public class Project {

    static {
        JFileChooser assetChooser = new JFileChooser();
        assetChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        assetChooser.setDialogTitle("RSauce Initialization: Set Source Directory");
        assetChooser.showOpenDialog(null);

        ASSET_ROOT = assetChooser.getSelectedFile().getPath() + "/";

        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        SCREEN_WIDTH = vidmode.width();
        SCREEN_HEIGHT = vidmode.height();
    }

    // Project Information
    public static final String ENGINE_VERSION = "0.3.4 Dev 6";
    public static final String NAME = "RSauce" + ENGINE_VERSION;
    public static final String PROJECT_VERSION = "0.0.0";
    public static final String ASSET_ROOT;

    // Graphics
    public static final boolean INTERPOLATION = false;
    public static final int SCREEN_WIDTH;
    public static final int SCREEN_HEIGHT;

    // Audio
    public static final int AUDIO_BUFFER_SIZE = 4096;

    // Other
    public static final int FRAME_LIMIT = 60;

}
