package sauce.collision;

import sauce.core.Component;
import sauce.core.Entity;
import sauce.util.RSauceLogger;
import sauce.util.structures.Vector2D;
import sauce.util.structures.nonsaveable.Set;

import static java.lang.Math.*;
import static sauce.util.structures.Vector2D.toVector2DDouble;
import static sauce.collision.CollisionUtil.InternalUtil.*;

public class BoundBox implements Component {

    private static EntityGrid sceneGrid = null;

    private Entity entity = null;

    private int x;
    private int y;
    private int width;
    private int height;
    private double angle;

    private double radius;
    private Vector2D center;

    private Vector2D upperLInner;
    private Vector2D upperRInner;
    private Vector2D lowerLInner;
    private Vector2D lowerRInner;

    private Vector2D upperLOuter;
    private Vector2D upperROuter;
    private Vector2D lowerLOuter;
    private Vector2D lowerROuter;

    static void setSceneGrid(EntityGrid grid){
        sceneGrid = grid;
    }

    public BoundBox(int posX, int posY, int wide, int high){
        x = posX;
        y = posY;

        width = wide;
        height = high;

        angle = 0;

        setup();
    }

    public BoundBox(int posX, int posY, int wide, int high, double radianAngle){
        x = posX;
        y = posY;

        width = wide;
        height = high;

        angle = radianAngle;

        setup();
    }

    private final void setup(){
        radius = (hypot(width, height)) / 2;
        center = Vector2D.create(x + width / 2, y + height / 2);

        upperLInner = Vector2D.create(x, y + height);
        upperRInner = Vector2D.create(x + width, y + height);
        lowerLInner = Vector2D.create(x, y);
        lowerRInner = Vector2D.create(x + width, y);

        upperLOuter = Vector2D.create(center.getX() - (int) radius, center.getY() + (int) radius);
        upperROuter = Vector2D.create(center.getX() + (int) radius, center.getY() + (int) radius);
        lowerLOuter = Vector2D.create(center.getX() - (int) radius, center.getY() - (int) radius);
        lowerROuter = Vector2D.create(center.getX() + (int) radius, center.getY() - (int) radius);
    }

    private void updatePosition(int dx, int dy){
        x += dx;
        y += dy;

        center.setX(center.getX() + dx);
        center.setY(center.getY() + dy);

        upperLInner.setX(upperLInner.getX() + dx);
        upperLInner.setY(upperLInner.getY() + dy);

        upperRInner.setX(upperRInner.getX() + dx);
        upperRInner.setY(upperRInner.getY() + dy);

        lowerLInner.setX(lowerLInner.getX() + dx);
        lowerLInner.setY(lowerLInner.getY() + dy);

        lowerRInner.setX(lowerRInner.getX() + dx);
        lowerRInner.setY(lowerRInner.getY() + dy);

        upperLOuter.setX(upperLOuter.getX() + dx);
        upperLOuter.setY(upperLOuter.getY() + dy);

        upperROuter.setX(upperROuter.getX() + dx);
        upperROuter.setY(upperROuter.getY() + dy);

        lowerLOuter.setX(lowerLOuter.getX() + dx);
        lowerLOuter.setY(lowerLOuter.getY() + dy);

        lowerROuter.setX(lowerROuter.getX() + dx);
        lowerROuter.setY(lowerROuter.getY() + dy);
    }

    public void moveTo(int newX, int newY){
        updatePosition(newX - x, newY - y);

        if(sceneGrid != null)
            sceneGrid.boxMoved(this);
    }

    public void resize(int newWidth, int newHeight){
        width = newWidth;
        height = newHeight;

        setup();

        if(sceneGrid != null)
            sceneGrid.boxMoved(this);
    }

    public void rotate(double radianAngle){
        angle = radianAngle;
    }

