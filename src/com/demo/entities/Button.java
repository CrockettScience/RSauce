package com.demo.entities;

import com.sauce.asset.graphics.Sprite;
import com.sauce.asset.scripts.Argument;
import com.sauce.asset.scripts.Return;
import com.sauce.asset.scripts.Script;
import com.sauce.core.engine.DrawComponent;
import com.sauce.core.engine.Entity;
import com.util.collision.BoundBox;
import com.util.structures.nonsaveable.ArrayGrid;

public class Button<A extends Argument, R extends Return> extends Entity {

    private Script<A, R> execute;
    private A invokationArgument;

    public Button(String imagePath, Text text, int x, int y, int z, Script<A, R> invokation){
        ArrayGrid<String> matrix = new ArrayGrid<>(2, 1);
        {
            matrix.set(0, 0, "off");
            matrix.set(1, 0, "on");
        }
        Sprite buttonSprite = new Sprite(imagePath, 2, 1, matrix, false, 0);
        buttonSprite.setOrigin(buttonSprite.width() / 2, buttonSprite.height() / 2);

        BoundBox box = new BoundBox(x - buttonSprite.width() / 2, y - buttonSprite.height() / 2, buttonSprite.width(), buttonSprite.height());

        addComponent(new DrawComponent(buttonSprite, x ,y, z));
        addComponent(box);

        execute = invokation;

        text.setX(x - text.getWidth() / 2);
        text.setY(y - text.getHeight() / 2);

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

        addComponent(new DrawComponent(buttonSprite, x ,y, z));
        addComponent(box);

        execute = invokation;

    }

    public R invoke(){
        return execute.execute(invokationArgument);
    }

    public void setInvokationArgument(A argument){
        invokationArgument = argument;
    }

    @Override
    public void dispose() {
        getComponent(DrawComponent.class).getImage().dispose();
    }

    public void turnOn(){
        ((Sprite)getComponent(DrawComponent.class).getImage()).setAnimationState("on");
    }

    public void turnOff(){
        ((Sprite)getComponent(DrawComponent.class).getImage()).setAnimationState("off");
    }
}
