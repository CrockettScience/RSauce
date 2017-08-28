package com.sauce.asset.graphics;

import com.sauce.core.Main;

import java.nio.ByteBuffer;

/**
 * Created by John Crockett.
 */
public class TiledTexture extends DrawableAsset {
    private Image image;
    private final int tileWidth;
    private final int tileHeight;

    public TiledTexture(String fileSource){
        image = new Image(fileSource);
        tileWidth = image.width();
        tileHeight = image.height();

        super.lateConstructor(Main.WIDTH, Main.HEIGHT, tileWidth, tileHeight);
    }

    @Override
    protected float[] regionCoordinates() {
        float w = Main.WIDTH / tileWidth;
        float h = Main.HEIGHT / tileHeight;

        float[] arr = { 0, 0, w, 0, w, h, 0, h};
        return arr;
    }

    @Override
    protected int components() {
        return image.components();
    }

    @Override
    protected ByteBuffer imageData() {
        return image.imageData();
    }

    @Override
    public void update(int delta) {}
}
