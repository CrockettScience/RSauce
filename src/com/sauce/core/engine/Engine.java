package com.sauce.core.engine;

import com.sauce.asset.graphics.DrawBatch;
import com.sauce.core.Main;
import com.sauce.core.Project;
import com.sauce.core.scene.BackgroundAttribute;
import com.sauce.core.scene.Camera;
import com.sauce.core.scene.Scene;
import com.sauce.core.scene.SceneManager;
import com.util.structures.nonsaveable.ArrayList;
import com.util.structures.nonsaveable.Set;
import com.util.structures.special.PriorityMap;
import com.util.structures.special.SortedArrayList;

import java.util.Comparator;
import java.util.Iterator;

import static com.sauce.util.io.GraphicsUtil.applyIOImageForDrawing;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;


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

    private int timeSinceLastUpdate;

    public void update(int delta){

        step(delta);

        if (timeSinceLastUpdate >= fpms) {

            SceneManager.getCamera().getCameraBuffer().bind();

            preDraw(delta);
            draw(delta);
            postDraw(delta);

            SceneManager.getCamera().getCameraBuffer().unbind();

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

        if(scene.containsAttribute(BackgroundAttribute.class)) {
            BackgroundAttribute bg = scene.getAttribute(BackgroundAttribute.class);

            Iterator<ParallaxBackground> i = bg.backgroundIterator();

            while (i.hasNext()) {
                ParallaxBackground back = i.next();
                if (back != null) {
                    back.update(delta);

                    glBindTexture(GL_TEXTURE_2D, back.texID());

                    applyIOImageForDrawing(back.getParallaxIOImage(), back.absWidth(), back.absHeight(), back.getParallaxComponents());

                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

                    glEnable(GL_TEXTURE_2D);

                    glPushMatrix();

                    float[] texelCoord = back.regionCoordinates();
                    Camera cam = SceneManager.getCamera();

                    glBegin(GL_QUADS);
                    {
                        glTexCoord2f(texelCoord[0], texelCoord[1]);
                        glVertex2f(cam.getX(), cam.getY());

                        glTexCoord2f(texelCoord[2], texelCoord[3]);
                        glVertex2f(cam.getX() + cam.getWidth(), cam.getY());

                        glTexCoord2f(texelCoord[4], texelCoord[5]);
                        glVertex2f(cam.getX() + cam.getWidth(), cam.getY() + cam.getHeight());

                        glTexCoord2f(texelCoord[6], texelCoord[7]);
                        glVertex2f(cam.getX(), cam.getY() + cam.getHeight());
                    }
                    glEnd();

                    glPopMatrix();
                }
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

            Iterator<ParallaxBackground> i = bg.foregroundIterator();

            while (i.hasNext()) {
                ParallaxBackground fore = i.next();
                if (fore != null) {
                    fore.update(delta);

                    glBindTexture(GL_TEXTURE_2D, fore.texID());

                    applyIOImageForDrawing(fore.getParallaxIOImage(), fore.absWidth(), fore.absHeight(), fore.getParallaxComponents());

                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

                    glEnable(GL_TEXTURE_2D);

                    glPushMatrix();

                    float[] texelCoord = fore.regionCoordinates();
                    Camera cam = SceneManager.getCamera();

                    glBegin(GL_QUADS);
                    {
                        glTexCoord2f(texelCoord[0], texelCoord[1]);
                        glVertex2f(cam.getX(), cam.getY());

                        glTexCoord2f(texelCoord[2], texelCoord[3]);
                        glVertex2f(cam.getX() + cam.getWidth(), cam.getY());

                        glTexCoord2f(texelCoord[4], texelCoord[5]);
                        glVertex2f(cam.getX() + cam.getWidth(), cam.getY() + cam.getHeight());

                        glTexCoord2f(texelCoord[6], texelCoord[7]);
                        glVertex2f(cam.getX(), cam.getY() + cam.getHeight());
                    }
                    glEnd();

                    glPopMatrix();
                }
            }
        }

        render.batch.renderBatch();
    }

    private void swapBuffer(){

        glBindTexture(GL_TEXTURE_2D, SceneManager.getCamera().getCameraBuffer().texID());

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        glEnable(GL_TEXTURE_2D);

        glPushMatrix();

        Camera cam = SceneManager.getCamera();

        glBegin(GL_QUADS);
        {
            glTexCoord2f(0, 0);
            glVertex2f(cam.getX(), cam.getY());

            glTexCoord2f(1, 0);
            glVertex2f(cam.getX() + cam.getWidth(), cam.getY());

            glTexCoord2f(1, 1);
            glVertex2f(cam.getX() + cam.getWidth(), cam.getY() + cam.getHeight());

            glTexCoord2f(0, 1);
            glVertex2f(cam.getX(), cam.getY() + cam.getHeight());
        }
        glEnd();

        glPopMatrix();

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
