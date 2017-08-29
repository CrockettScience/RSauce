package com.demo.scenes;

import com.sauce.asset.graphics.Sprite;
import com.sauce.asset.graphics.TiledTexture;
import com.sauce.core.Main;
import com.sauce.core.engine.DrawComponent;
import com.sauce.core.engine.Engine;
import com.sauce.core.engine.Entity;
import com.sauce.core.scene.BackgroundAttribute;
import com.sauce.core.scene.Scene;
import com.sauce.core.scene.SceneManager;
import com.sauce.input.InputClient;
import com.sauce.input.InputEvent;
import com.sauce.input.InputServer;
import com.util.structures.nonsaveable.ArrayGrid;

import static com.sauce.core.Project.ASSET_ROOT;
import static com.sauce.core.Project.FRAME_LIMIT;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

/**
 * Created by John Crockett.
 */
public class DemoScene extends Scene implements InputClient {

    // Resource Handles
    DrawComponent eggyComponent;

    @Override
    protected void loadResources() {
        // Setup Eggy
        Entity eggy = new Entity();

            ArrayGrid<String> eggyMatrix = new ArrayGrid<>(4, 4);
            createEggyMatrix(eggyMatrix);

            Sprite eggySprite = new Sprite(ASSET_ROOT + "eggy.png",
                    4,
                    4,
                    eggyMatrix,
                    true,
                    4);

            eggySprite.setXScale(4f);
            eggySprite.setYScale(4f);

            eggyComponent = new DrawComponent(eggySprite, SceneManager.getView().getWidth() / 2, SceneManager.getView().getHeight() / 2, 0);
            eggy.addComponent(eggyComponent);


        Engine engine = Engine.getEngine(FRAME_LIMIT);

        putEntity("eggy", eggy);

        // Setup Background
        BackgroundAttribute attr = new BackgroundAttribute();
        addAttribute(attr);

        attr.background_0 = new TiledTexture(ASSET_ROOT + "bg.png");

        // Bind to recieve InputEvents
        InputServer.bind(this);
    }

    @Override
    protected void destroyResources() {
        removeEntities();
        eggyComponent.getImage().dispose();
        getAttribute(BackgroundAttribute.class).background_0.dispose();
    }

    @Override
    protected void sceneMain() {
        activateEntity("eggy");
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
        switch (event.key()){
            case GLFW_KEY_ESCAPE:
                if(event.action() == GLFW_RELEASE)
                    Main.quitAtEndOfCycle();
                break;

            case GLFW_KEY_W:
                if(event.action() == GLFW_REPEAT) {
                    eggyComponent.setY(eggyComponent.getY() + 2);
                    ((Sprite) eggyComponent.getImage()).setAnimationState("up");
                } else
                    ((Sprite) eggyComponent.getImage()).setAnimationState("idle");
                break;

            case GLFW_KEY_S:
                if(event.action() == GLFW_REPEAT) {
                    eggyComponent.setY(eggyComponent.getY() - 2);
                    ((Sprite) eggyComponent.getImage()).setAnimationState("down");
                } else
                    ((Sprite) eggyComponent.getImage()).setAnimationState("idle");
                break;

            case GLFW_KEY_A:
                if(event.action() == GLFW_REPEAT) {
                    eggyComponent.setX(eggyComponent.getX() - 2);
                    ((Sprite) eggyComponent.getImage()).setAnimationState("left");
                } else
                    ((Sprite) eggyComponent.getImage()).setAnimationState("idle");
                break;

            case GLFW_KEY_D:
                if(event.action() == GLFW_REPEAT) {
                    eggyComponent.setX(eggyComponent.getX() + 2);
                    ((Sprite) eggyComponent.getImage()).setAnimationState("right");
                } else
                    ((Sprite) eggyComponent.getImage()).setAnimationState("idle");
                break;
        }
    }
}
