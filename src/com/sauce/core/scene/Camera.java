package com.sauce.core.scene;

import com.sauce.core.Project;
import com.sauce.core.engine.BackBuffer;
import com.sauce.core.engine.DrawComponent;
import com.sauce.core.engine.Entity;
import com.sauce.util.ogl.OGLCoordinateSystem;
import com.util.RSauceLogger;
import com.util.Vector2D;
import com.util.structures.nonsaveable.Set;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.glMatrixMode;

/**
 *
 * @author Jonathan Crockett
 */
public class Camera {

    private int x;
    private int y;
    private int width;
    private int height;

    private Entity entityFollowing;
    private int xBufferZone;
    private int yBufferZone;

    private BackBuffer cameraBuffer;

    private Set<CameraChangeSubscriber> cameraChangeSubscribers = new Set<>();

    public Camera(int aX, int aY, int aWidth, int aHeight, int xBuff, int yBuff) {
        x = aX;
        y = aY;
        width = aWidth;
        height = aHeight;
        xBufferZone = xBuff;
        yBufferZone = yBuff;

        cameraBuffer = new BackBuffer(Project.SCREEN_WIDTH, Project.SCREEN_HEIGHT);
    }

    void bindSubscriber(CameraChangeSubscriber sub){
        cameraChangeSubscribers.add(sub);
    }

    void removeSubscriber(CameraChangeSubscriber sub){
        cameraChangeSubscribers.remove(sub);
    }

    public void resize(int newWidth, int newHeight) {
        width = newWidth;
        height = newHeight;

        for(CameraChangeSubscriber sub : cameraChangeSubscribers){
            sub.cameraResized(new Vector2D(width, height));
        }

        reAdjustCoordinateSystem();
    }

    public void move(int newX, int newY){
        int oldX = x;
        int oldY = y;
        x = newX;
        y = newY;

        for(CameraChangeSubscriber sub: cameraChangeSubscribers){
            sub.cameraMovedPosition(new Vector2D(newX - oldX, newY - oldY));
        }

        reAdjustCoordinateSystem();
    }

    private void reAdjustCoordinateSystem(){
        OGLCoordinateSystem.setCoordinateState(x, y, width, height);
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

    public BackBuffer getCameraBuffer(){
        return cameraBuffer;
    }

    void dispose(){
        cameraChangeSubscribers.clear();
        cameraBuffer.dispose();
    }


}
