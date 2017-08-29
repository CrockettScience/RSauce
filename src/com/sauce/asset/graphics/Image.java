package com.sauce.asset.graphics;

import org.lwjgl.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.stb.STBImage.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;

/**
 * Created by John Crockett.
 */
public class Image extends DrawableAsset {

    // Properties
    private ByteBuffer image;
    private int components;
    private int texID = glGenTextures();

    public Image(String imagePath){
        ByteBuffer buffer;

        try{
            buffer = ioResourceToByteBuffer(imagePath);
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

        super.lateConstructor(w.get(0), h.get(0), w.get(0), h.get(0));
        components = c.get(0);

    }

    private static ByteBuffer ioResourceToByteBuffer(String resource) throws IOException {
        ByteBuffer buffer;

        Path path = Paths.get(resource);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = BufferUtils.createByteBuffer((int)fc.size() + 1);
                while (fc.read(buffer) != -1) {}
            }
        } else {
            throw new IOException();
        }

        buffer.flip();
        return buffer;
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
        glBindTexture(GL_TEXTURE_2D, texID);

        if (components() == 3) {
            if ((absWidth() & 3) != 0) {
                glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (absWidth() & 1));
            }
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, absWidth(), absHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, image);
        } else {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, absWidth(), absHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);

        }

        return texID;
    }

    @Override
    public void update(int delta) {

    }

    @Override
    public void dispose() {
        glDeleteTextures(texID);
    }
}
