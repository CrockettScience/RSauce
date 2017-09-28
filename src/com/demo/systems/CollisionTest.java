package com.demo.systems;

import com.demo.entities.Eggy;
import com.sauce.asset.graphics.Sprite;
import com.Project;
import com.sauce.core.engine.DrawComponent;
import com.sauce.core.engine.Engine;
import com.sauce.core.engine.Entity;
import com.sauce.core.engine.StepSystem;
import com.sauce.input.InputClient;
import com.sauce.input.InputEvent;
import static com.sauce.input.InputServer.*;

import com.sauce.input.InputServer;
import com.util.collision.BoundBox;
import com.util.collision.CollisionUtil;
import com.util.structures.nonsaveable.ArrayGrid;

public class CollisionTest extends StepSystem implements InputClient {

    private Eggy eggy;
    private Entity collisionButton;

    public CollisionTest(int priority, Eggy spawner) {
        super(priority);

        eggy = spawner;
    }

    @Override
    public void addedToEngine(Engine engine) {

        collisionButton = new Entity();
        {
            ArrayGrid<String> buttonMatrix = new ArrayGrid<>(2, 1);
            {
                buttonMatrix.set(0, 0, "off");
                buttonMatrix.set(1, 0, "on");
            }

            Sprite buttonSprite = new Sprite(Project.ASSET_ROOT + "collisionTest.png", 2, 1, buttonMatrix, false, 0);
            buttonSprite.setAnimationState("off");

            DrawComponent buttonPos = new DrawComponent(buttonSprite, eggy.getComponent(DrawComponent.class).getX(), eggy.getComponent(DrawComponent.class).getY(), eggy.getComponent(DrawComponent.class).getZ() + 1);

            BoundBox box = new BoundBox(buttonPos.getX(), buttonPos.getY(), buttonSprite.width(), buttonSprite.height());

            collisionButton.addComponent(buttonPos);
            collisionButton.addComponent(box);
        }

        DrawComponent pos = eggy.getComponent(DrawComponent.class);
        int width = (int)(pos.getImage().width() * pos.getImage().getXScale());
        int height = (int)(pos.getImage().height() * pos.getImage().getYScale());

        eggy.addComponent(new BoundBox(pos.getX() - width / 2, pos.getY() - height / 2, width, height));

        Engine.getEngine().add(collisionButton);
        Engine.getEngine().add(new CollisionUtil.DrawBBoxWires());

        InputServer.bind(this);
    }

    @Override
    public void update(double delta) {
        if(eggy.getComponent(BoundBox.class).detectCollision(collisionButton.getComponent(BoundBox.class)))
            ((Sprite)collisionButton.getComponent(DrawComponent.class).getImage()).setAnimationState("on");
        else
            ((Sprite)collisionButton.getComponent(DrawComponent.class).getImage()).setAnimationState("off");

        if(isKeyPressed(KEY_EQUAL))
            eggy.getComponent(BoundBox.class).rotate(eggy.getComponent(BoundBox.class).getRadianAngle() + 0.1);

        if(isKeyPressed(KEY_MINUS))
            eggy.getComponent(BoundBox.class).rotate(eggy.getComponent(BoundBox.class).getRadianAngle() - 0.1);

        DrawComponent pos = eggy.getComponent(DrawComponent.class);
        int width = (int)(pos.getImage().width() * pos.getImage().getXScale());
        int height = (int)(pos.getImage().height() * pos.getImage().getYScale());

        eggy.getComponent(BoundBox.class).moveTo(pos.getX() - width / 2, pos.getY() - height / 2);

    }

    @Override
    public void removedFromEngine(Engine engine) {
        Engine.getEngine().removeEntity(collisionButton);
        eggy.removeComponent(BoundBox.class);
        Engine.getEngine().removeDrawSystem(CollisionUtil.DrawBBoxWires.class);

        collisionButton.getComponent(DrawComponent.class).getImage().dispose();

        InputServer.unbind(this);
    }

    @Override
    public void receivedKeyEvent(InputEvent event) {
        if(event.key() == KEY_Q && event.action() == ACTION_PRESSED){
            Engine.getEngine().removeStepSystem(this.getClass());
        }
    }

    @Override
    public void receivedTextEvent(char character) {

    }

    @Override
    public void receivedMouseButtonEvent(InputEvent event) {

    }

    @Override
    public void mouseScrolled(double x, double y) {

    }

    @Override
    public void cursorPosChanged(double x, double y) {
    }

    @Override
    public void joystickConnected(int joyID) {

    }

    @Override
    public void joystickDisconnected(int joyID) {

    }
}
