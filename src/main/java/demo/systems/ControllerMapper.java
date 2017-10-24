package demo.systems;

import sauce.core.engine.Engine;
import sauce.core.engine.StepSystem;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.glfwGetJoystickAxes;
import static org.lwjgl.glfw.GLFW.glfwGetJoystickButtons;
import static org.lwjgl.glfw.GLFW.glfwGetJoystickName;

public class ControllerMapper extends StepSystem {

    private int joyId;

    public ControllerMapper(int id){
        super(0);
        joyId = id;
    }

    @Override
    public void addedToEngine(Engine engine) {

        System.out.println("Controller Mapping Utility");
        String name = glfwGetJoystickName(joyId);
        FloatBuffer axes = glfwGetJoystickAxes(joyId);

        System.out.println("This Controller's name is: " + name + ".");
        System.out.println(name + " has " + axes.capacity() + " axes");
    }

    @Override
    public void update(double delta) {
        glfwGetJoystickButtons(joyId);
    }

    @Override
    public void removedFromEngine(Engine engine) {

    }
}
