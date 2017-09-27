package com.util.collision;

import com.sauce.core.engine.*;
import com.util.Vector2D;
import com.util.Vector2DDouble;
import com.util.structures.nonsaveable.LinkedList;
import com.util.structures.nonsaveable.Set;

import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static com.util.DrawUtil.*;

public class CollisionUtil {

    static class InternalUtil{

        static Vector2DDouble getProjectedPoint(Vector2D p, Vector2D a){
            double projectionFactor = ((double)p.getX() * a.getX() + (double)p.getY() * a.getY()) / (pow(a.getX(), 2) + pow(a.getY(), 2));
            return new Vector2DDouble(projectionFactor * a.getX(), projectionFactor * a.getY());
        }

        static double dotProduct(Vector2DDouble a, Vector2DDouble b){
            return a.getX() * b.getX() + a.getY() * b.getY();
        }

        static Vector2D getRotatedPoint(Vector2D point, Vector2D about, double theta){
            return new Vector2D((int)(about.getX() + ((point.getX() - about.getX()) * cos(theta)) + ((point.getY() - about.getY()) * sin(theta))),
                                (int)(about.getY() - ((point.getX() - about.getX()) * sin(theta)) + ((point.getY() - about.getY()) * cos(theta))));
        }

    }

    public static class DrawBBoxWires extends DrawSystem implements EntitySubscriber{

        private LinkedList<Entity> boxes = new LinkedList<>();

        public DrawBBoxWires() {
            super(2147483647);
            Engine.getEngine().bindEntitySubscriber(this);
        }

        @Override
        public void addedToEngine(Engine engine) {

        }

        @Override
        public void update(int delta){
            for(Entity ent : boxes){
                BoundBox box = ent.getComponent(BoundBox.class);
                BoundBox.Box inner = box.getInnerBox();
                BoundBox.Box outer = box.getOuterBox();
                quad(12, inner.getUL(), inner.getUR(), inner.getLL(), inner.getLR(), C_RED, 1);
                quad(12, outer.getUL(), outer.getUR(), outer.getLL(), outer.getLR(), C_BLACK, 1);
            }
        }

        @Override
        public void removedFromEngine(Engine engine) {
            Engine.getEngine().unbindEntitySubscriber(this);
        }

        @Override
        public Class<? extends Component>[] componentsToHave() {
            Class[] arr = {BoundBox.class};
            return arr;
        }

        @Override
        public Class<? extends Component>[] componentsNotToHave() {
            return new Class[0];
        }

        @Override
        public boolean containsEntity(Entity e) {
            for(Entity ent : boxes){
                if(ent.equals(e)){
                    return true;
                }
            }
            return false;
        }

        @Override
        public void addQualifiedEntity(Entity ent) {
        boxes.add(ent);
        }

        @Override
        public void addQualifiedEntities(Set<Entity> ents) {
            for(Entity ent : ents){
                boxes.add(ent);
            }
        }

        @Override
        public void entityRemovedFromEngine(Entity ent) {
            for(int i = 0; i < boxes.size(); i++){
                if(boxes.get(i).equals(ent)){
                    boxes.remove(i);
                    return;
                }
            }
        }

        @Override
        public void entityNoLongerQualifies(Entity ent) {
            for(int i = 0; i < boxes.size(); i++){
                if(boxes.get(i).equals(ent)){
                    boxes.remove(i);
                    return;
                }
            }
        }
    }
}
