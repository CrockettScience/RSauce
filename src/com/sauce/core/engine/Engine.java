package com.sauce.core.engine;

import com.sauce.asset.graphics.DrawBatch;
import com.sauce.asset.graphics.Surface;
import com.sauce.core.Main;
import com.sauce.core.Project;
import com.sauce.core.scene.BackgroundAttribute;
import com.sauce.core.scene.Scene;
import com.sauce.core.scene.SceneManager;
import com.util.structures.nonsaveable.ArrayList;
import com.util.structures.nonsaveable.Set;
import com.util.structures.special.PriorityMap;
import com.util.structures.special.SortedArrayList;

import java.util.Comparator;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;


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

    private Engine() {
        fpms = 1000 / Project.FRAME_LIMIT;
        render.addedToEngine(this);
    }

    public static Engine getEngine(){
        if(singletonEngine == null)
            singletonEngine = new Engine();

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

    public boolean containsStepSystem(Class<? extends StepSystem> sys){
        return steps.containsKey(sys);
    }

    public boolean containsDrawSystem(Class<? extends DrawSystem> sys){
        return draws.containsKey(sys);
    }

    public boolean containsEntity(Entity ent){
        return entities.contains(ent);
    }

    public <T extends StepSystem> T removeStepSystem(Class<T> sys){
        StepSystem ret = steps.get(sys);
        steps.remove(sys);
        ret.removedFromEngine(this);
        return (T) ret;
    }

    public <T extends DrawSystem> T removeDrawSystem(Class<T> sys){
        DrawSystem ret = draws.get(sys);
        draws.remove(sys);
        ret.removedFromEngine(this);
        return (T) ret;
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

    private Surface backBuffer = new Surface(SceneManager.getView().getWidth(), SceneManager.getView().getHeight());
    private int timeSinceLastUpdate;

    public void update(int delta){

        step(delta);

        if (timeSinceLastUpdate >= fpms) {

            backBuffer.bind();
            preDraw(delta);
            draw(delta);
            postDraw(delta);
            backBuffer.unbind();

            swapBuffer();

            timeSinceLastUpdate -= fpms;
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
        Scene scene = SceneManager.getCurrentScene();

        if(scene.containsAttribute(BackgroundAttribute.class)){
            BackgroundAttribute bg = scene.getAttribute(BackgroundAttribute.class);

            if(bg.background_0 != null){
                bg.background_0.update(delta);
                render.batch.add(bg.background_0, 0, 0);
            }

            if(bg.background_1 != null){
                bg.background_1.update(delta);
                render.batch.add(bg.background_1, 0, 0);
            }

            if(bg.background_2 != null){
                bg.background_2.update(delta);
                render.batch.add(bg.background_2, 0, 0);
            }

            if(bg.background_3 != null){
                bg.background_3.update(delta);
                render.batch.add(bg.background_3, 0, 0);
            }

            if(bg.background_4 != null){
                bg.background_4.update(delta);
                render.batch.add(bg.background_4, 0, 0);
            }

            if(bg.background_5 != null){
                bg.background_5.update(delta);
                render.batch.add(bg.background_5, 0, 0);
            }

            if(bg.background_6 != null){
                bg.background_6.update(delta);
                render.batch.add(bg.background_6, 0, 0);
            }

            if(bg.background_7 != null){
                bg.background_7.update(delta);
                render.batch.add(bg.background_7, 0, 0);
            }

            if(bg.background_8 != null){
                bg.background_8.update(delta);
                render.batch.add(bg.background_8, 0, 0);
            }

            if(bg.background_9 != null){
                bg.background_9.update(delta);
                render.batch.add(bg.background_9, 0, 0);
            }
        }

        render.batch.renderBatch();
    }

    private void draw(int delta){
        render.update(delta);

        for (int i = 0; i < draws.size(); i++) {
            draws.next().update(delta);
        }
    }

    private void postDraw(int delta){
        Scene scene = SceneManager.getCurrentScene();

        if(scene.containsAttribute(BackgroundAttribute.class)) {
            BackgroundAttribute bg = scene.getAttribute(BackgroundAttribute.class);

            if (bg.foreground_0 != null) {
                bg.foreground_0.update(delta);
                render.batch.add(bg.foreground_0, 0, 0);
            }

            if (bg.foreground_1 != null) {
                bg.foreground_1.update(delta);
                render.batch.add(bg.foreground_1, 0, 0);
            }

            if (bg.foreground_2 != null) {
                bg.foreground_2.update(delta);
                render.batch.add(bg.foreground_2, 0, 0);
            }

            if (bg.foreground_3 != null) {
                bg.foreground_3.update(delta);
                render.batch.add(bg.foreground_3, 0, 0);
            }

            if (bg.foreground_4 != null) {
                bg.foreground_4.update(delta);
                render.batch.add(bg.foreground_4, 0, 0);
            }

            if (bg.foreground_5 != null) {
                bg.foreground_5.update(delta);
                render.batch.add(bg.foreground_5, 0, 0);
            }

            if (bg.foreground_6 != null) {
                bg.foreground_6.update(delta);
                render.batch.add(bg.foreground_6, 0, 0);
            }

            if (bg.foreground_7 != null) {
                bg.foreground_7.update(delta);
                render.batch.add(bg.foreground_7, 0, 0);
            }

            if (bg.foreground_8 != null) {
                bg.foreground_8.update(delta);
                render.batch.add(bg.foreground_8, 0, 0);
            }

            if (bg.foreground_9 != null) {
                bg.foreground_9.update(delta);
                render.batch.add(bg.foreground_9, 0, 0);
            }
        }

        render.batch.renderBatch();
    }

    private void swapBuffer(){
        render.batch.add(backBuffer, 0, 0);
        render.batch.renderBatch();

        glfwSwapBuffers(Main.LOOP.window);
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
                DrawComponent draw = ent.getComponent(DrawComponent.class);
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
