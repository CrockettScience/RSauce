package demo.entities;

import sauce.core.Sprite;
import sauce.concurrent.Argument;
import sauce.concurrent.Return;
import sauce.concurrent.Script;
import sauce.core.Preferences;
import sauce.core.SpriteComponent;
import sauce.core.Engine;
import sauce.core.Entity;
import sauce.util.structures.Color;
import sauce.collision.BoundBox;
import sauce.util.structures.nonsaveable.ArrayGrid;

public class Button<A extends Argument, R extends Return> extends Entity {

    private Script<A, R> execute;
    private A invokationArgument;
    private Text tag;

    public Button(String imagePath, String label, int x, int y, int z, Script<A, R> invokation){
        ArrayGrid<String> matrix = new ArrayGrid<>(2, 1);
        {
            matrix.set(0, 0, "off");
            matrix.set(1, 0, "on");
        }
        Sprite buttonSprite = new Sprite(imagePath, 2, 1, matrix, false, 0);
        buttonSprite.setOrigin(buttonSprite.width() / 2, buttonSprite.height() / 2);

        BoundBox box = new BoundBox(x - buttonSprite.width() / 2, y - buttonSprite.height() / 2, buttonSprite.width(), buttonSprite.height());

        addComponent(new SpriteComponent(buttonSprite, x ,y, z));
        addComponent(box);

        Text text = new Text(Preferences.ASSET_ROOT + "coderCrux.ttf", Color.C_BLACK, label, buttonSprite.height() / 2, 0, 0, 0, 6);

        text.setX(x - text.getWidth() / 2);
        text.setY(y - text.getHeight() / 2);
        text.setZ(z - 1);

        execute = invokation;
        tag = text;

    }

    public Button(String imagePath, int x, int y, int z, Script<A, R> invokation){
        ArrayGrid<String> matrix = new ArrayGrid<>(2, 1);
        {
            matrix.set(0, 0, "off");
            matrix.set(1, 0, "on");
        }
        Sprite buttonSprite = new Sprite(imagePath, 2, 1, matrix, false, 0);
        buttonSprite.setOrigin(buttonSprite.width() / 2, buttonSprite.height() / 2);

        BoundBox box = new BoundBox(x - buttonSprite.width() / 2, y - buttonSprite.height() / 2, buttonSprite.width(), buttonSprite.height());

        addComponent(new SpriteComponent(buttonSprite, x ,y, z));
        addComponent(box);

        execute = invokation;

    }

    @Override
    protected void addedToEngine() {
        super.addedToEngine();

        if(tag != null)
            Engine.add(tag);
    }

    @Override
    protected void removedFromEngine() {
        super.removedFromEngine();

        if(tag != null)
            Engine.remove(tag);
    }

    public R invoke(){
        return execute.execute(invokationArgument);
    }

    public void setInvokationArgument(A argument){
        invokationArgument = argument;
    }

    @Override
    public void dispose() {
        super.dispose();
        if(tag != null)
            tag.dispose();
    }

    public void turnOn(){
        getComponent(SpriteComponent.class).getSprite().setAnimationState("on");
    }

    public void turnOff(){
        getComponent(SpriteComponent.class).getSprite().setAnimationState("off");
    }
}
