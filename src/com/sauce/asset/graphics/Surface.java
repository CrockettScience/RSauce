package com.sauce.asset.graphics;

import com.sauce.core.Project;
import com.sauce.core.scene.SceneManager;
import com.util.Vector2D;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Created by John Crockett.
 * A wrapper object that represents a GL_FRAMEBUFFER.
 */
public class Surface extends DrawableAsset {

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

    }

    public void bind(){
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fboHandle);
        glFramebufferTexture2DEXT( GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, texID, 0);

        glPushAttrib(GL_VIEWPORT_BIT);
        glViewport(0,0, width, height);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0, width, 0.0, height, -1.0, 1.0);
        glMatrixMode(GL_MODELVIEW);

        glClear(GL_COLOR_BUFFER_BIT);
    }

    public void unbind(){
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
        glPopAttrib();

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0, SceneManager.getCamera().getWidth(), 0.0, SceneManager.getCamera().getHeight(), -1.0, 1.0);
        glMatrixMode(GL_MODELVIEW);
    }

    public void dispose(){
        glDeleteFramebuffersEXT(fboHandle);
        glDeleteTextures(texID);
    }

    @Override
    protected float[] regionCoordinates() {
        float[] arr = {0f, 0f, 1f, 0f, 1f, -1f, 0f, -1f};
        return arr;
    }

    @Override
    protected int components() {
        return 4;
    }

    @Override
    protected int textureID() {
        return texID;
    }

    protected int getHandle(){
        return fboHandle;
    }

    @Override
    public void update(int delta) {

    }
}
