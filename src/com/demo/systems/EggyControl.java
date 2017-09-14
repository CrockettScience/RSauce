package com.demo.systems;

import com.sauce.asset.graphics.Sprite;
import com.sauce.core.engine.DrawComponent;
import com.sauce.core.engine.Engine;
import com.sauce.core.engine.StepSystem;

import static com.sauce.input.InputServer.*;

public class EggyControl extends StepSystem {

    DrawComponent eggy;

    public EggyControl(int priority, DrawComponent eggyDrawComponent){
        super(priority);
        eggy = eggyDrawComponent;
    }

    @Override
    public void addedToEngine(Engine engine) {

    }

    @Override
    public void update(int delta) {
        if(isKeyPressed(KEY_W) || isKeyPressed(KEY_A) || isKeyPressed(KEY_S) || isKeyPressed(KEY_D)) {
            if (isKeyPressed(KEY_W)) {
                eggy.setY(eggy.getY() + 10 * (isKeyPressed(KEY_LEFT_SHIFT) ? 2 : 1));
                ((Sprite) eggy.getImage()).setAnimationState("up");
            }

            if (isKeyPressed(KEY_S)) {
                eggy.setY(eggy.getY() - 10 * (isKeyPressed(KEY_LEFT_SHIFT) ? 2 : 1));
                ((Sprite) eggy.getImage()).setAnimationState("down");
            }

            if (isKeyPressed(KEY_A)) {
                eggy.setX(eggy.getX() - 10 * (isKeyPressed(KEY_LEFT_SHIFT) ? 2 : 1));
                ((Sprite) eggy.getImage()).setAnimationState("left");
            }

            if (isKeyPressed(KEY_D)) {
                eggy.setX(eggy.getX() + 10 * (isKeyPressed(KEY_LEFT_SHIFT) ? 2 : 1));
                ((Sprite) eggy.getImage()).setAnimationState("right");
            }
        }
        else
            ((Sprite) eggy.getImage()).setAnimationState("idle");
    }

    @Override
    public void removedFromEngine(Engine engine) {

    }
}
