package com.sauce.input;

import com.sauce.core.Main;
import com.sauce.core.Preferences;
import com.sauce.core.scene.Camera;
import com.sauce.core.scene.SceneManager;
import com.util.Vector2D;
import com.util.Vector2DDouble;
import com.util.structures.special.RecyclePool;
import com.util.structures.threadsafe.SafeArrayList;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by John Crockett.
 */
public class InputServer {

    private static final int EVENT_TYPE_KEY = 0;
    private static final int EVENT_TYPE_TEXT = 1;
    private static final int EVENT_TYPE_MOUSE_BUTTON = 2;
    private static final int EVENT_TYPE_MOUSE_SCROLL = 3;
    private static final int EVENT_TYPE_MOUSE_MOVE = 4;
    private static final int EVENT_TYPE_JOYSTICK_CONNECT = 5;

    private static RecyclePool<InputEvent> inputEventPool = new RecyclePool<InputEvent>() {
        @Override
        protected InputEvent newElement() {
            return new InputEvent();
        }
    };

    static{
        glfwSetKeyCallback(Main.getWindowHandle(), (window, key, scanCode, action, mods) -> {
            if(action != GLFW_REPEAT)
                dispatch(EVENT_TYPE_KEY, key, action, mods, -1, -1);
        });

        glfwSetCharCallback(Main.getWindowHandle(), (window, character) -> {
            dispatch(EVENT_TYPE_TEXT, character,-1,-1, -1, -1);
        });

        glfwSetMouseButtonCallback(Main.getWindowHandle(), (window, button, action, mods) -> {
            dispatch(EVENT_TYPE_MOUSE_BUTTON, button, action, mods, -1, -1);
        });

        glfwSetScrollCallback(Main.getWindowHandle(), (window, x, y) -> {
            dispatch(EVENT_TYPE_MOUSE_SCROLL, -1, -1, -1, x, y);
        });

        glfwSetCursorPosCallback(Main.getWindowHandle(), (window, x, y) -> {
            dispatch(EVENT_TYPE_MOUSE_MOVE, -1, -1, -1, x, Preferences.getScreenHeight() - y);
        });

        glfwSetJoystickCallback((joyId, event) -> {
            dispatch(EVENT_TYPE_JOYSTICK_CONNECT, joyId, event, -1, -1, -1);
        });
    }

    private static SafeArrayList<InputClient> subscribers = new SafeArrayList<>();

    private static InputEvent event = null;

    private static void dispatch(int type, int key, int action, int mods, double x, double y){
        for(InputClient sub : subscribers){
            switch(type){
                case EVENT_TYPE_KEY:
                    event = inputEventPool.grab().setState(key, action, mods);
                    sub.receivedKeyEvent(event);
                    inputEventPool.toss(event);
                    event = null;
                    break;

                case EVENT_TYPE_TEXT:
                    sub.receivedTextEvent((char) key);
                    break;

                case EVENT_TYPE_MOUSE_BUTTON:
                    event = inputEventPool.grab().setState(key, action, mods);
                    sub.receivedMouseButtonEvent(event);
                    inputEventPool.toss(event);
                    event = null;
                    break;

                case EVENT_TYPE_MOUSE_MOVE:
                    sub.cursorPosChanged(x, y);
                    break;

                case EVENT_TYPE_MOUSE_SCROLL:
                    sub.mouseScrolled(x, y);
                    break;

                case EVENT_TYPE_JOYSTICK_CONNECT:
                    if(action == GLFW_CONNECTED)
                        sub.joystickConnected(key);
                    else
                        sub.joystickDisconnected(key);
                    break;
            }
        }
    }

    public static void bind(InputClient sub){
        subscribers.add(sub);
    }

    public static void unbind(InputClient sub){
        subscribers.remove(sub);
    }

    public static boolean isKeyPressed(int key){
        return glfwGetKey(Main.getWindowHandle(), key) == GLFW_PRESS;
    }

    public static boolean isKeyReleased(int key){
        return glfwGetKey(Main.getWindowHandle(), key) == GLFW_RELEASE;
    }

    public static boolean isButtonPressed(int button){
        return glfwGetMouseButton(Main.getWindowHandle(), button) == GLFW_PRESS;
    }

    public static boolean isButtonReleased(int button){
        return glfwGetMouseButton(Main.getWindowHandle(), button) == GLFW_RELEASE;
    }

    public static Vector2DDouble mouseScreenPosition(){
        double[] x = new double[1];
        double[] y = new double[1];

        glfwGetCursorPos(Main.getWindowHandle(), x, y);
        return new Vector2DDouble(x[0], Preferences.getScreenHeight() - y[0]);
    }

