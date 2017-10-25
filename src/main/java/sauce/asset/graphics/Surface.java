package sauce.asset.graphics;

import sauce.util.io.GraphicsUtil;
import sauce.util.ogl.OGLCoordinateSystem;
import util.Color;
import util.RSauceLogger;
import util.structures.nonsaveable.Stack;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Created by John Crockett.
 * A wrapper object that represents a GL_FRAMEBUFFER.
 */
public class Surface extends Graphic {
    private int fboHandle = glGenFramebuffers();
    private int texID = glGenTextures();

    public Surface(int width, int height){
        super(width, height, width, height);

        glBindTexture(GL_TEXTURE_2D, texID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_INT, (java.nio.ByteBuffer) null);

        bind();
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texID, 0);
        unbind();

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
        OGLCoordinateSystem.popCoordinateState();
        glPopAttrib();

    }

    public void clear(Color color, float alpha){
        bind();
        float[] c = {color.getRed(), color.getGreen(), color.getBlue(), alpha};
        glClearBufferfv(GL_COLOR, 0, c);
        unbind();
    }

    public void dispose(){
        super.dispose();
        glDeleteFramebuffers(fboHandle);
        glDeleteTextures(texID);
    }

    @Override
    protected float[] regionCoordinates() {
        checkDisposed();
        return new float[]{0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f};
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

        private static void applyState(){
            glBindFramebuffer(GL_FRAMEBUFFER_EXT, surfaceStack.top());
        }
    }

}
