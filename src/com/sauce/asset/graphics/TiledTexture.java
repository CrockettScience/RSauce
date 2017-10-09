package com.sauce.asset.graphics;

import com.sauce.util.io.GraphicsUtil;

/**
 * Created by John Crockett.
 */
public class TiledTexture extends Graphic {
    private Image image;
    private final int tileWidth;
    private final int tileHeight;
    private int textureWidth;
    private int textureHeight;

    public TiledTexture(String fileSource, int width, int height){
        image = new Image(fileSource);
        tileWidth = image.width();
        tileHeight = image.height();
        textureWidth = width;
        textureHeight = height;

        resize(width, height, tileWidth, tileHeight);
    }

    @Override
    protected float[] regionCoordinates() {
        checkDisposed();
        float w = (float)textureWidth / (float)tileWidth;
        float h = (float)textureHeight / (float)tileHeight;

        float[] arr = { 0, 0, w, 0, w, h, 0, h};
        return arr;
    }

    public void resize(int width, int height){
        checkDisposed();
        textureWidth = width;
        textureHeight = height;

        resize(width, height, tileWidth, tileHeight);
    }

    @Override
    protected int components() {
        checkDisposed();
        return image.components();
    }

    @Override
    protected int textureID() {
        checkDisposed();
        return image.textureID();
    }

    @Override
    public GraphicsUtil.IOGraphic getIOImage() {
        checkDisposed();
        return image.getIOImage();
    }

    @Override
    public void update(double delta) {
        checkDisposed();
    }

    @Override
    public void dispose(){
        super.dispose();
        image.dispose();
    }
}
