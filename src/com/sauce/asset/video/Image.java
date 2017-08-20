package com.sauce.asset.video;

import org.lwjgl.*;

import static org.lwjgl.stb.STBImage.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;

/**
 * Created by John Crockett.
 */
public class Image {

    // Properties
    private int width;
    private int height;
    private int components;
    private ByteBuffer image;

    private float angle;
    private float scale;

    // Precomputed Values
    private int halfWidth;
    private int halfHeight;

    public Image(String imagePath){
        ByteBuffer buffer;

        try{
            buffer = ioResourceToByteBuffer(imagePath, 1024);
        } catch(IOException e){
            throw new RuntimeException();
        }

        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer c = BufferUtils.createIntBuffer(1);

        if (!stbi_info_from_memory(buffer, w, h, c)) {
            throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
        }

        image = stbi_load_from_memory(buffer, w, h, c, 0);
        if(image == null){
            throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
        }

        width = w.get(0);
        height = h.get(0);
        components = c.get(0);

        angle = 0;
        scale = 1;

        halfWidth = width / 2;
        halfHeight = height / 2;

    }

    private static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;

        Path path = Paths.get(resource);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = BufferUtils.createByteBuffer((int)fc.size() + 1);
                while (fc.read(buffer) != -1) {
                    ;
                }
            }
        } else {
            throw new IOException();
        }

        buffer.flip();
        return buffer;
    }

    public int width(){
        return width;
    }

    public int height(){
        return height;
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

    int halfwidth(){
        return halfWidth;
    }

    int halfHeight(){
        return halfHeight;
    }

    // Only for use by DrawBatch Class
    int components(){
        return components;
    }

    ByteBuffer image(){
        return image;
    }
}
