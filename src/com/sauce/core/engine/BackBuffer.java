package com.sauce.core.engine;

import com.sauce.asset.graphics.Surface;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glBindFramebufferEXT;
import static org.lwjgl.opengl.GL11.*;

public class BackBuffer extends Surface {
    public BackBuffer(int width, int height) {
        super(width, height);
    }

    public void bind(){
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, getHandle());

        glPushAttrib(GL_VIEWPORT_BIT);
        glViewport(0,0, width(), height());
    }

    public void unbind(){
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
        glPopAttrib();
    }

    int texID(){
        return textureID();
    }
}
