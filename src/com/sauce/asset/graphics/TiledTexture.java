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
        float w = (float)textureWidth / (float)tileWidth;
        float h = (float)textureHeight / (float)tileHeight;

        float[] arr = { 0, 0, w, 0, w, h, 0, h};
        return arr;
    }

    public void resize(int width, int height){
        textureWidth = width;
        textureHeight = height;

        resize(width, height, tileWidth, tileHeight);
    }

    @Override
    protected int components() {
        return image.components();
    }

    @Override
    protected int textureID() {
        return image.textureID();
    }

    @Override
    public GraphicsUtil.IOGraphic getIOImage() {
        return image.getIOImage();
    }

    @Override
    public void update(int delta) {}

    @Override
    public void dispose() {
        image.dispose();
    }
}
