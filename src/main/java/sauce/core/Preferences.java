package sauce.core;

import sauce.util.misc.Ini;
import util.RSauceLogger;
import util.structures.special.SortedArrayList;
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
    public static final String ENGINE_VERSION = "0.3.6 Dev 19";
    public static final String NAME = "RSauce " + ENGINE_VERSION;
    public static final String PROJECT_VERSION = "0.0.0";

    // Dev Settings
    public static final boolean DEBUG = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
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
            SupportedVideoModes.addModes(glfwGetVideoModes(glfwGetPrimaryMonitor()));

            if (proj.isEmpty()) {

                proj.write("GRAPHICS", "WindowWidth", String.valueOf(vidmode.width()));
                proj.write("GRAPHICS", "WindowHeight", String.valueOf(vidmode.height()));

                proj.write("GRAPHICS", "FullScreen", "false");

                
                proj.write("GRAPHICS", "FullScreenWidth", String.valueOf(vidmode.width()));
                proj.write("GRAPHICS", "FullScreenHeight", String.valueOf(vidmode.height()));
                proj.write("GRAPHICS", "RefreshRate", String.valueOf(vidmode.refreshRate()));

                proj.write("GRAPHICS", "TexturePageSize", "2048");

                proj.write("AUDIO", "BufferSize", "4096");

                proj.save();
            }
        }

        windowScreenWidth = proj.readInt("GRAPHICS", "WindowWidth", -1);
        windowScreenHeight = proj.readInt("GRAPHICS", "WindowHeight", -1);

        fullscreenWidth = proj.readInt("GRAPHICS", "FullScreenWidth", -1);
        fullscreenHeight = proj.readInt("GRAPHICS", "FullScreenHeight", -1);
        fullscreenRefreshRate = proj.readInt("GRAPHICS", "RefreshRate", -1);

        fullscreen = proj.readBool("GRAPHICS", "FullScreen", false);

        AUDIO_BUFFER_SIZE = proj.readInt("AUDIO", "BufferSize", 4096);

        TEXTURE_PAGE_SIZE = proj.readInt("GRAPHICS", "TexturePageSize", 2048);


        for(SupportedVideoModes.FullScreenMode mode : SupportedVideoModes.getModes()){
            RSauceLogger.printDebugln(mode.width + " x " + mode.height + " at " + mode.rate);
        }

    }

    // FINAL SETTINGS: Require a restart to change.
    public static final String ASSET_ROOT;
    public static final int AUDIO_BUFFER_SIZE;
    public static final int TEXTURE_PAGE_SIZE;

    // NON-FINAL SETTINGS: Can change at runtime.
    // Graphics
    private static boolean fullscreen;
    private static int fullscreenWidth;
    private static int fullscreenHeight;
    private static int fullscreenRefreshRate;
    private static int windowScreenWidth;
    private static int windowScreenHeight;

    // Other
    public static void setWindowedSize(int width, int height){
        windowScreenWidth = width;
        windowScreenHeight = height;

        Ini proj = new Ini(PROJ_INI_PATH, NAME);

        proj.write("GRAPHICS", "WindowWidth", String.valueOf(width));
        proj.write("GRAPHICS", "WindowHeight", String.valueOf(height));

        proj.save();

        if(!fullscreen)
            glfwSetWindowSize(Main.getWindowHandle(), width, height);
    }

    public static void setFullscreenMode(SupportedVideoModes.FullScreenMode vidmode){
        fullscreenWidth = vidmode.width;
        fullscreenHeight = vidmode.height;
        fullscreenRefreshRate = vidmode.rate;

        Ini proj = new Ini(PROJ_INI_PATH, NAME);

        proj.write("GRAPHICS", "FullScreenWidth", String.valueOf(vidmode.width));
        proj.write("GRAPHICS", "FullScreenHeight", String.valueOf(vidmode.height));
        proj.write("GRAPHICS", "RefreshRate", String.valueOf(vidmode.rate));

        proj.save();

        if(fullscreen)
            glfwSetWindowSize(Main.getWindowHandle(), vidmode.width, vidmode.height);
    }

    public static void setFullscreen(boolean bool){
        fullscreen = bool;

        Ini proj = new Ini(PROJ_INI_PATH, NAME);

        proj.write("GRAPHICS", "FullScreen", String.valueOf(bool));

        proj.save();

        changeMode();
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

    public static int getWindowScreenWidth() {
        return windowScreenWidth;
    }

    public static int getWindowScreenHeight() {
        return windowScreenHeight;
    }

    public static int getCurrentScreenWidth() {
        return (Preferences.isFullscreen() ? Preferences.getFullscreenWidth() : Preferences.getWindowScreenWidth());
    }

    public static int getCurrentScreenHeight() {
        return (Preferences.isFullscreen() ? Preferences.getFullscreenHeight() : Preferences.getWindowScreenHeight());

    }

    static void changeMode(){
        glfwSetWindowMonitor(
                Main.getWindowHandle(),
                fullscreen ? glfwGetPrimaryMonitor() : NULL,
                0,0,
                Preferences.getCurrentScreenWidth(), Preferences.getCurrentScreenHeight(),
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

        public static FullScreenMode getClosestVidMode(int width, int height, int rate){
                int low = 0;
                int mid;
                int high = modes.size() - 1;
                while (low < high) {
                    mid = (low + high) / 2;
                    if (compare(modes.get(mid), width, height, rate) < 0)
                        low = mid + 1;
                    else if (compare(modes.get(mid), width, height, rate) > 0)
                        high = mid - 1;
                    else
                        return modes.get(mid);
                }
                return modes.get(low);
        }

        private static int compare(FullScreenMode mode, int width, int height, int rate){
            return mode.width - width == 0 ? mode.height - height == 0 ? mode.rate - rate : mode.height - height : mode.width - width;
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
