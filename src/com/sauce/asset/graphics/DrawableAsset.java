package com.sauce.asset.graphics;

import com.util.Vector2D;

import java.nio.ByteBuffer;

/**
 * Created by John Crockett.
 */
public abstract class DrawableAsset {

    // Properties
    protected int w;
    protected int h;
    private int absW;
    private int absH;
    protected float angle = 0.0f;
    protected float xScale = 1.0f;
    protected float yScale = 1.0f;
    private boolean staticMode = false;

    protected Vector2D origin = new Vector2D(0, 0);

    public DrawableAsset(int width, int height, int absWidth, int absHeight){
        this.w = width;
        this.h = height;

        this.absW = absWidth;
        this.absH = absHeight;
    }

    public DrawableAsset(){}

    protected final void resize(int width, int height, int absWidth, int absHeight){
        w = width;
        h = height;

        absW = absWidth;
        absH = absHeight;
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

    public Vector2D getOrigin(){
        return origin;
    }

    public void setOrigin(Vector2D newOrigin){
        origin = newOrigin;
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
