package com.sauce.core.engine;

import com.util.structures.nonsaveable.ArrayList;
import com.util.structures.nonsaveable.Set;

import java.util.Iterator;

/**
 * Created by John Crockett.
 * Utility Structure that wraps a Set<Entity> int order to overhaul the set algebra methods
 * to compare entities based on their components.
 */
class EntitySet implements Iterable<Entity>{
    private Set<EntityEntry> entities;

    public int size() {
        return entities.size();
    }

    public boolean isEmpty() {
        return entities.isEmpty();
    }

    public boolean add(Entity e) {
        return entities.add(new EntityEntry(e));
    }

    public EntitySet onlyEntitiesWithComponent(Class<? extends Component> c) {
        EntitySet ents = new EntitySet();

        for (EntityEntry ent : entities) {
            if (ent.entity.hasComponent(c)) {
                ents.add(ent.entity);
            }
        }

        return ents;
    }

    public EntitySet onlyEntitiesWithoutComponent(Class<? extends Component> c) {
        EntitySet ents = new EntitySet();

        for (EntityEntry ent : entities) {
            if (!ent.entity.hasComponent(c)) {
                ents.add(ent.entity);
            }
        }

        return ents;
    }

    public void remove(Entity e) {
        EntityEntry ent = entities.remove(new EntityEntry(e));
        ent.removedFromEngine();
    }

    public void clear() {
        for(EntityEntry entry : entities){
            entry.removedFromEngine();
        }
        entities.clear();
    }

    public Entity[] toArray(){
        return toArray();
    }

    public Set<Entity> toSet(){
        Set<Entity> ents = new Set<>();
        for(EntityEntry entry : entities)
            ents.add(entry.entity);

        return ents;
    }

    @Override
    public Iterator<Entity> iterator() {
        return toSet().iterator();
    }

    private class EntityEntry{
        private Entity entity;
        private ArrayList<EntitySubscriber> subs;

        private EntityEntry(Entity ent){
            entity = ent;
        }

        private void removedFromEngine(){
            for(EntitySubscriber sub : subs)
                sub.entityRemovedFromEngine(entity);

            entity = null;
        }

        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof EntityEntry))
                return false;

            return entity.equals((Entity) obj);
        }
    }
}
