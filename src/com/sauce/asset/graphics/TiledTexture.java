package com.sauce.asset.graphics;

import com.sauce.core.Main;
import com.sauce.core.scene.SceneManager;

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

        super.lateConstructor(SceneManager.getView().getWidth(), SceneManager.getView().getHeight(), tileWidth, tileHeight);
    }

    @Override
    protected float[] regionCoordinates() {
        float w = SceneManager.getView().getWidth() / tileWidth;
        float h = SceneManager.getView().getHeight() / tileHeight;

        float[] arr = { 0, 0, w, 0, w, h, 0, h};
        return arr;
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
    public void update(int delta) {}

    @Override
    public void dispose() {
        image.dispose();
    }
}