    public static Vector2D mouseScenePosition(){
        Vector2DDouble pos = mouseScreenPosition();
        Camera cam = SceneManager.getCamera();

        int sWidth = (Preferences.isFullscreen() ? Preferences.getFullscreenWidth() : Preferences.getScreenWidth());
        int sHeight = (Preferences.isFullscreen() ? Preferences.getFullscreenHeight() : Preferences.getScreenHeight());
        int cWidth = cam.getWidth();
        int cHeight = cam.getHeight();

        return new Vector2D((int)(((double)cWidth / sWidth) * pos.getX()), (int)(((double)cHeight / sHeight) * pos.getY()));
    }

    public static final int MOUSE_LEFT = GLFW_MOUSE_BUTTON_LEFT;
    public static final int MOUSE_RIGHT = GLFW_MOUSE_BUTTON_RIGHT;
    public static final int MOUSE_MIDDLE = GLFW_MOUSE_BUTTON_MIDDLE;

    public static final int ACTION_RELEASED = GLFW_RELEASE;
    public static final int ACTION_PRESSED = GLFW_PRESS;

    public static final int MOD_SHIFT = GLFW_MOD_SHIFT;
    public static final int MOD_CONTROL = GLFW_MOD_CONTROL;
    public static final int MOD_ALT = GLFW_MOD_ALT;
    public static final int MOD_SUPER = GLFW_MOD_SUPER;

    public static final int KEY_A = GLFW_KEY_A;
    public static final int KEY_B = GLFW_KEY_B;
    public static final int KEY_C = GLFW_KEY_C;
    public static final int KEY_D = GLFW_KEY_D;
    public static final int KEY_E = GLFW_KEY_E;
    public static final int KEY_F = GLFW_KEY_F;
    public static final int KEY_G = GLFW_KEY_G;
    public static final int KEY_H = GLFW_KEY_H;
    public static final int KEY_I = GLFW_KEY_I;
    public static final int KEY_J = GLFW_KEY_J;
    public static final int KEY_K = GLFW_KEY_K;
    public static final int KEY_L = GLFW_KEY_L;
    public static final int KEY_M = GLFW_KEY_M;
    public static final int KEY_N = GLFW_KEY_N;
    public static final int KEY_O = GLFW_KEY_O;
    public static final int KEY_P = GLFW_KEY_P;
    public static final int KEY_Q = GLFW_KEY_Q;
    public static final int KEY_R = GLFW_KEY_R;
    public static final int KEY_S = GLFW_KEY_S;
    public static final int KEY_T = GLFW_KEY_T;
    public static final int KEY_U = GLFW_KEY_U;
    public static final int KEY_V = GLFW_KEY_V;
    public static final int KEY_W = GLFW_KEY_W;
    public static final int KEY_X = GLFW_KEY_X;
    public static final int KEY_Y = GLFW_KEY_Y;
    public static final int KEY_Z = GLFW_KEY_Z;

    public static final int KEY_1 = GLFW_KEY_1;
    public static final int KEY_2 = GLFW_KEY_2;
    public static final int KEY_3 = GLFW_KEY_3;
    public static final int KEY_4 = GLFW_KEY_4;
    public static final int KEY_5 = GLFW_KEY_5;
    public static final int KEY_6 = GLFW_KEY_6;
    public static final int KEY_7 = GLFW_KEY_7;
    public static final int KEY_8 = GLFW_KEY_8;
    public static final int KEY_9 = GLFW_KEY_9;
    public static final int KEY_0 = GLFW_KEY_0;

    public static final int KEY_ESCAPE = GLFW_KEY_ESCAPE;
    public static final int KEY_APOSTROPHE = GLFW_KEY_APOSTROPHE;
    public static final int KEY_BACKSLASH = GLFW_KEY_BACKSLASH;
    public static final int KEY_BACKSPACE = GLFW_KEY_BACKSPACE;
    public static final int KEY_CAPS_LOCK = GLFW_KEY_CAPS_LOCK;
    public static final int KEY_COMMA = GLFW_KEY_COMMA;
    public static final int KEY_DELET = GLFW_KEY_DELETE;
    public static final int KEY_END = GLFW_KEY_END;
    public static final int KEY_ENTER = GLFW_KEY_ENTER;
    public static final int KEY_EQUAL = GLFW_KEY_EQUAL;
    public static final int KEY_MINUS = GLFW_KEY_MINUS;
    public static final int KEY_NUM_LOCK = GLFW_KEY_NUM_LOCK;
    public static final int KEY_TAB = GLFW_KEY_TAB;
    public static final int KEY_PAGE_DOWN = GLFW_KEY_PAGE_DOWN;
    public static final int KEY_PAGE_UP = GLFW_KEY_PAGE_UP;
    public static final int KEY_PAUSE = GLFW_KEY_PAUSE;
    public static final int KEY_SEMICOLON = GLFW_KEY_SEMICOLON;
    public static final int KEY_PERIOD = GLFW_KEY_PERIOD;
    public static final int KEY_PRINT_SCREEN = GLFW_KEY_PRINT_SCREEN;
    public static final int KEY_SCROLL_LOCK = GLFW_KEY_SCROLL_LOCK;
    public static final int KEY_SLASH = GLFW_KEY_SLASH;
    public static final int KEY_SPACE = GLFW_KEY_SPACE;
    public static final int KEY_WORLD_1 = GLFW_KEY_WORLD_1;
    public static final int KEY_WOELD_2 = GLFW_KEY_WORLD_2;
    public static final int KEY_GRAVE_ACCENT = GLFW_KEY_GRAVE_ACCENT;
    public static final int KEY_HOME = GLFW_KEY_HOME;
    public static final int KEY_INSERT = GLFW_KEY_INSERT;
    public static final int KEY_MENU = GLFW_KEY_MENU;

