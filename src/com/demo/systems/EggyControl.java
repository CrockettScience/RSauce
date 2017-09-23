package com.demo.systems;

import com.demo.entities.Eggy;
import com.sauce.asset.audio.Audio;
import com.sauce.asset.audio.AudioThread;
import com.sauce.asset.audio.Effect;
import com.sauce.asset.graphics.Sprite;
import com.sauce.core.Project;
import com.sauce.core.engine.DrawComponent;
import com.sauce.core.engine.Engine;
import com.sauce.core.engine.StepSystem;
import com.util.structures.nonsaveable.Map;

import static com.sauce.input.InputServer.*;

public class EggyControl extends StepSystem {

    private Map<Eggy, DrawComponent> components = new Map<>();

    public EggyControl(int priority, Eggy eggy, DrawComponent eggyDrawComponent){
        super(priority);
        components.put(eggy, eggyDrawComponent);
    }

    @Override
    public void addedToEngine(Engine engine) {

    }

    private int totalDelta = 0;
    @Override
    public void update(int delta) {
        for(DrawComponent eggy : components.valueSet()) {
            if (isKeyPressed(KEY_W) || isKeyPressed(KEY_A) || isKeyPressed(KEY_S) || isKeyPressed(KEY_D)) {
                if (isKeyPressed(KEY_W)) {
                    eggy.setY(eggy.getY() + 20 * (isKeyPressed(KEY_LEFT_SHIFT) ? 2 : 1));
                    ((Sprite) eggy.getImage()).setAnimationState("up");
                }

                if (isKeyPressed(KEY_S)) {
                    eggy.setY(eggy.getY() - 20 * (isKeyPressed(KEY_LEFT_SHIFT) ? 2 : 1));
                    ((Sprite) eggy.getImage()).setAnimationState("down");
                }

                if (isKeyPressed(KEY_A)) {
                    eggy.setX(eggy.getX() - 20 * (isKeyPressed(KEY_LEFT_SHIFT) ? 2 : 1));
                    ((Sprite) eggy.getImage()).setAnimationState("left");
                }

                if (isKeyPressed(KEY_D)) {
                    eggy.setX(eggy.getX() + 20 * (isKeyPressed(KEY_LEFT_SHIFT) ? 2 : 1));
                    ((Sprite) eggy.getImage()).setAnimationState("right");
                }
                // 8fps
                if(totalDelta >= 125){
                    AudioThread.enqueue(new Effect(Project.ASSET_ROOT + "blip.ogg"));
                    totalDelta = 0;
                }

                totalDelta += delta;
            } else {
                ((Sprite) eggy.getImage()).setAnimationState("idle");
                totalDelta = 0;
            }
        }


    }

    public boolean removeEggy(Eggy eggy){
        components.remove(eggy);
        return components.isEmpty();
    }

    public void addEggy(Eggy eggy, DrawComponent eggyDrawComponent){
        components.put(eggy, eggyDrawComponent);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        components.clear();
    }
}
