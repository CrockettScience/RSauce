package demo.entities;

import demo.systems.EggyControl;
import sauce.asset.graphics.Sprite;
import sauce.core.engine.DrawComponent;
import sauce.core.engine.Engine;
import sauce.core.engine.Entity;
import sauce.core.collision.BoundBox;
import util.structures.nonsaveable.ArrayGrid;

import static sauce.core.engine.Preferences.ASSET_ROOT;

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

        int width = (int)(eggySprite.width() * xScale);
        int height = (int)(eggySprite.height() * yScale);
        BoundBox box = new BoundBox(x, y, width, height);

        addComponent(box);
        addComponent(eggyComponent);

        if(controller == null){
            controller = new EggyControl(0,this,  eggyComponent, box);
            Engine.add(controller);
        }
        else{
            controller.addEggy(this, eggyComponent, box);
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
        if(controller.removeEggy(this)){
            Engine.remove(EggyControl.class);
            controller = null;
        }
    }
}
