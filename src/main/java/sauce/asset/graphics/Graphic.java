package sauce.asset.graphics;

import sauce.util.io.GraphicsUtil;
import sauce.util.misc.AssetDisposedException;
import sauce.util.misc.Disposable;
import util.Vector2D;

/**
 * Created by John Crockett.
 */
public abstract class Graphic implements Disposable {
    protected boolean disposed = false;

    // Properties
    protected int width;
    protected int height;
    private int absWidth;
    private int absHeight;
    protected float angle = 0.0f;
    protected float xScale = 1.0f;
    protected float yScale = 1.0f;

    protected Vector2D center;
    protected Vector2D origin = new Vector2D(0, 0);

    public Graphic(int basicWidth, int basicHeight, int actualWidth, int actualHeight){
        width = basicWidth;
        height = basicHeight;

        absWidth = actualWidth;
        absHeight = actualHeight;

        center = new Vector2D(basicWidth / 2, basicHeight / 2);
    }

    public Graphic(){}

    protected final void resize(int basicWidth, int basicHeight, int actualWidth, int actualHeight){
        width = basicWidth;
        height = basicHeight;

        absWidth = actualWidth;
        absHeight = actualHeight;

        center = new Vector2D(basicWidth / 2, basicHeight / 2);
    }

    public int width(){
        return width;
    }

    public int height(){
        return height;
    }

    public int actualWidth() {
        return absWidth;
    }

    public int actualHeight() {
        return absHeight;
    }

    public void setAngle(float degrees){
        angle = degrees;
    }

    public float getAngle(){
        return angle;
    }

    public void setXScale(float factor){
        xScale = factor;

        // Refactor the origin
        origin = new Vector2D((int)(xScale * origin.getX()), origin.getY());
    }

    public float getXScale(){
        return xScale;
    }

    public void setYScale(float factor){
        yScale = factor;

        // Refactor the origin
        origin = new Vector2D(origin.getX(), (int)(yScale * origin.getY()));
    }

    public float getYScale(){
        return yScale;
    }

    public Vector2D getOrigin(){
        return origin;
    }

    public void setOrigin(Vector2D newOrigin){
        origin = newOrigin;
    }

    public void setOrigin(int x, int y){
        origin = new Vector2D(x, y);
    }

    protected abstract float[] regionCoordinates();

    protected abstract int components();

    protected abstract int textureID();

    public abstract GraphicsUtil.IOGraphic getIOImage();

    public abstract void update(double delta);

    public void dispose(){
        checkDisposed();
        disposed = true;
    }

    protected void checkDisposed(){
        if(disposed)
            throw new AssetDisposedException(this);

    }
}
