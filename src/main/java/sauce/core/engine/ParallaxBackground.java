package sauce.core.engine;

import sauce.asset.graphics.TiledTexture;
import util.RSauceLogger;
import util.Vector2D;

public class ParallaxBackground extends TiledTexture implements CameraChangeSubscriber{

    private int xScroll;
    private int yScroll;
    private int xPos;
    private int yPos;

    public ParallaxBackground(String fileSource, int xScrollFactor, int yScrollFactor) {
        super(fileSource, 0, 0);
        resize(SceneManager.getCamera().getWidth() + getIOImage().getGraphicInfo().getWidth(), SceneManager.getCamera().getHeight() + getIOImage().getGraphicInfo().getHeight());

        xScroll = xScrollFactor;
        yScroll = yScrollFactor;

        SceneManager.subscribeToCameraChanges(this);
    }

    @Override
    public void update(double delta) {
        super.update(delta);
        xPos += xScroll;
        yPos += yScroll;
    }

    int getXPos(){
        checkDisposed();
        return xPos;
    }

    int getYPos(){
        checkDisposed();
        return yPos;
    }

    @Override
    public void setAngle(float degrees) {
        RSauceLogger.printWarningln("ParallaxBackground rotation not supported");
    }

    @Override
    public void setXScale(float factor) {
        RSauceLogger.printWarningln("ParallaxBackground scaling not supported");
    }

    @Override
    public void setYScale(float factor) {
        RSauceLogger.printWarningln("ParallaxBackground scaling not supported");
    }

    public int getxScroll() {
        return xScroll;
    }

    public void setxScroll(int xScrollFactor) {
        xScroll = xScrollFactor;
    }

    public int getyScroll() {
        return yScroll;
    }

    public void setyScroll(int yScrollFactor) {
        yScroll = yScrollFactor;
    }

    public void setxPos(int xPosition) {
        xPos = xPosition;
    }

    public void setyPos(int yPosition) {
        yPos = yPosition;
    }

    @Override
    public void cameraResized(Vector2D newSize) {
        checkDisposed();
        resize(newSize.getX() + getIOImage().getGraphicInfo().getWidth(), newSize.getY() + getIOImage().getGraphicInfo().getHeight());
    }

    @Override
    public void cameraChanged(Camera newCamera) {
        checkDisposed();
        resize(newCamera.getWidth() +getIOImage().getGraphicInfo().getWidth(), newCamera.getHeight() + getIOImage().getGraphicInfo().getHeight());
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
        super.dispose();
        SceneManager.unsubscribeToCameraChanges(this);
    }
}
