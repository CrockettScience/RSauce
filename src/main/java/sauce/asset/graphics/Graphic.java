package sauce.asset.graphics;

import sauce.util.misc.AssetDisposedException;
import sauce.util.misc.Disposable;
import util.Vector2D;

/**
 * Created by John Crockett.
 */
public abstract class Graphic implements Disposable {
    private boolean disposed = false;

    // Properties
    protected int width;
    protected int height;
    private Vector2D origin = Vector2D.create(0, 0);
    private float angle = 0.0f;
    private float xScale = 1.0f;
    private float yScale = 1.0f;

    Vector2D center;
    TextureAtlas.TextureRegion region;

    Graphic(int basicWidth, int basicHeight, TextureAtlas.TextureRegion reg){
        width = basicWidth;
        height = basicHeight;

        region = reg;

        center = findCenter();
    }

    Graphic(int basicWidth, int basicHeight){
        width = basicWidth;
        height = basicHeight;

        center = findCenter();
    }

    Graphic(){};

    void resize(int basicWidth, int basicHeight, TextureAtlas.TextureRegion reg){
        width = basicWidth;
        height = basicHeight;

        region = reg;

        center = findCenter();
    }

    private Vector2D findCenter(){
        return region == null ? Vector2D.create(width / 2, height / 2) : Vector2D.create(region.p00.getX() + width / 2, region.p00.getY() + height / 2);
    }

    public int width(){
        return width;
    }

    public int height(){
        return height;
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
        origin = Vector2D.create((int)(xScale * origin.getX()), origin.getY());
    }

    public float getXScale(){
        return xScale;
    }

    public void setYScale(float factor){
        yScale = factor;

        // Refactor the origin
        origin = Vector2D.create(origin.getX(), (int)(yScale * origin.getY()));
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
        origin = Vector2D.create(x, y);
    }

    protected abstract float[] regionCoordinates();

    protected abstract int components();

    protected abstract int textureID();

    public abstract GraphicsUtil.IOGraphic getIOImage();

    public abstract void update(double delta);

    public void dispose(){
        checkDisposed();
        disposed = true;
        if(region != null)
            region.removeReference();
    }

    protected void checkDisposed(){
        if(disposed)
            throw new AssetDisposedException(this);

    }
}
