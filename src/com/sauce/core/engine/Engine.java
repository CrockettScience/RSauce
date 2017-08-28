package com.sauce.core.engine;

import com.sauce.asset.graphics.DrawBatch;
import com.sauce.asset.graphics.Surface;
import com.sauce.core.scene.SceneManager;
import com.sun.org.apache.regexp.internal.RE;
import com.util.structures.nonsaveable.ArrayList;
import com.util.structures.nonsaveable.Set;
import com.util.structures.special.PriorityMap;
import com.util.structures.special.SortedArrayList;
import javafx.scene.Scene;

import java.util.Comparator;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

/**
 * Created by John Crockett.
 */
public final class Engine {
    private static Engine singletonEngine;

    private EntitySet entities = new EntitySet();
    private ArrayList<EntitySubscriber> subs = new ArrayList<>();
    private PriorityMap<Class<? extends StepSystem>, StepSystem> steps = new PriorityMap<>();
    private PriorityMap<Class<? extends DrawSystem>, DrawSystem> draws = new PriorityMap<>();
    private Integer fpms;
    private RenderSystem render = new RenderSystem(0);

    private Engine(int maxUpdatesPerSecond) {
        fpms = 1000 / maxUpdatesPerSecond;
        render.addedToEngine(this);
    }

    public static Engine getEngine(int maxUpdatesPerSecond){
        if(singletonEngine == null)
            singletonEngine = new Engine(maxUpdatesPerSecond);

        return singletonEngine;
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
        sys.addedToEngine(this);
    }

    public void add(DrawSystem sys){
        draws.put(sys.getClass(), sys, sys.prio);
        sys.addedToEngine(this);
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
        ret.removedFromEngine(this);
        return ret;
    }

    public DrawSystem removeDrawSystem(Class<? extends DrawSystem> sys){
        DrawSystem ret = draws.get(sys);
        draws.remove(sys);
        ret.removedFromEngine(this);
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

    private Surface backBuffer = new Surface(800, 600);
    private int timeSinceLastUpdate;

    public void update(int delta){
        if (timeSinceLastUpdate >= fpms) {
            step(delta);

            backBuffer.bind();

            preDraw(delta);

            draw(delta);

            backBuffer.unbind();

            swapBuffer();
        } else {
            timeSinceLastUpdate += delta;
        }
    }

    private void step(int delta){
        for (int i = 0; i < steps.size(); i++) {
            steps.next().update(delta);
        }
    }

    private void preDraw(int delta){

        render.update(delta);
    }

    private void draw(int delta){
        for (int i = 0; i < draws.size(); i++) {
            draws.next().update(delta);
        }
    }

    private void swapBuffer(){
        render.batch.add(backBuffer, 0, 0);
        render.batch.renderBatch();
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

    private static class RenderSystem extends System implements EntitySubscriber{
        private SortedArrayList<Entity> entities = new SortedArrayList<Entity>(new ZComparator());
        private final DrawBatch batch = new DrawBatch();

        public RenderSystem(int priority){
            super(priority);
        }

        @Override
        public Class<? extends Component>[] componentsToHave() {
            Class[] classes = {DrawComponent.class};
            return classes;
        }

        @Override
        public Class<? extends Component>[] componentsNotToHave() {
            return new Class[0];
        }

        @Override
        public void addQualifiedEntity(Entity ent) {
            entities.add(ent);
        }

        @Override
        public void addQualifiedEntities(Set<Entity> ents) {
            for(Entity ent : ents){
                entities.add(ent);
            }
        }

        @Override
        public void entityRemovedFromEngine(Entity ent) {
            entities.remove(ent);
        }

        @Override
        public void addedToEngine(Engine engine) {
            engine.bindEntitySubscriber(this);
        }

        @Override
        public void update(int delta) {
            for(Entity ent : entities){
                DrawComponent draw = (DrawComponent) ent.getComponent(DrawComponent.class);
                draw.getImage().update(delta);
                batch.add(draw.getImage(), draw.getX(), draw.getY());
            }

            batch.renderBatch();
        }

        @Override
        public void removedFromEngine(Engine engine) {
            engine.unbindEntitySubscriber(this);
        }

        public static class ZComparator implements Comparator<Entity>{

            @Override
            public int compare(Entity o1, Entity o2) {
                return ((DrawComponent) o2.getComponent(DrawComponent.class)).getZ() - ((DrawComponent) o1.getComponent(DrawComponent.class)).getZ();
            }
        }
    }

    public class EntityQualifier {
        private EntitySet ents;

        private EntityQualifier(EntitySet entities){
            ents = entities;
        }

        public EntityQualifier all(Class<? extends Component>... components){
            if(components.length > 0) {
                EntitySet ents = Engine.this.onlyEntitiesWithComponent(components[0]);
                for (int i = 1; i < components.length; i++) {
                    ents = ents.onlyEntitiesWithComponent(components[i]);
                }
            }

            return this;
        }

        public EntityQualifier not(Class<? extends Component>... components){
            if(components.length > 0) {
                ents = Engine.this.onlyEntitiesWithoutComponent(components[0]);
                for (int i = 1; i < components.length; i++) {
                    ents = ents.onlyEntitiesWithoutComponent(components[i]);
                }
            }

            return this;
        }

        public Set<Entity> getSet(){
            return ents.toSet();
        }
    }

}
