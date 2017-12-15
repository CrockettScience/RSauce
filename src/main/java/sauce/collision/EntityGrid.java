package sauce.collision;

import sauce.core.*;
import sauce.util.structures.Vector2D;
import sauce.util.structures.nonsaveable.HashGrid;
import sauce.util.structures.nonsaveable.Map;
import sauce.util.structures.nonsaveable.Set;

public class EntityGrid implements Attribute, EntitySubscriber {

    private Map<Entity, BoxEntry> boxes = new Map<>();
    private HashGrid<Bucket> buckets = new HashGrid<>();
    private int cSize;

    private EntityGrid(int cellSize){
        cSize = cellSize;
    }

    @Override
    public boolean addedToScene(Scene scn) {
        Engine.bindEntitySubscriber(this);
        BoundBox.setSceneGrid(this);
        return true;
    }

    @Override
    public void removedFromScene(Scene scn) {
        Engine.unbindEntitySubscriber(this);
        BoundBox.setSceneGrid(null);
    }

    @Override
    public Class<? extends Component>[] componentsToHave() {
        return new Class[]{BoundBox.class};
    }

    @Override
    public Class<? extends Component>[] componentsNotToHave() {
        return new Class[0];
    }

    @Override
    public boolean containsEntity(Entity e) {
        return boxes.containsKey(e);
    }

    @Override
    public void addQualifiedEntity(Entity ent) {
        boxes.put(ent, new BoxEntry(ent.getComponent(BoundBox.class)));
    }

    @Override
    public void addQualifiedEntities(Set<Entity> ents) {
        for(Entity ent : ents)
            addQualifiedEntity(ent);
    }

    @Override
    public void entityRemovedFromEngine(Entity ent) {
        boxes.remove(ent);
    }

    void boxMoved(BoundBox box){
        boxes.get(box.getEntity()).computeBuckets();
    }

    Set<BoundBox> getCollisions(BoundBox box){
        Set<BoundBox> collidingBoxes = new Set<>();

        for(Bucket bucket : boxes.get(box.getEntity()).buckets){
            bucket.getCollidingBoxes(box).union(collidingBoxes);
        }

        return collidingBoxes;
    }

    @Override
    public void entityNoLongerQualifies(Entity ent) {
        boxes.remove(ent);
    }

    private Bucket getBucketForPoint(Vector2D point){
        int x = point.getX() / cSize;
        int y = point.getY() / cSize;

        if(buckets.get(x, y) == null)
            buckets.set(x, y, new Bucket());

        return buckets.get(x, y);
    }

    private class BoxEntry{
        private Set<Bucket> buckets = new Set<>();
        private BoundBox boundBox;

        private BoxEntry(BoundBox box){
            boundBox = box;
            computeBuckets();
        }

        private void computeBuckets(){
            Set<Bucket> last = buckets.union(new Set<>());
            buckets.clear();

            BoundBox.Box box = boundBox.getOuterBox();
            Bucket bucket = getBucketForPoint(box.getUR());

            buckets.add(bucket);
            bucket.add(boundBox);

            if(!buckets.contains((bucket = getBucketForPoint(box.getUL()))))
                buckets.add(bucket);

            bucket.add(boundBox);

            if(!buckets.contains((bucket = getBucketForPoint(box.getLR()))))
                buckets.add(bucket);

            bucket.add(boundBox);

            if(!buckets.contains((bucket = getBucketForPoint(box.getLL()))))
                buckets.add(bucket);

            bucket.add(boundBox);

            for(Bucket b : last.not(buckets))
                b.remove(boundBox);
        }

    }

    private class Bucket{
        Set<BoundBox> boxes = new Set<>();

        private void add(BoundBox box){
            boxes.add(box);
        }

        private void remove(BoundBox box){
            boxes.remove(box);
        }

        private Set<BoundBox> getCollidingBoxes(BoundBox box){
            Set<BoundBox> collidingBoxes = new Set<>();
            for(BoundBox otherBox : boxes){
                if(box != otherBox && box.detectCollision(otherBox))
                    collidingBoxes.add(otherBox);

            }

            return collidingBoxes;

        }
    }

}
