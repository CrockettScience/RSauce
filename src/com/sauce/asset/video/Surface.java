package com.sauce.asset.video;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Created by John Crockett.
 * A wrapper object that represents a GL_FRAMEBUFFER.
 */
public class Surface {

    private int fboHandle;
    private int width;
    private int height;

    public Surface(int width, int height){
        fboHandle = glGenFramebuffersEXT();


    }

    public void bind(){
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fboHandle);

        glPushAttrib(GL_VIEWPORT_BIT);
        glViewport(0,0, width, height);

        glClear(GL_COLOR_BUFFER_BIT);
    }

    public void unbind(){
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
        glPopAttrib();
    }

}
