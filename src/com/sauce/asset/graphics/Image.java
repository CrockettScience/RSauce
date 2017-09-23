package com.sauce.asset.graphics;


import static org.lwjgl.opengl.GL11.*;
import static com.sauce.util.io.ResourceUtil.*;
import static com.sauce.util.io.GraphicsUtil.*;

import java.io.*;

/**
 * Created by John Crockett.
 */
public class Image extends Graphic {

    // Properties
    private IOGraphic image;
    private int components;
    private int texID = glGenTextures();

    public Image(String imagePath){
        IOResource resource;

        try{
            resource = loadResource(imagePath);
        } catch(IOException e){
            throw new RuntimeException();
        }

        GraphicInfo info = getGraphicInfo(resource);

        image = ioResourceToImage(resource, info);
        components = info.getComponents();
        resize(info.getWidth(), info.getHeight(), info.getWidth(), info.getHeight());

    }

    @Override
    protected float[] regionCoordinates() {
        float[] arr = {0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f};
        return arr;
    }

    @Override
    protected int components() {
        return components;
    }

    @Override
    protected int textureID() {
        return texID;
    }

    @Override
    public IOGraphic getIOImage() {
        return image;
    }

    @Override
    public void update(int delta) {

    }

    @Override
    public void dispose() {
        glDeleteTextures(texID);
    }
}
