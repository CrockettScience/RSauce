package com.sauce.asset.graphics;

import com.sauce.util.io.GraphicsUtil;
import com.sauce.util.ogl.OGLCoordinateSystem;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Created by John Crockett.
 * A wrapper object that represents a GL_FRAMEBUFFER.
 */
public class Surface extends Graphic {

    private int fboHandle;
    private int width;
    private int height;
    private int texID = glGenTextures();

    public Surface(int w, int h){
        super(w, h, w, h);

        width = w;
        height = h;

        fboHandle = glGenFramebuffersEXT();

        glBindTexture(GL_TEXTURE_2D, texID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_INT, (java.nio.ByteBuffer) null);

        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fboHandle);
        glFramebufferTexture2DEXT( GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, texID, 0);

        OGLCoordinateSystem.pushCoordinateState();
        OGLCoordinateSystem.setCoordinateState(0, 0, width, height);

        glClear(GL_COLOR_BUFFER_BIT);

        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);

        OGLCoordinateSystem.popCoordinateState();
    }

    public void bind(){
        checkDisposed();
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fboHandle);

        glPushAttrib(GL_VIEWPORT_BIT);
        glViewport(0,0, width, height);

        OGLCoordinateSystem.pushCoordinateState();
        OGLCoordinateSystem.setCoordinateState(0, 0, width, height);
    }

    public void unbind(){
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
        glPopAttrib();

        OGLCoordinateSystem.popCoordinateState();
    }

    public void dispose(){
        super.dispose();
        glDeleteFramebuffersEXT(fboHandle);
        glDeleteTextures(texID);
    }

    @Override
    protected float[] regionCoordinates() {
        checkDisposed();
        float[] arr = {0f, 0f, 1f, 0f, 1f, -1f, 0f, -1f};
        return arr;
    }

    @Override
    protected int components() {
        checkDisposed();
        return 4;
    }

    @Override
    protected int textureID() {
        checkDisposed();
        return texID;
    }

    @Override
    public GraphicsUtil.IOGraphic getIOImage() {
        checkDisposed();
        return null;
    }

    protected int getHandle(){
        checkDisposed();
        return fboHandle;
    }

    @Override
    public void update(double delta) {
        checkDisposed();

    }
}
