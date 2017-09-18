package com.sauce.core.engine;

import com.sauce.asset.graphics.Surface;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glBindFramebufferEXT;
import static org.lwjgl.opengl.GL11.*;

public class BackBuffer extends Surface {
    public BackBuffer(int width, int height) {
        super(width, height);
        setStaticMode(true);
    }

    public void bind(){
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, getHandle());
        glFramebufferTexture2DEXT( GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, texID(), 0);

        glPushAttrib(GL_VIEWPORT_BIT);
        glViewport(0,0, width(), height());

        glClear(GL_COLOR_BUFFER_BIT);
    }

    public void unbind(){
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
        glPopAttrib();
    }

    int texID(){
        return textureID();
    }
}
