package com.sauce.core.scene;

import com.sauce.asset.graphics.TiledTexture;
import com.sauce.core.Project;

public class ParallaxBackground extends TiledTexture {

    private int xScroll;
    private int yScroll;
    private int xPos;
    private int yPos;

    public ParallaxBackground(String fileSource, int xScrollFactor, int yScrollFactor) {
        super(fileSource, Project.INTERNAL_WIDTH, Project.INTERNAL_HEIGHT);

        xScroll = xScrollFactor;
        yScroll = yScrollFactor;

        setStaticMode(true);
    }

    @Override
    public void update(int delta) {
        xPos += xScroll;
        yPos += yScroll;
    }

    @Override
    protected float[] regionCoordinates() {
        float w = (float)width() / (float)absWidth();
        float h = (float)height() / (float)absHeight();
        float xAdjust = (xPos - SceneManager.getView().getX() / getXScale()) / (float)absWidth();
        float yAdjust = (yPos - SceneManager.getView().getY() / getYScale()) / (float)absHeight();

        float[] arr = {0 - xAdjust, 0 - yAdjust, w - xAdjust, 0 - yAdjust, w - xAdjust, h - yAdjust, 0 - xAdjust, h - yAdjust};
        return arr;
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

    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPosition) {
        xPos = xPosition;
    }

    public int getyPos() {
        return yPos;
    }

    public void setyPos(int yPosition) {
        yPos = yPosition;
    }
}