    public static final int KEY_LEFT_ALT = GLFW_KEY_LEFT_ALT;
    public static final int KEY_LEFT_BRACKET = GLFW_KEY_LEFT_BRACKET;
    public static final int KEY_LEFT_CONTROL = GLFW_KEY_LEFT_CONTROL;
    public static final int KEY_LEFT_SHIFT = GLFW_KEY_LEFT_SHIFT;
    public static final int KEY_LEFT_SUPER = GLFW_KEY_LEFT_SUPER;
    public static final int KEY_RIGHT_ALT = GLFW_KEY_RIGHT_ALT;
    public static final int KEY_RIGHT_BRACKET = GLFW_KEY_RIGHT_BRACKET;
    public static final int KEY_RIGHT_CONTROL = GLFW_KEY_RIGHT_CONTROL;
    public static final int KEY_RIGHT_SHIFT = GLFW_KEY_RIGHT_SHIFT;
    public static final int KEY_RIGHT_SUPER = GLFW_KEY_RIGHT_SUPER;

    public static final int KEY_UP = GLFW_KEY_UP;
    public static final int KEY_DOWN = GLFW_KEY_DOWN;
    public static final int KEY_RIGHT = GLFW_KEY_RIGHT;
    public static final int KEY_LEFT = GLFW_KEY_LEFT;

    public static final int KEY_F1 = GLFW_KEY_F1;
    public static final int KEY_F2 = GLFW_KEY_F2;
    public static final int KEY_F3 = GLFW_KEY_F3;
    public static final int KEY_F4 = GLFW_KEY_F4;
    public static final int KEY_F5 = GLFW_KEY_F5;
    public static final int KEY_F6 = GLFW_KEY_F6;
    public static final int KEY_F7 = GLFW_KEY_F7;
    public static final int KEY_F8 = GLFW_KEY_F8;
    public static final int KEY_F9 = GLFW_KEY_F9;
    public static final int KEY_F10 = GLFW_KEY_F10;
    public static final int KEY_F11 = GLFW_KEY_F11;
    public static final int KEY_F12 = GLFW_KEY_F12;
    public static final int KEY_F13 = GLFW_KEY_F13;
    public static final int KEY_F14 = GLFW_KEY_F14;
    public static final int KEY_F15 = GLFW_KEY_F15;
    public static final int KEY_F16 = GLFW_KEY_F16;
    public static final int KEY_F17 = GLFW_KEY_F17;
    public static final int KEY_F18 = GLFW_KEY_F18;
    public static final int KEY_F19 = GLFW_KEY_F19;
    public static final int KEY_F20 = GLFW_KEY_F20;
    public static final int KEY_F21 = GLFW_KEY_F21;
    public static final int KEY_F22 = GLFW_KEY_F22;
    public static final int KEY_F23 = GLFW_KEY_F23;
    public static final int KEY_F24 = GLFW_KEY_F24;
    public static final int KEY_F25 = GLFW_KEY_F25;

    public static final int KEY_KP_0 = GLFW_KEY_KP_0;
    public static final int KEY_KP_1 = GLFW_KEY_KP_1;
    public static final int KEY_KP_2 = GLFW_KEY_KP_2;
    public static final int KEY_KP_3 = GLFW_KEY_KP_3;
    public static final int KEY_KP_4 = GLFW_KEY_KP_4;
    public static final int KEY_KP_5 = GLFW_KEY_KP_5;
    public static final int KEY_KP_6 = GLFW_KEY_KP_6;
    public static final int KEY_KP_7 = GLFW_KEY_KP_7;
    public static final int KEY_KP_8 = GLFW_KEY_KP_8;
    public static final int KEY_KP_9 = GLFW_KEY_KP_9;
    public static final int KEY_KP_ADD = GLFW_KEY_KP_ADD;
    public static final int KEY_KP_DECIMAL = GLFW_KEY_KP_DECIMAL;
    public static final int KEY_KP_DIVIDE = GLFW_KEY_KP_DIVIDE;
    public static final int KEY_KP_ENTER = GLFW_KEY_KP_ENTER;
    public static final int KEY_KP_EQUAL = GLFW_KEY_KP_EQUAL;
    public static final int KEY_KP_MULTIPLY = GLFW_KEY_KP_MULTIPLY;
    public static final int KEY_KP_SUBTRACT = GLFW_KEY_KP_SUBTRACT;
}
