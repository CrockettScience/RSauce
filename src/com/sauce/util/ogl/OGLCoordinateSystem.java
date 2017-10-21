package com.sauce.util.ogl;

import com.sauce.core.Preferences;
import com.util.RSauceLogger;
import com.util.structures.nonsaveable.Stack;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.glMatrixMode;

public class OGLCoordinateSystem {

    private static Stack<CoordinateState> coordinateStateStack = new Stack<>();

    public static void pushCoordinateState(){
        coordinateStateStack.push(coordinateStateStack.top());
    }

    public static void popCoordinateState(){
        if(coordinateStateStack.isEmpty()){
            RSauceLogger.printErrorln("Coordinate State Stack is empty!");
            return;
        }

        coordinateStateStack.pop();

        applyState();
    }

    public static void setCoordinateState(int x, int y, int width, int height){
        if(!coordinateStateStack.isEmpty()){
            coordinateStateStack.pop();
        }

        coordinateStateStack.push(new CoordinateState(x, y, width, height));

        applyState();
    }

    private static void  applyState(){
        CoordinateState current = coordinateStateStack.top();
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(current.x, current.x + current.width, current.y, current.y + current.height, -1.0, 1.0);
        glMatrixMode(GL_MODELVIEW);
    }

    private static class CoordinateState{
        int x;
        int y;
        int width;
        int height;

        CoordinateState(int aX, int aY, int aWidth, int aHeight){
            x = aX;
            y = aY;
            width = aWidth;
            height = aHeight;
        }
    }
}
