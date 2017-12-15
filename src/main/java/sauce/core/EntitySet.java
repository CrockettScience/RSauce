package sauce.core;

import sauce.util.structures.nonsaveable.Set;

import java.util.Iterator;

/**
 * Created by John Crockett.
 * Utility Structure that wraps a Set<Entity> int order to overhaul the set algebra methods
 * to compare entities based on their components.
 */
class EntitySet implements Iterable<Entity>{
    private Set<Entity> entities = new Set<>();

    int size() {
        return entities.size();
    }

    boolean isEmpty() {
        return entities.isEmpty();
    }

    boolean add(Entity e) {
        return entities.add(e);
    }

    boolean contains(Entity e){
        return entities.contains(e);
    }

    EntitySet onlyEntitiesWithComponent(Class<? extends Component> c) {
        EntitySet ents = new EntitySet();

        for (Entity ent : entities) {
            if (ent.hasComponent(c)) {
                ents.add(ent);
            }
        }

        return ents;
    }

    EntitySet onlyEntitiesWithoutComponent(Class<? extends Component> c) {
        EntitySet ents = new EntitySet();

        for (Entity ent : entities) {
            if (!ent.hasComponent(c)) {
                ents.add(ent);
            }
        }

        return ents;
    }

    void remove(Entity e) {
        Entity ent = entities.remove(e);
    }

    void clear() {
        entities.clear();
    }

    Set<Entity> toSet(){
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
