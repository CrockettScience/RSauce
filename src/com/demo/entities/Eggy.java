package com.demo.entities;

import com.demo.systems.EggyControl;
import com.sauce.asset.graphics.Sprite;
import com.sauce.core.engine.DrawComponent;
import com.sauce.core.engine.Engine;
import com.sauce.core.engine.Entity;
import com.util.structures.nonsaveable.ArrayGrid;

import static com.sauce.core.Preferences.ASSET_ROOT;

public class Eggy extends Entity{


    private static EggyControl controller;
    private DrawComponent eggyComponent;

    public Eggy(float xScale, float yScale, int x, int y, int z){
        ArrayGrid<String> eggyMatrix = new ArrayGrid<>(4, 4);
        createEggyMatrix(eggyMatrix);

        Sprite eggySprite = new Sprite(ASSET_ROOT + "eggy.png",
                4,
                4,
                eggyMatrix,
                true,
                8);

        eggySprite.setXScale(xScale);
        eggySprite.setYScale(yScale);

        eggyComponent = new DrawComponent(eggySprite, x, y, z);
        addComponent(eggyComponent);

        if(controller == null){
            controller = new EggyControl(0,this,  eggyComponent);
            Engine.getEngine().add(controller);
        }
        else{
            controller.addEggy(this, eggyComponent);
        }

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
    public void dispose(){
        super.dispose();
        eggyComponent.getImage().dispose();
        if(controller.removeEggy(this)){
            Engine.getEngine().removeStepSystem(EggyControl.class);
            controller = null;
        }
    }
}
