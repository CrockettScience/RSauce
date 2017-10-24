package demo.systems;

import sauce.core.Preferences;
import demo.entities.Eggy;
import sauce.asset.audio.AudioThread;
import sauce.asset.audio.Effect;
import sauce.asset.graphics.Sprite;
import sauce.core.engine.DrawComponent;
import sauce.core.engine.Engine;
import sauce.core.engine.StepSystem;
import util.collision.BoundBox;
import util.structures.nonsaveable.Map;

import static sauce.input.InputServer.*;

public class EggyControl extends StepSystem {

    private static int EGGY_BASE_SPEED = 1;

    private Map<Eggy, DrawComponent> components = new Map<>();
    private Map<Eggy, BoundBox> boxes = new Map<>();

    public EggyControl(int priority, Eggy eggy, DrawComponent eggyDrawComponent, BoundBox box){
        super(priority);
        components.put(eggy, eggyDrawComponent);
        boxes.put(eggy, box);
    }

    @Override
    public void addedToEngine(Engine engine) {

    }

    private double totalDelta = 0;
    @Override
    public void update(double delta) {
        for(Eggy eggy : components.keySet()) {

            DrawComponent eggyDraw = components.get(eggy);
            if (isKeyPressed(KEY_W) || isKeyPressed(KEY_A) || isKeyPressed(KEY_S) || isKeyPressed(KEY_D)) {
                if (isKeyPressed(KEY_W)) {
                    eggyDraw.setY(eggyDraw.getY() + EGGY_BASE_SPEED * (isKeyPressed(KEY_LEFT_SHIFT) ? 2 : 1));
                    ((Sprite) eggyDraw.getImage()).setAnimationState("up");
                }

                if (isKeyPressed(KEY_S)) {
                    eggyDraw.setY(eggyDraw.getY() - EGGY_BASE_SPEED * (isKeyPressed(KEY_LEFT_SHIFT) ? 2 : 1));
                    ((Sprite) eggyDraw.getImage()).setAnimationState("down");
                }

                if (isKeyPressed(KEY_A)) {
                    eggyDraw.setX(eggyDraw.getX() - EGGY_BASE_SPEED * (isKeyPressed(KEY_LEFT_SHIFT) ? 2 : 1));
                    ((Sprite) eggyDraw.getImage()).setAnimationState("left");
                }

                if (isKeyPressed(KEY_D)) {
                    eggyDraw.setX(eggyDraw.getX() + EGGY_BASE_SPEED * (isKeyPressed(KEY_LEFT_SHIFT) ? 2 : 1));
                    ((Sprite) eggyDraw.getImage()).setAnimationState("right");
                }

                // 8fps
                if (totalDelta >= 1.0 / 8) {
                    AudioThread.enqueue(new Effect(Preferences.ASSET_ROOT + "blip.ogg"));
                    totalDelta = 0;
                }


                boxes.get(eggy).moveTo(eggyDraw.getX(), eggyDraw.getY());

                totalDelta += delta;
            } else {
                ((Sprite) eggyDraw.getImage()).setAnimationState("idle");
                totalDelta = 0;
            }
        }


    }

    public boolean removeEggy(Eggy eggy){
        components.remove(eggy);
        return components.isEmpty();
    }

    public void addEggy(Eggy eggy, DrawComponent eggyDrawComponent, BoundBox box){
        components.put(eggy, eggyDrawComponent);
        boxes.put(eggy, box);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        components.clear();
    }
}
