package com.sauce.asset.graphics;

import com.sauce.util.io.GraphicsUtil;
import com.util.Vector2D;

/**
 * Created by John Crockett.
 */
public abstract class Graphic {

    // Properties
    protected int w;
    protected int h;
    private int absW;
    private int absH;
    protected float angle = 0.0f;
    protected float xScale = 1.0f;
    protected float yScale = 1.0f;
    private boolean staticMode = false;

    Vector2D center;
    protected Vector2D origin = new Vector2D(0, 0);

    public Graphic(int width, int height, int absWidth, int absHeight){
        w = width;
        h = height;

        absW = absWidth;
        absH = absHeight;

        center = new Vector2D(w / 2, h / 2);
    }

    public Graphic(){}

    protected final void resize(int width, int height, int absWidth, int absHeight){
        w = width;
        h = height;

        absW = absWidth;
        absH = absHeight;
        center = new Vector2D(w / 2, h / 2);
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

    public abstract GraphicsUtil.IOGraphic getIOImage();

    protected void setStaticMode(boolean mode){
        staticMode = mode;
    }

    protected boolean isStatic(){
        return staticMode;
    }

    public abstract void update(int delta);

    public abstract void dispose();
}
