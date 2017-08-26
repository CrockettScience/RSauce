package com.sauce.core.engine;

import com.structures.nonsaveable.ArrayList;
import com.structures.nonsaveable.Set;
import com.structures.special.PriorityMap;

/**
 * Created by John Crockett.
 */
public class Engine {
    private EntitySet entities = new EntitySet();
    private ArrayList<EntitySubscriber> subs = new ArrayList<>();
    private PriorityMap<Class<? extends StepSystem>, StepSystem> steps = new PriorityMap<>();
    private PriorityMap<Class<? extends DrawSystem>, DrawSystem> draws = new PriorityMap<>();
    private Integer fps;

    public Engine(int maxUpdatesPerSecond){
        fps = maxUpdatesPerSecond;
    }

    public Engine(){
        fps = null;
    }

    public void bindEntitySubscriber(EntitySubscriber sub){
        subs.add(sub);
        sub.addQualifiedEntities(getEntityQualifier().all(sub.componentsToHave()).not(sub.componentsNotToHave()).getSet());
    }

    public void unbindEntitySubscriber(EntitySubscriber sub){
        subs.remove(sub);
    }

    public void add(StepSystem sys){
        steps.put(sys.getClass(), sys, sys.prio);
        sys.addedToEngine();
    }

    public void add(DrawSystem sys){
        draws.put(sys.getClass(), sys, sys.prio);
        sys.addedToEngine();
    }

    public void add(Entity ent){
        entities.add(ent);

        for(EntitySubscriber sub : subs){
            if(ent.hasAll(sub.componentsToHave()) && ent.hasNone(sub.componentsNotToHave()))
                sub.addQualifiedEntity(ent);
        }
    }

    public StepSystem removeStepSystem(Class<? extends StepSystem> sys){
        StepSystem ret = steps.get(sys);
        steps.remove(sys);
        return ret;
    }

    public DrawSystem removeDrawSystem(Class<? extends DrawSystem> sys){
        DrawSystem ret = draws.get(sys);
        draws.remove(sys);
        return ret;
    }

    public void removeEntity(Entity ent){
        entities.remove(ent);
    }

    public void clearSystems(){
        steps.clear();
        draws.clear();
    }

    public void clearEntities(){
        entities.clear();
    }

    public void update(int delta){
        // Step

        // Draw
    }

    private EntitySet onlyEntitiesWithComponent(Class<? extends Component> c){
        return entities.onlyEntitiesWithComponent(c);
    }

    private EntitySet onlyEntitiesWithoutComponent(Class<? extends Component> c){
        return entities.onlyEntitiesWithoutComponent(c);
    }

    public EntityQualifier getEntityQualifier(){
        return new EntityQualifier(entities);
    }

    public class EntityQualifier {
        private EntitySet ents;

        private EntityQualifier(EntitySet entities){
            ents = entities;
        }

        public EntityQualifier all(Class<? extends Component>... components){
            EntitySet ents = Engine.this.onlyEntitiesWithComponent(components[0]);
            for(int i = 1; i < components.length; i++){
                ents = ents.onlyEntitiesWithComponent(components[i]);
            }

            return this;
        }

        public EntityQualifier not(Class<? extends Component>... components){
            ents = Engine.this.onlyEntitiesWithoutComponent(components[0]);
            for(int i = 1; i < components.length; i++){
                ents = ents.onlyEntitiesWithoutComponent(components[i]);
            }

            return this;
        }

        public Set<Entity> getSet(){
            return ents.toSet();
        }
    }

}
