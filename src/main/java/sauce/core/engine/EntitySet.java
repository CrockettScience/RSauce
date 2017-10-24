package sauce.core.engine;

import util.structures.nonsaveable.ArrayList;
import util.structures.nonsaveable.Set;

import java.util.Iterator;

/**
 * Created by John Crockett.
 * Utility Structure that wraps a Set<Entity> int order to overhaul the set algebra methods
 * to compare entities based on their components.
 */
class EntitySet implements Iterable<Entity>{
    private Set<Entity> entities = new Set<>();

    public int size() {
        return entities.size();
    }

    public boolean isEmpty() {
        return entities.isEmpty();
    }

    public boolean add(Entity e) {
        return entities.add(e);
    }

    public boolean contains(Entity e){
        return entities.contains(e);
    }

    public EntitySet onlyEntitiesWithComponent(Class<? extends Component> c) {
        EntitySet ents = new EntitySet();

        for (Entity ent : entities) {
            if (ent.hasComponent(c)) {
                ents.add(ent);
            }
        }

        return ents;
    }

    public EntitySet onlyEntitiesWithoutComponent(Class<? extends Component> c) {
        EntitySet ents = new EntitySet();

        for (Entity ent : entities) {
            if (!ent.hasComponent(c)) {
                ents.add(ent);
            }
        }

        return ents;
    }

    public void remove(Entity e) {
        Entity ent = entities.remove(e);
    }

    public void clear() {
        entities.clear();
    }

    public Set<Entity> toSet(){
        Set<Entity> ents = new Set<>();
        for(Entity ent : entities)
            ents.add(ent);

        return ents;
    }

    @Override
    public Iterator<Entity> iterator() {
        return toSet().iterator();
    }

}
