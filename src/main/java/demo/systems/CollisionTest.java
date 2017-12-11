package demo.systems;

import demo.entities.Eggy;
import sauce.asset.graphics.Sprite;
import sauce.core.Preferences;
import sauce.core.DrawComponent;
import sauce.core.Engine;
import sauce.core.Entity;
import sauce.core.StepSystem;
import sauce.input.InputClient;
import sauce.input.InputEvent;
import sauce.input.InputServer;
import util.collision.BoundBox;
import util.collision.CollisionUtil;
import util.structures.nonsaveable.ArrayGrid;

import static sauce.input.InputServer.*;

public class CollisionTest extends StepSystem implements InputClient {

    private Eggy eggy;
    private Entity collisionButton;

    public CollisionTest(int priority, Eggy spawner) {
        super(priority);

        eggy = spawner;
    }

    @Override
    public void addedToEngine() {
        {
            ArrayGrid<String> buttonMatrix = new ArrayGrid<>(2, 1);
            {
                buttonMatrix.set(0, 0, "off");
                buttonMatrix.set(1, 0, "on");
            }

            Sprite buttonSprite = new Sprite(Preferences.ASSET_ROOT + "button.png", 2, 1, buttonMatrix, false, 0);

            collisionButton = new Entity();

            buttonSprite.setAnimationState("off");

            DrawComponent buttonPos = new DrawComponent(buttonSprite, eggy.getComponent(DrawComponent.class).getX(), eggy.getComponent(DrawComponent.class).getY(), eggy.getComponent(DrawComponent.class).getZ() + 1);

            BoundBox box = new BoundBox(buttonPos.getX(), buttonPos.getY(), buttonSprite.width(), buttonSprite.height());

            collisionButton.addComponent(buttonPos);
            collisionButton.addComponent(box);
        }

        Engine.add(collisionButton);
        Engine.add(new CollisionUtil.DrawBBoxWires());

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

    }

    @Override
    public void removedFromEngine() {
        Engine.remove(collisionButton);
        eggy.removeComponent(BoundBox.class);
        Engine.remove(CollisionUtil.DrawBBoxWires.class);

        collisionButton.dispose();

        InputServer.unbind(this);
    }

    @Override
    public void receivedKeyEvent(InputEvent event) {
        if(event.key() == KEY_Q && event.action() == ACTION_PRESSED){
            Engine.remove(this.getClass());
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
