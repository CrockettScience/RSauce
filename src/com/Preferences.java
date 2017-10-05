package com;

import com.util.RSauceLogger;
import com.util.structures.special.SortedArrayList;
import org.lwjgl.glfw.GLFWVidMode;

import javax.swing.*;

import java.util.Comparator;
import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetVideoModes;

/**
 * Created by John Crockett.
 * Global Preferences Object
 */
public class Preferences {

    static {
        JFileChooser assetChooser = new JFileChooser();
        assetChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        assetChooser.setDialogTitle("RSauce Initialization: Set Source Directory");
        assetChooser.showOpenDialog(null);

        ASSET_ROOT = assetChooser.getSelectedFile().getPath() + "/";

        SupportedVideoModes.addModes(glfwGetVideoModes(glfwGetPrimaryMonitor()));

        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        screenWidth = vidmode.width();
        screenHeight = vidmode.height();

        fullscreenWidth = vidmode.width();
        fullscreenHeight = vidmode.height();
        fullscreenRefreshRate = vidmode.refreshRate();
    }

    // FINAL SETTINGS: Require a restart to change.
    // Project Information
    public static final String ENGINE_VERSION = "0.3.5 Dev 3";
    public static final String NAME = "RSauce" + ENGINE_VERSION;
    public static final String PROJECT_VERSION = "0.0.0";
    public static final String ASSET_ROOT;

    // Audio
    public static final int AUDIO_BUFFER_SIZE = 4096;

    // NON-FINAL SETTINGS: Can change at runtime.
    // Graphics
    private static boolean interpolation = false;
    private static boolean fullscreen = false;
    private static int fullscreenWidth;
    private static int fullscreenHeight;
    private static int fullscreenRefreshRate;
    private static int screenWidth;
    private static int screenHeight;

    // Other
    private static int frameLimit = 60;

    public static boolean interpolation() {
        return interpolation;
    }

    public static boolean isFullscreen() {
        return fullscreen;
    }

    public static int getFullscreenWidth() {
        return fullscreenWidth;
    }

    public static int getFullscreenHeight() {
        return fullscreenHeight;
    }

    public static int getFullscreenRefreshRate() {
        return fullscreenRefreshRate;
    }

    public static int getScreenWidth() {
        return screenWidth;
    }

    public static int getScreenHeight() {
        return screenHeight;
    }

    public static int getFrameLimit() {
        return frameLimit;
    }

    public static class SupportedVideoModes{

        private static SortedArrayList<FullScreenMode> modes = new SortedArrayList<FullScreenMode>((o1, o2) -> o1.width - o2.width == 0 ? o1.height - o2.height == 0 ? o1.rate - o2.rate : o1.height - o2.height : o1.width - o2.width){
            private boolean locked = false;

            @Override
            public boolean add(FullScreenMode element) {
                if(!locked)
                    return super.add(element);

                RSauceLogger.println("You cannot modify the list of supported video modes.");
                return false;
            }

            @Override
            public boolean remove(Object x) {
                if(!locked)
                    return super.remove(x);

                RSauceLogger.println("You cannot modify the list of supported video modes.");
                return false;
            }

            @Override
            public FullScreenMode remove(int idx) {
                if(!locked)
                    return super.remove(idx);

                RSauceLogger.println("You cannot modify the list of supported video modes.");
                return null;
            }

            @Override
            public void clear() {
                if(!locked)
                    super.clear();
                else
                    RSauceLogger.println("You cannot modify the list of supported video modes.");
            }

            @Override
            public void clear(int size) {
                if(!locked)
                    super.clear(size);
                else
                    RSauceLogger.println("You cannot modify the list of supported video modes.");
            }

            @Override
            public void forEach(Consumer<? super FullScreenMode> action) {
                RSauceLogger.println("You cannot modify the list of supported video modes.");
            }
        };

        private static void addModes(GLFWVidMode.Buffer modeBuffer){
            for(int i = 1; modeBuffer.hasRemaining(); i++){
                modes.add(new FullScreenMode(modeBuffer.width(), modeBuffer.height(), modeBuffer.refreshRate()));
                modeBuffer.position(i);
            }
        }

        public static SortedArrayList<FullScreenMode> getModes() {
            return modes;
        }

        public static class FullScreenMode{
            private int width;
            private int height;
            private int rate;

            private FullScreenMode(int w, int h, int r){
                width = w;
                height = h;
                rate = r;
            }

            public int getWidth() {
                return width;
            }

            public int getHeight() {
                return height;
            }

            public int getRate() {
                return rate;
            }

            @Override
            public String toString() {
                return "Width: " + width + "; Height: " + height + "; Refresh Rate: " + rate + ".";
            }
        }
    }

}
