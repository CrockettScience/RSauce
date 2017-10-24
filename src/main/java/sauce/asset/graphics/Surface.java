package sauce.asset.graphics;

import sauce.util.io.GraphicsUtil;
import sauce.util.ogl.OGLCoordinateSystem;
import util.RSauceLogger;
import util.Vector2D;
import util.structures.nonsaveable.Stack;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Created by John Crockett.
 * A wrapper object that represents a GL_FRAMEBUFFER.
 */
public class Surface extends Graphic {

    private int fboHandle;
    private int texID = glGenTextures();

    public Surface(int width, int height){
        super(width, height, width, height);

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
        OGLSurfaceSystem.pushSurface();
        OGLSurfaceSystem.setSurface(this);

        glPushAttrib(GL_VIEWPORT_BIT);
        glViewport(0,0, width(), height());

        OGLCoordinateSystem.pushCoordinateState();
        OGLCoordinateSystem.setCoordinateState(0, 0, width(), height());
    }

    public void unbind(){
        OGLSurfaceSystem.popSurface();
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
        float[] arr = {0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f};
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

    }

    public static class OGLSurfaceSystem {

        private static Stack<Integer> surfaceStack = new Stack<>();

        static{
            surfaceStack.push(0);
        }

        public static void pushSurface(){
            surfaceStack.push(surfaceStack.top());
        }

        public static void popSurface(){
            if(surfaceStack.isEmpty()){
                RSauceLogger.printErrorln("Surface Stack is empty!");
                return;
            }

            surfaceStack.pop();

            applyState();
        }

        public static void setSurface(Surface surface){
            if(!surfaceStack.isEmpty()){
                surfaceStack.pop();
            }

            surfaceStack.push(surface.fboHandle);

            applyState();
        }

        private static void  applyState(){
            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, surfaceStack.top());
        }
    }

}
