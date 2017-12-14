package sauce.core.engine;

import sauce.core.coreutil.misc.AssetDisposedException;
import sauce.core.coreutil.misc.Disposable;
import util.Color;
import util.RSauceLogger;
import util.structures.nonsaveable.Stack;

import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Created by John Crockett.
 * A wrapper object that represents a GL_FRAMEBUFFER.
 */
public class Surface implements Disposable {
    private boolean disposed = false;

    private int width;
    private int height;
    private int fboHandle = glGenFramebuffers();
    private int texID = glGenTextures();

    public Surface(int aWidth, int aHeight){
        setWidth(aWidth);
        setHeight(aHeight);

        GraphicsUtil.applyBufferToTexture(null, getWidth(), getHeight(), 4, texID);

        bind();
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texID, 0);
        unbind();

    }

    public void bind(){
        checkDisposed();
        OGLSurfaceSystem.pushSurface();
        OGLSurfaceSystem.setSurface(this);

        glPushAttrib(GL_VIEWPORT_BIT);
        glViewport(0,0, getWidth(), getHeight());

        CoordinateSystem.pushCoordinateState();
        CoordinateSystem.setCoordinateState(0, 0, getWidth(), getHeight());
    }

    private void checkDisposed() {
        if(disposed)
            throw new AssetDisposedException(this);
    }

    public void unbind(){
        OGLSurfaceSystem.popSurface();
        CoordinateSystem.popCoordinateState();
        glPopAttrib();

    }

    public void draw(float x, float y){

        glBindTexture(GL_TEXTURE_2D, textureID());

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glEnable(GL_TEXTURE_2D);

        glPushMatrix();

        glBegin(GL_QUADS);
        {
            glTexCoord2f(0, 0);
            glVertex2f(x, y);

            glTexCoord2f(1, 0);
            glVertex2f(x + getWidth(), y);

            glTexCoord2f(1, 1);
            glVertex2f(x + getWidth(), y + getHeight());

            glTexCoord2f(0, 1);
            glVertex2f(x, y + getHeight());
        }
        glEnd();

        glPopMatrix();
    }

    public void clear(Color color, float alpha){
        bind();
        float[] c = {color.getRed(), color.getGreen(), color.getBlue(), alpha};
        glClearBufferfv(GL_COLOR, 0, c);
        unbind();
    }

    public void dispose(){
        checkDisposed();
        disposed = true;
        glDeleteFramebuffers(fboHandle);
        glDeleteTextures(texID);
    }

    int components() {
        checkDisposed();
        return 4;
    }

    int textureID() {
        checkDisposed();
        return texID;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    private static class OGLSurfaceSystem {

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
