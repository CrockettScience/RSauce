package com.sauce.core.scene;


import com.sauce.core.Project;
import com.sauce.core.engine.DrawComponent;
import com.sauce.core.engine.Entity;
import com.sauce.core.engine.Engine;
import com.util.RSauceLogger;

/**
 *
 * @author Jonathan Crockett
 */
public class View{

    private int x;
    private int y;
    private int width;
    private int height;

    private Entity entityFollowing;
    private int xBufferZone;
    private int yBufferZone;

    public View(int aX, int aY, int aWidth, int aHeight, int xBuff, int yBuff) {
        x = aX;
        y = aY;
        width = aWidth;
        height = aHeight;
        xBufferZone = xBuff;
        yBufferZone = yBuff;
    }

    public void resize(int newWidth, int newHeight) {
        width = newWidth;
        height = newHeight;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void followEntity(Entity ent) {
        if (ent.getComponent(DrawComponent.class) == null) {
            RSauceLogger.printWarningln("The view attempted to follow an entity that does not have a position");
        } else {
            entityFollowing = ent;
        }
    }

    public Entity getEntityFollowing() {
        return entityFollowing;
    }

    public void setBufferZone(int xBuff, int yBuff) {
        xBufferZone = xBuff;
        yBufferZone = yBuff;
    }

    public float getXBufferZone() {
        return xBufferZone;
    }

    public float getYBufferZone() {
        return yBufferZone;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
