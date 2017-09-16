package com.sauce.asset.graphics;

import java.nio.ByteBuffer;

/**
 * Created by John Crockett.
 */
public abstract class DrawableAsset {

    // Properties
    private int w;
    private int h;
    private int absW;
    private int absH;
    private float angle = 0.0f;
    private float xScale = 1.0f;
    private float yScale = 1.0f;
    private boolean staticMode = false;

    // Precomputed Values
    private int halfWidth;
    private int halfHeight;

    public DrawableAsset(int width, int height, int absWidth, int absHeight){
        this.w = width;
        this.h = height;

        this.absW = absWidth;
        this.absH = absHeight;

        halfWidth = width / 2;
        halfHeight = height / 2;
    }

    public DrawableAsset(){}

    protected final void resize(int width, int height, int absWidth, int absHeight){
        w = width;
        h = height;

        absW = absWidth;
        absH = absHeight;

        halfWidth = width / 2;
        halfHeight = height / 2;
    }

    public int width(){
        return w;
    }

    public int height(){
        return h;
    }

    public int absWidth() {
        return absW;
    }

    public int absHeight() {
        return absH;
    }

    public void setAngle(float degrees){
        angle = degrees;
    }

    public float getAngle(){
        return angle;
    }

    public void setXScale(float factor){
        xScale = factor;
    }

    public float getXScale(){
        return xScale;
    }

    public void setYScale(float factor){
        yScale = factor;
    }

    public float getYScale(){
        return yScale;
    }

    int halfwidth(){
        return halfWidth;
    }

    int halfHeight(){
        return halfHeight;
    }

    protected abstract float[] regionCoordinates();

    protected abstract int components();

    protected abstract int textureID();

    protected void setStaticMode(boolean mode){
        staticMode = mode;
    }

    protected boolean isStatic(){
        return staticMode;
    }

    public abstract void update(int delta);

    public abstract void dispose();
}
