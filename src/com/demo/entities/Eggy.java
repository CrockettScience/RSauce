package com.demo.entities;

import com.sauce.asset.graphics.Sprite;
import com.sauce.core.engine.DrawComponent;
import com.sauce.core.engine.Entity;
import com.sauce.core.scene.SceneManager;
import com.sauce.input.InputClient;
import com.sauce.input.InputEvent;
import com.util.structures.nonsaveable.ArrayGrid;

import static com.sauce.core.Project.ASSET_ROOT;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

public class Eggy extends Entity implements InputClient {

    DrawComponent eggyComponent;

    public Eggy(){
        ArrayGrid<String> eggyMatrix = new ArrayGrid<>(4, 4);
        createEggyMatrix(eggyMatrix);

        Sprite eggySprite = new Sprite(ASSET_ROOT + "eggy.png",
                4,
                4,
                eggyMatrix,
                true,
                8);

        eggySprite.setXScale(16f);
        eggySprite.setYScale(16f);

        eggyComponent = new DrawComponent(eggySprite, SceneManager.getView().getWidth() / 2, SceneManager.getView().getHeight() / 2, 0);
        addComponent(eggyComponent);
    }

    private void createEggyMatrix(ArrayGrid<String> eggy) {
        String idle = "idle";
        String down = "down";
        String up = "up";
        String left = "left";
        String right = "right";

        eggy.set(0, 0, idle);
        eggy.set(1, 0, down);
        eggy.set(2, 0, down);
        eggy.set(3, 0, right);

        eggy.set(0, 1, right);
        eggy.set(1, 1, left);
        eggy.set(2, 1, left);
        eggy.set(3, 1, up);

        eggy.set(0, 2, up);
    }

    @Override
    public void receivedInputEvent(InputEvent event) {
        if(event.key() == GLFW_KEY_W){
            if(event.action() == GLFW_REPEAT) {
                eggyComponent.setY(eggyComponent.getY() + 8);
                ((Sprite) eggyComponent.getImage()).setAnimationState("up");
            } else
                ((Sprite) eggyComponent.getImage()).setAnimationState("idle");
        }

        if(event.key() == GLFW_KEY_S) {
            if (event.action() == GLFW_REPEAT) {
                eggyComponent.setY(eggyComponent.getY() - 8);
                ((Sprite) eggyComponent.getImage()).setAnimationState("down");
            } else
                ((Sprite) eggyComponent.getImage()).setAnimationState("idle");
        }

        if(event.key() == GLFW_KEY_A) {
            if (event.action() == GLFW_REPEAT) {
                eggyComponent.setX(eggyComponent.getX() - 8);
                ((Sprite) eggyComponent.getImage()).setAnimationState("left");
            } else
                ((Sprite) eggyComponent.getImage()).setAnimationState("idle");
        }

        if(event.key() == GLFW_KEY_D) {
            if (event.action() == GLFW_REPEAT) {
                eggyComponent.setX(eggyComponent.getX() + 8);
                ((Sprite) eggyComponent.getImage()).setAnimationState("right");
            } else
                ((Sprite) eggyComponent.getImage()).setAnimationState("idle");
        }
    }

    @Override
    public void dispose(){
        eggyComponent.getImage().dispose();
    }
}
