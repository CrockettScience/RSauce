package sauce.core.engine;

import sauce.core.coreutil.ogl.OGLCoordinateSystem;
import util.RSauceLogger;
import util.Vector2D;
import util.structures.nonsaveable.Set;

/**
 *
 * @author Jonathan Crockett
 */
public class Camera {

    private int x;
    private int y;
    private int width;
    private int height;
    private boolean isActive;

    private Entity entityFollowing;
    private int xBufferZone;
    private int yBufferZone;

    private Set<CameraChangeSubscriber> cameraChangeSubscribers = new Set<>();

    public Camera(int aX, int aY, int aWidth, int aHeight, int xBuff, int yBuff) {
        x = aX;
        y = aY;
        width = aWidth;
        height = aHeight;
        xBufferZone = xBuff;
        yBufferZone = yBuff;

        isActive = false;
    }

    public Camera(int aX, int aY, int aWidth, int aHeight) {
        x = aX;
        y = aY;
        width = aWidth;
        height = aHeight;
        xBufferZone = 0;
        yBufferZone = 0;

        isActive = false;
    }

    void activate(){
        isActive = true;
        reAdjustCoordinateSystem();
    }

    void deactivate(){
        isActive = false;
    }

    void bindSubscriber(CameraChangeSubscriber sub){
        cameraChangeSubscribers.add(sub);
    }

    void removeSubscriber(CameraChangeSubscriber sub){
        cameraChangeSubscribers.remove(sub);
    }

    public void resize(int newWidth, int newHeight) {
        width = newWidth;
        height = newHeight;

        for(CameraChangeSubscriber sub : cameraChangeSubscribers){
            sub.cameraResized(Vector2D.create(width, height));
        }

        reAdjustCoordinateSystem();
    }

    public void move(int newX, int newY){
        int oldX = x;
        int oldY = y;
        x = newX;
        y = newY;

        for(CameraChangeSubscriber sub: cameraChangeSubscribers){
            sub.cameraMovedPosition(Vector2D.create(newX - oldX, newY - oldY));
        }

        reAdjustCoordinateSystem();
    }

    private void reAdjustCoordinateSystem(){
        if(isActive)
            OGLCoordinateSystem.setCoordinateState(x, y, width, height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void followEntity(Entity ent) {
        if (ent.getComponent(DrawComponent.class) == null) {
            RSauceLogger.printWarningln("The view attempted to follow an entity that does not have a position");
        } else {
            entityFollowing = ent;
        }
    }

    public Entity getEntityFollowing() {
        return entityFollowing;
    }

    public void setBufferZone(int xBuff, int yBuff) {
        xBufferZone = xBuff;
        yBufferZone = yBuff;
    }

    public float getXBufferZone() {
        return xBufferZone;
    }

    public float getYBufferZone() {
        return yBufferZone;
    }


    void dispose(){
        cameraChangeSubscribers.clear();
    }
}
