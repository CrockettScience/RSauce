package com.sauce.core.engine;

import com.sauce.asset.graphics.TiledTexture;
import com.sauce.core.scene.Camera;
import com.sauce.core.scene.CameraChangeSubscriber;
import com.sauce.core.scene.SceneManager;
import com.sauce.util.io.GraphicsUtil;
import com.util.RSauceLogger;
import com.util.Vector2D;

public class ParallaxBackground extends TiledTexture implements CameraChangeSubscriber{

    private int xScroll;
    private int yScroll;
    private int xPos;
    private int yPos;

    public ParallaxBackground(String fileSource, int xScrollFactor, int yScrollFactor) {
        super(fileSource, SceneManager.getCamera().getWidth(), SceneManager.getCamera().getHeight());

        xScroll = xScrollFactor;
        yScroll = yScrollFactor;

        setOrigin(new Vector2D(w / 2, h / 2));

        SceneManager.subscribeToCameraChanges(this);
    }

    @Override
    public void update(double delta) {
        super.update(delta);
        xPos -= xScroll;
        yPos += yScroll;
    }

    @Override
    protected float[] regionCoordinates() {
        checkDisposed();
        float x = (float)xPos / absWidth();
        float y = (float)yPos / absWidth();
        float w = (float)width() / absWidth();
        float h = (float)height() / absHeight();

        float[] arr = {x, y, x + w, y, x + w, y - h, x, y - h};
        return arr;
    }

    int getXPos(){
        checkDisposed();
        return xPos;
    }

    int getYPos(){
        checkDisposed();
        return yPos;
    }

    @Override
    public void setAngle(float degrees) {
        RSauceLogger.printWarningln("ParallaxBackground rotation not supported");
    }

    @Override
    public void setXScale(float factor) {
        RSauceLogger.printWarningln("ParallaxBackground scaling not supported");
    }

    @Override
    public void setYScale(float factor) {
        RSauceLogger.printWarningln("ParallaxBackground scaling not supported");
    }

    public int getxScroll() {
        return xScroll;
    }

    public void setxScroll(int xScrollFactor) {
        xScroll = xScrollFactor;
    }

    public int getyScroll() {
        return yScroll;
    }

    public void setyScroll(int yScrollFactor) {
        yScroll = yScrollFactor;
    }

    public void setxPos(int xPosition) {
        xPos = xPosition;
    }

    public void setyPos(int yPosition) {
        yPos = -yPosition;
    }

    int texID(){
        checkDisposed();
        return textureID();
    }

    int getParallaxComponents(){
        checkDisposed();
        return components();
    }

    GraphicsUtil.IOGraphic getParallaxIOImage(){
        checkDisposed();
        return getIOImage();
    }

    @Override
    public void cameraResized(Vector2D newSize) {
        checkDisposed();
        w = newSize.getX();
        h = newSize.getY();
    }

    @Override
    public void cameraChanged(Camera newCamera) {
        checkDisposed();
        w = newCamera.getWidth();
        h = newCamera.getHeight();
        xPos = newCamera.getX();
        yPos = -newCamera.getY();
    }

    @Override
    public void cameraMovedPosition(Vector2D deltaPosition) {
        checkDisposed();
        xPos += deltaPosition.getX();
        yPos -= deltaPosition.getY();
    }

    @Override
    public void dispose() {
        checkDisposed();
        super.dispose();
        SceneManager.unsubscribeToCameraChanges(this);
    }
}
