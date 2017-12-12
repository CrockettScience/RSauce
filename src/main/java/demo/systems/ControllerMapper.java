package demo.systems;

import sauce.core.engine.StepSystem;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class ControllerMapper extends StepSystem {

    private int joyId;

    public ControllerMapper(int id){
        super(0);
        joyId = id;
    }

    @Override
    public void addedToEngine() {

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
    public void removedFromEngine() {

    }
}
