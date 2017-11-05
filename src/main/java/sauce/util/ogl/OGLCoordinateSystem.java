package sauce.util.ogl;

import util.RSauceLogger;
import util.structures.nonsaveable.Stack;

import static org.lwjgl.opengl.GL11.*;

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

    public static int[] getCoordinateState(){
        int[] state = new int[4];

        state[0] = coordinateStateStack.top().x;
        state[1] = coordinateStateStack.top().y;
        state[2] = coordinateStateStack.top().width;
        state[3] = coordinateStateStack.top().height;

        return state;
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
