package com.sauce.asset.video;

import java.nio.ByteBuffer;

/**
 * Created by John Crockett.
 */
public abstract class DrawableAsset {

    // Properties
    private int width;
    private int height;
    private float angle = 0.0f;
    private float scale = 1.0f;

    // Precomputed Values
    private int halfWidth;
    private int halfHeight;

    public DrawableAsset(int width, int height){
        this.width = width;
        this.height = height;

        halfWidth = width / 2;
        halfHeight = height / 2;
    }

    public DrawableAsset(){};

    protected void lateConstructor(int width, int height){
        this.width = width;
        this.height = height;

        halfWidth = width / 2;
        halfHeight = height / 2;
    }

    public int width(){
        return width;
    }

    public int height(){
        return height;
    }

    int halfwidth(){
        return halfWidth;
    }

    int halfHeight(){
        return halfHeight;
    }

    public void setAngle(float degrees){
        angle = degrees;
    }

    public float getAngle(){
        return angle;
    }

    public void setScale(float factor){
        scale = factor;
    }

    public float getScale(){
        return scale;
    }

    protected abstract int components();

    protected abstract ByteBuffer imageData();
}
