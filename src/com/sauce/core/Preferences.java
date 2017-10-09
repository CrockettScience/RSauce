package com.sauce.core;

import com.sauce.util.misc.Ini;
import com.util.RSauceLogger;
import com.util.structures.special.SortedArrayList;
import org.lwjgl.glfw.GLFWVidMode;

import javax.swing.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created by John Crockett.
 * Global Preferences Object
 */
public class Preferences {

    // Project CONSTANTS
    public static final String ENGINE_VERSION = "0.3.6 Dev 4";
    public static final String NAME = "RSauce " + ENGINE_VERSION;
    public static final String PROJECT_VERSION = "0.0.0";

    // Dev Settings
    public static final boolean DEBUG = false;
    private static final Path DEV_INI_PATH = Paths.get(System.getProperty("user.home"), "RSauce Dev");
    private static final Path PROJ_INI_PATH = Paths.get(System.getProperty("user.home"), NAME + " " + PROJECT_VERSION);

    static {
        Ini dev = new Ini(DEV_INI_PATH, "dev");
        if(dev.isEmpty()) {
            JFileChooser assetChooser = new JFileChooser();
            assetChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            assetChooser.setDialogTitle("RSauce Dev Initialization: Set Repository Directory");
            assetChooser.showOpenDialog(null);

            dev.write("PATHS", "Repository", assetChooser.getSelectedFile().getPath() + "/");

            dev.save();
        }

        ASSET_ROOT = dev.read("PATHS", "Repository", Paths.get(System.getProperty("user.home"), "RSauce").toString()) + "\\assets\\";

        Ini proj = new Ini(PROJ_INI_PATH, NAME);
        {
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            if (proj.isEmpty() ||
                    !(proj.readInt("GRAPHICS", "WindowWidth", -1) == vidmode.width() &&
                            proj.readInt("GRAPHICS", "WindowHeight", -1) == vidmode.height()) &&
                            proj.readInt("GRAPHICS", "RefreshRate", -1) == vidmode.refreshRate()) {

                proj.write("GRAPHICS", "WindowWidth", String.valueOf(vidmode.width()));
                proj.write("GRAPHICS", "WindowHeight", String.valueOf(vidmode.height()));

                proj.write("GRAPHICS", "FullScreen", "false");

                proj.write("GRAPHICS", "FullScreenWidth", String.valueOf(vidmode.width()));
                proj.write("GRAPHICS", "FullScreenHeight", String.valueOf(vidmode.height()));
                proj.write("GRAPHICS", "RefreshRate", String.valueOf(vidmode.refreshRate()));

                proj.write("GRAPHICS", "FrameLimit", String.valueOf(vidmode.refreshRate()));

                proj.write("AUDIO", "BufferSize", "4096");

                proj.save();
            }
        }

        screenWidth = proj.readInt("GRAPHICS", "WindowWidth", -1);
        screenHeight = proj.readInt("GRAPHICS", "WindowHeight", -1);

        fullscreenWidth = proj.readInt("GRAPHICS", "FullScreenWidth", -1);
        fullscreenHeight = proj.readInt("GRAPHICS", "FullScreenHeight", -1);
        fullscreenRefreshRate = proj.readInt("GRAPHICS", "RefreshRate", -1);

        fullscreen = proj.readBool("GRAPHICS", "FullScreen", false);

        AUDIO_BUFFER_SIZE = proj.readInt("AUDIO", "BufferSize", 4096);

        SupportedVideoModes.addModes(glfwGetVideoModes(glfwGetPrimaryMonitor()));

    }

    // FINAL SETTINGS: Require a restart to change.
    public static final String ASSET_ROOT;

    // Audio
    public static final int AUDIO_BUFFER_SIZE;

    // NON-FINAL SETTINGS: Can change at runtime.
    // Graphics
    private static boolean fullscreen;
    private static int fullscreenWidth;
    private static int fullscreenHeight;
    private static int fullscreenRefreshRate;
    private static int screenWidth;
    private static int screenHeight;

    // Other
    private static int frameLimit = 60;

    public static void setWindowedSize(int width, int height){
        screenWidth = width;
        screenHeight = height;

        Ini proj = new Ini(PROJ_INI_PATH, NAME);

        proj.write("GRAPHICS", "WindowWidth", String.valueOf(width));
        proj.write("GRAPHICS", "WindowHeight", String.valueOf(height));

        proj.save();

        updateWindow();
    }

    public static void setFullscreenMode(GLFWVidMode vidmode){
        fullscreenWidth = vidmode.width();
        fullscreenHeight = vidmode.height();
        fullscreenRefreshRate = vidmode.refreshRate();

        Ini proj = new Ini(PROJ_INI_PATH, NAME);

        proj.write("GRAPHICS", "FullScreenWidth", String.valueOf(vidmode.width()));
        proj.write("GRAPHICS", "FullScreenHeight", String.valueOf(vidmode.height()));
        proj.write("GRAPHICS", "RefreshRate", String.valueOf(vidmode.refreshRate()));

        proj.save();

        updateWindow();
    }

    public static void setFullscreen(boolean bool){
        fullscreen = bool;

        Ini proj = new Ini(PROJ_INI_PATH, NAME);

        proj.write("GRAPHICS", "FullScreen", String.valueOf(bool));

        proj.save();

        updateWindow();
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

    private static void updateWindow(){
        glfwSetWindowMonitor(
                Main.getWindowHandle(),
                fullscreen ? glfwGetPrimaryMonitor() : NULL,
                0,0,
                fullscreen ? fullscreenWidth : screenWidth,
                fullscreen ? fullscreenHeight : screenHeight,
                fullscreenRefreshRate);
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
