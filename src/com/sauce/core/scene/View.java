package com.sauce.core.scene;


import com.sauce.core.Project;
import com.sauce.core.engine.DrawComponent;
import com.sauce.core.engine.Entity;
import com.sauce.core.engine.Engine;

/**
 *
 * @author Jonathan Crockett
 */
public class View{
    private static final Engine ENGINE = Engine.getEngine(Project.FRAME_LIMIT);

    private float x;
    private float y;
    private float width;
    private float height;

    private Entity entityFollowing;
    private float xBufferZone;
    private float yBufferZone;

    public View(float aX, float aY, float aWidth, float aHeight, float xBuff, float yBuff) {
        x = aX;
        y = aY;
        width = aWidth;
        height = aHeight;
        xBufferZone = xBuff;
        yBufferZone = yBuff;
    }

    public void resize(float newWidth, float newHeight) {
        width = newWidth;
        height = newHeight;
    }

    /**
     * @return the x
     */
    public float getX() {
        return x;
    }

    /**
     * @return the y
     */
    public float getY() {
        return y;
    }

    /**
     * @return the width
     */
    public float getWidth() {
        return width;
    }

    /**
     * @return the height
     */
    public float getHeight() {
        return height;
    }

    public void followEntity(Entity ent) {
        if (ent.getComponent(DrawComponent.class) == null) {
            System.out.println("WARNING: The view attempted to follow an entity that does not have a position");
        } else {
            entityFollowing = ent;
        }
    }

    public Entity getEntityFollowing() {
        return entityFollowing;
    }

    /**
     *
     * @param xBuff
     * @param yBuff
     */
    public void setBufferZone(float xBuff, float yBuff) {
        xBufferZone = xBuff;
        yBufferZone = yBuff;
    }

    public float getXBufferZone() {
        return xBufferZone;
    }

    public float getYBufferZone() {
        return yBufferZone;
    }

    /**
     * @param x the x to set
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * @param y the y to set
     */
    public void setY(float y) {
        this.y = y;
    }
}
