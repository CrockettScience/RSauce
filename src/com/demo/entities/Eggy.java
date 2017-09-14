package com.demo.entities;

import com.demo.systems.EggyControl;
import com.sauce.asset.graphics.Sprite;
import com.sauce.core.engine.DrawComponent;
import com.sauce.core.engine.Engine;
import com.sauce.core.engine.Entity;
import com.sauce.core.scene.SceneManager;
import com.util.structures.nonsaveable.ArrayGrid;

import static com.sauce.core.Project.ASSET_ROOT;

public class Eggy extends Entity{

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

        Engine.getEngine(60).add(new EggyControl(0, eggyComponent));
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
        eggyComponent.getImage().dispose();
    }
}