    public boolean detectCollision(BoundBox other){
        if(angle == other.angle)
            // Use simple algorithm for axis aligned boxes
            return( x          < other.x + other.width  &&
                    y          < other.y + other.height &&
                    x + width  > other.x                &&
                    y + height > other.y                );
        else{
            // Radial limiting
            if(sqrt(pow((double) center.getX() - other.center.getX(), 2.0) + pow((double) center.getY() - other.center.getY(), 2.0)) > radius + other.radius)
                return false;

            // Last Resort: Use the Separating Axis Theorem
            Box boxA = getInnerBox();
            Box boxB = other.getInnerBox();

            Vector2D[] axes = new Vector2D[4];

            axes[0] = Vector2D.create(boxA.UR.getX() - boxA.UL.getX(), boxA.UR.getY() - boxA.UL.getY());
            axes[1] = Vector2D.create(boxA.UR.getX() - boxA.LR.getX(), boxA.UR.getY() - boxA.LR.getY());
            axes[2] = Vector2D.create(boxB.UR.getX() - boxB.UL.getX(), boxB.UR.getY() - boxB.UL.getY());
            axes[3] = Vector2D.create(boxB.UR.getX() - boxB.LR.getX(), boxB.UR.getY() - boxB.LR.getY());

            for(Vector2D axis : axes){

                double[] boxAScalars = new double[4];
                double[] boxBScalars = new double[4];

                boxAScalars[0] = dotProduct(getProjectedPoint(boxA.UL, axis), toVector2DDouble(axis));
                boxAScalars[1] = dotProduct(getProjectedPoint(boxA.UR, axis), toVector2DDouble(axis));
                boxAScalars[2] = dotProduct(getProjectedPoint(boxA.LL, axis), toVector2DDouble(axis));
                boxAScalars[3] = dotProduct(getProjectedPoint(boxA.LR, axis), toVector2DDouble(axis));

                boxBScalars[0] = dotProduct(getProjectedPoint(boxB.UL, axis), toVector2DDouble(axis));
                boxBScalars[1] = dotProduct(getProjectedPoint(boxB.UR, axis), toVector2DDouble(axis));
                boxBScalars[2] = dotProduct(getProjectedPoint(boxB.LL, axis), toVector2DDouble(axis));
                boxBScalars[3] = dotProduct(getProjectedPoint(boxB.LR, axis), toVector2DDouble(axis));

                double maxA = boxAScalars[0], minA = boxAScalars[0], maxB = boxBScalars[0], minB = boxBScalars[0];

                for(int i = 1; i < 4; i++){
                    if(boxAScalars[i] > maxA)
                        maxA = boxAScalars[i];

                    if(boxBScalars[i] > maxB)
                        maxB = boxBScalars[i];

                    if(boxAScalars[i] < minA)
                        minA = boxAScalars[i];

                    if(boxBScalars[i] < minB)
                        minB = boxBScalars[i];
                }

                if(!(minA <= maxB && maxA >= minB))
                    return false;

            }

            return true;
        }
    }

    public boolean detectPointInside(Vector2D point) {
        if (angle == 0) {
            // Use simple algorithm for non-rotated boxes
            return (point.getX() < x + width &&
                    point.getY() < y + height &&
                    point.getX() > x &&
                    point.getY() > y);
        }
        else {
            // Last Resort: Rotate point and check for Collision
            Vector2D rotatedPoint = getRotatedPoint(point, center, -angle);

            return (rotatedPoint.getX() < x + width &&
                    rotatedPoint.getY() < y + height &&
                    rotatedPoint.getX() > x &&
                    rotatedPoint.getY() > y);
        }
    }

    public Set<Entity> getCollisions(){
        if(sceneGrid == null){
            RSauceLogger.printErrorln("Entity " + entity + " tried to check for a collision, but the current scene has no EntityGrid Attribute.");
            return null;
        }

        Set<Entity> collidingEntities = new Set<>();

        for(BoundBox box : sceneGrid.getCollisions(this))
            collidingEntities.add(box.entity);

        return collidingEntities;
    }

    public Set<Entity> checkForCollisionsAt(Vector2D point){
        Set<Entity> collidingEntities = new Set<>();

        int oldX = x;
        int oldY = y;

        moveTo(point.getX(), point.getY());

        collidingEntities = getCollisions();

        moveTo(oldX, oldY);

        return collidingEntities;
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

    public double getRadianAngle(){
        return angle;
    }

    Box getOuterBox(){
        return new Box(upperLOuter, upperROuter, lowerLOuter, lowerROuter);
    }

    Box getInnerBox(){
        return new Box(getRotatedPoint(upperLInner, center, angle), getRotatedPoint(upperRInner, center, angle),
                       getRotatedPoint(lowerLInner, center, angle), getRotatedPoint(lowerRInner, center, angle));
    }

    @Override
    public boolean addedToEntity(Entity ent) {
        entity = ent;
        return true;
    }

    @Override
    public void removedFromEntity(Entity ent) {
        entity = null;
    }

    public Entity getEntity(){
        return entity;
    }

    static class Box {
        private final Vector2D UL;
        private final Vector2D UR;
        private final Vector2D LL;
        private final Vector2D LR;

        private Box(Vector2D upperL, Vector2D upperR, Vector2D lowerL, Vector2D lowerR){
            UL = upperL;
            UR = upperR;
            LL = lowerL;
            LR = lowerR;
        }

        Vector2D getUL() {
            return UL;
        }

        Vector2D getUR() {
            return UR;
        }

        Vector2D getLL() {
            return LL;
        }

        Vector2D getLR() {
            return LR;
        }
    }
}
