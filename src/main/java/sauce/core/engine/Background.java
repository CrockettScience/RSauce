package sauce.core.engine;

import sauce.core.coreutil.misc.AssetDisposedException;
import sauce.core.coreutil.misc.Disposable;
import util.Vector2D;

/**
 * Created by John Crockett.
 */
public class Background implements CameraChangeSubscriber, Disposable {
    boolean disposed = false;
    private float xScroll;
    private float yScroll;
    private float xPos;
    private float yPos;

    private Surface tiledSurface;
    private Sprite tile;

    public Background(String fileSource, float xScrollFactor, float yScrollFactor){
        xScroll = xScrollFactor;
        yScroll = yScrollFactor;

        tile = new Sprite(fileSource);

        updateTiledSurface(Engine.getCamera().getWidth(), Engine.getCamera().getHeight());

        Engine.subscribeToCameraChanges(this);
    }

    public Background(Surface fileSource, float xScrollFactor, float yScrollFactor){
        xScroll = xScrollFactor;
        yScroll = yScrollFactor;

        tile = new Sprite(fileSource);

        updateTiledSurface(Engine.getCamera().getWidth(), Engine.getCamera().getHeight());

        Engine.subscribeToCameraChanges(this);

    }

    private void updateTiledSurface(int width, int height) {
        if(tiledSurface != null)
            tiledSurface.dispose();

        int xCount = width / tile.width() + 2;
        int yCount = height / tile.height() + 2;

        tiledSurface = new Surface(xCount * tile.width(), yCount * tile.height());

        SpriteBatch batch = new SpriteBatch();

        for(int i = 0; i < xCount; i++){
            for(int j = 0; j < yCount; j++){
                batch.add(tile, i * tile.width(), j * tile.height());
            }
        }

        tiledSurface.bind();
        {
            batch.render();
        }
        tiledSurface.unbind();
    }

    private void checkDisposed() {
        if(disposed)
            throw new AssetDisposedException(this);
    }

    void update(double delta) {
        xPos = (float)(xPos + (xScroll * delta));
        yPos = (float)(yPos + (yScroll * delta));

        xPos = (xPos % tile.width() + tile.width()) % tile.width();
        yPos = (yPos % tile.height() + tile.height()) % tile.height();
    }

    void render(){
        tiledSurface.draw(xPos - tile.width() + Engine.getCamera().getX(), yPos - tile.height() + Engine.getCamera().getY());
    }

    public float getXPos(){
        checkDisposed();
        return xPos;
    }

    public float getYPos(){
        checkDisposed();
        return yPos;
    }

    public float getxScroll() {
        return xScroll;
    }

    public void setxScroll(int xScrollFactor) {
        xScroll = xScrollFactor;
    }

    public float getyScroll() {
        return yScroll;
    }

    public void setyScroll(int yScrollFactor) {
        yScroll = yScrollFactor;
    }

    public void setxPos(float xPosition) {
        xPos = xPosition;
    }

    public void setyPos(float yPosition) {
        yPos = yPosition;
    }

    @Override
    public void cameraResized(Vector2D newSize) {
        checkDisposed();
        updateTiledSurface(newSize.getX(), newSize.getY());
    }

    @Override
    public void cameraChanged(Camera newCamera) {
        checkDisposed();
        updateTiledSurface(newCamera.getX(), newCamera.getY());
        xPos = -newCamera.getX();
        yPos = -newCamera.getY();
    }

    @Override
    public void cameraMovedPosition(Vector2D delta) {
        checkDisposed();
        xPos -= delta.getX();
        yPos -= delta.getY();
    }

    @Override
    public void dispose() {
        checkDisposed();
        disposed = true;
        tile.dispose();
        Engine.unsubscribeToCameraChanges(this);
    }

    public Surface getTiledSurface(){
        return tiledSurface;
    }
}
