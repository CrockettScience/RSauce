package sauce.core.engine;

import demo.scenes.Demo;
import sauce.asset.audio.AudioManager;
import sauce.asset.scripts.Argument;
import sauce.asset.scripts.Return;
import sauce.asset.scripts.Script;
import sauce.core.attributes.BackgroundAttribute;
import util.RSauceLogger;
import util.structures.nonsaveable.ArrayList;
import util.structures.nonsaveable.Set;
import util.structures.special.PriorityMap;
import util.structures.special.SortedArrayList;
import util.structures.threadsafe.ThreadSafeQueue;

import java.util.Comparator;
import java.util.Iterator;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;


/**
 * Created by John Crockett.
 */
public final class Engine {

    private static final Scene OPENING_SCENE = new Demo();

    private static EntitySet entities = new EntitySet();
    private static ArrayList<EntitySubscriber> subs = new ArrayList<>();
    private static PriorityMap<Class<? extends StepSystem>, StepSystem> steps = new PriorityMap<>();
    private static PriorityMap<Class<? extends DrawSystem>, DrawSystem> draws = new PriorityMap<>();
    private static PriorityMap<Class<? extends GUISystem>, GUISystem> gui = new PriorityMap<>();
    private static Surface GUILayer = new Surface(Preferences.getCurrentScreenWidth(), Preferences.getCurrentScreenHeight());
    private static RenderSystem render = new RenderSystem(0);

    private static ThreadSafeQueue<ScriptEntry<? extends Argument>> scriptQueue = new ThreadSafeQueue<>();

    private static class ScriptEntry<A extends Argument>{

        private ScriptEntry(Script<A, ?> script, A argument){
            scr = script;
            arg = argument;
        }

        private Script<A, ?> scr;
        private A arg;
    }

    private static Scene scene;
    private static Camera camera = new Camera(0,0, Preferences.getCurrentScreenWidth(), Preferences.getCurrentScreenHeight(), 0, 0);
    private static Set<CameraChangeSubscriber> cameraChangeSubscribers = new Set<>();

    static{
        setScene(OPENING_SCENE);
        render.addedToEngine();
    }

    public static Scene getCurrentScene() {
        return scene;
    }

    public static Camera getCamera() {
        return camera;
    }

    public static void setScene(Scene aScene) {
        if(scene != null) {
            scene.dispose();
        }

        AudioManager.clear();
        AudioManager.clearAudioCache();

        setCamera(new Camera(0, 0, Preferences.getCurrentScreenWidth(), Preferences.getCurrentScreenHeight(), 0, 0), true);

        scene = aScene;
        scene.loadResources();
        scene.sceneMain();
    }

    public static void setCamera(Camera aCamera, boolean disposeCurrent) {
        if(aCamera == null) {
            RSauceLogger.printWarningln("You cannot set camera to a null value.");
            return;
        }

        if(disposeCurrent)
            camera.dispose();

        camera.deactivate();
        camera = aCamera;

        for (CameraChangeSubscriber sub : cameraChangeSubscribers) {
            sub.cameraChanged(camera);
            camera.bindSubscriber(sub);
        }

        camera.activate();
    }

    public static void subscribeToCameraChanges(CameraChangeSubscriber sub){
        cameraChangeSubscribers.add(sub);
        camera.bindSubscriber(sub);
    }

    public static void unsubscribeToCameraChanges(CameraChangeSubscriber sub){
        cameraChangeSubscribers.remove(sub);
        camera.removeSubscriber(sub);
    }

    public static void bindEntitySubscriber(EntitySubscriber sub){
        subs.add(sub);
        sub.addQualifiedEntities(getEntityQualifier().all(sub.componentsToHave()).not(sub.componentsNotToHave()).getSet());
    }

    public static void unbindEntitySubscriber(EntitySubscriber sub){
        subs.remove(sub);
    }

    public static boolean add(StepSystem sys){
        if(steps.containsKey(sys.getClass())){
            RSauceLogger.printWarningln("Can't add duplicate StepSystem " + sys.getClass().getName());
            return false;
        }

        steps.put(sys.getClass(), sys, sys.prio);
        sys.addedToEngine();
        return true;
    }

    public static boolean add(DrawSystem sys){
        if(draws.containsKey(sys.getClass())){
            RSauceLogger.printWarningln("Can't add duplicate DrawSystem " + sys.getClass().getName());
            return false;
        }
        draws.put(sys.getClass(), sys, sys.prio);
        sys.addedToEngine();
        return true;
    }

    public static void add(Entity ent){
        entities.add(ent);
        ent.addedToEngine();

        for(EntitySubscriber sub : subs){
            if(ent.hasAll(sub.componentsToHave()) && ent.hasNone(sub.componentsNotToHave()))
                sub.addQualifiedEntity(ent);
        }
    }

    public static <A extends Argument> void engueueScript(Script<A, Return> script, A arg){
        scriptQueue.enqueue(new ScriptEntry<>(script, arg));
    }

    public static boolean contains(Class<? extends EngineSystem> sys){
        if (GUISystem.class.isAssignableFrom(sys))
            return gui.containsKey((Class<? extends GUISystem>) sys);


        else if (StepSystem.class.isAssignableFrom(sys))
            return steps.containsKey((Class<? extends StepSystem>) sys);


        else
            return draws.containsKey((Class<? extends DrawSystem>) sys);

    }

    public static boolean contains(Entity ent){
        return entities.contains(ent);
    }

    public static <T extends EngineSystem> T remove(Class<T> sys) {
        if (GUISystem.class.isAssignableFrom(sys)) {
            GUISystem ret = gui.get((Class<? extends GUISystem>) sys);
            if (ret == null) {
                RSauceLogger.printWarningln("EngineSystem " + sys.getSimpleName() + " was not found in engine");
                return null;
            }
            gui.remove((Class<? extends GUISystem>) sys);
            ret.removedFromEngine();
            return (T) ret;
        }

        else if (StepSystem.class.isAssignableFrom(sys)) {
            StepSystem ret = steps.get((Class<? extends StepSystem>) sys);
            if (ret == null) {
                RSauceLogger.printWarningln("EngineSystem " + sys.getSimpleName() + " was not found in engine");
                return null;
            }
            steps.remove((Class<? extends StepSystem>) sys);
            ret.removedFromEngine();
            return (T) ret;
        }

        else {
            DrawSystem ret = draws.get((Class<? extends DrawSystem>) sys);
            if (ret == null) {
                RSauceLogger.printWarningln("EngineSystem " + sys.getSimpleName() + " was not found in engine");
                return null;
            }
            draws.remove((Class<? extends DrawSystem>) sys);
            ret.removedFromEngine();
            return (T) ret;
        }
    }

    public static void remove(Entity ent){
        if(!entities.contains(ent)){
            RSauceLogger.printWarningln("Entity was not found in engine");
            return;
        }

        entities.remove(ent);
        ent.removedFromEngine();
        for(EntitySubscriber sub : subs){
            if(sub.containsEntity(ent)){
                sub.entityRemovedFromEngine(ent);
            }
        }
    }

    public static void clearSystems(){
        steps.clear();
        draws.clear();
    }

    public static void clearEntities(){
        for(Entity ent : entities){
            for(EntitySubscriber sub : subs){
                if(sub.containsEntity(ent)){
                    sub.entityRemovedFromEngine(ent);
                }
            }
            ent.removedFromEngine();
            ent.dispose();
        }
    }

    private static double timeSinceLast;

    static void update(double delta){
        timeSinceLast += delta;
        if(timeSinceLast >= 1.0 / Preferences.getFullscreenRefreshRate()) {

            step(timeSinceLast);

            glClear(GL_COLOR_BUFFER_BIT); // clear the framebuffer

            preDraw(timeSinceLast);
            draw(timeSinceLast);
            postDraw(timeSinceLast);

            drawGUI(timeSinceLast);

            glfwSwapBuffers(Main.getWindowHandle());
            timeSinceLast = 0;
        }
    }

    static void step(double delta){

        while(!scriptQueue.isEmpty()){
            ScriptEntry entry = scriptQueue.dequeue();
            entry.scr.execute(entry.arg);
        }

        for (int i = 0; i < steps.size(); i++) {
            steps.next().update(delta);
        }
    }

    static void preDraw(double delta){
        if(getCurrentScene().containsAttribute(BackgroundAttribute.class)){
            BackgroundAttribute backAttr = getCurrentScene().getAttribute(BackgroundAttribute.class);
             Iterator<Background> i = backAttr.backgroundIterator();
                Background back;

             while(i.hasNext()){
                 if((back = i.next()) != null) {
                     back.update(delta);
                     back.render();
                 }
             }
        }
    }

    static void draw(double delta){
        render.update(delta);

        for (int i = 0; i < draws.size(); i++) {
            draws.next().update(delta);
        }
    }

    static void postDraw(double delta){
        if(getCurrentScene().containsAttribute(BackgroundAttribute.class)){
            BackgroundAttribute backAttr = getCurrentScene().getAttribute(BackgroundAttribute.class);
            Iterator<Background> i = backAttr.foregroundIterator();
            Background fore;

            while(i.hasNext()) {
                if ((fore = i.next()) != null) {
                    fore.update(delta);
                    fore.render();
                }
            }
        }
    }

    static void drawGUI(double delta){
        if(Preferences.getCurrentScreenWidth() != GUILayer.getWidth() || Preferences.getCurrentScreenHeight() != GUILayer.getHeight()){
            GUILayer.dispose();
            GUILayer = new Surface(Preferences.getCurrentScreenWidth(), Preferences.getCurrentScreenHeight());
        }

        GUILayer.bind();
        {
            for (int i = 0; i < gui.size(); i++) {
                gui.next().update(delta);
            }
        }
        GUILayer.unbind();
    }

    private static EntitySet onlyEntitiesWithComponent(Class<? extends Component> c){
        return entities.onlyEntitiesWithComponent(c);
    }

    private static EntitySet onlyEntitiesWithoutComponent(Class<? extends Component> c){
        return entities.onlyEntitiesWithoutComponent(c);
    }

    public static EntityQualifier getEntityQualifier(){
        return new EntityQualifier(entities);
    }

    private static class RenderSystem extends EngineSystem implements EntitySubscriber{
        private SortedArrayList<Entity> entities = new SortedArrayList<>(new ZComparator());
        private final SpriteBatch batch = new SpriteBatch();

        public RenderSystem(int priority){
            super(priority);
        }

        @Override
        public Class<? extends Component>[] componentsToHave() {
            Class[] classes = {SpriteComponent.class};
            return classes;
        }

        @Override
        public Class<? extends Component>[] componentsNotToHave() {
            return new Class[0];
        }

        @Override
        public boolean containsEntity(Entity e) {
            return entities.contains(e);
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
        public void entityNoLongerQualifies(Entity ent) {
            entities.remove(ent);
        }

        @Override
        public void addedToEngine() {
            Engine.bindEntitySubscriber(this);
        }

        @Override
        public void update(double delta) {
            for(Entity ent : entities){
                SpriteComponent draw = ent.getComponent(SpriteComponent.class);
                if(draw.getSprite() != null)
                    draw.getSprite().update(delta);
                batch.add(draw.getSprite(), draw.getX(), draw.getY(), draw.getScript());
            }

            batch.render();
        }

        @Override
        public void removedFromEngine() {
            Engine.unbindEntitySubscriber(this);
        }

        public void entityChangedZ(Entity ent){
            entities.remove(ent);
            entities.add(ent);
        }

        public static class ZComparator implements Comparator<Entity>{

            @Override
            public int compare(Entity o1, Entity o2) {
                return ((SpriteComponent) o2.getComponent(SpriteComponent.class)).getZ() - ((SpriteComponent) o1.getComponent(SpriteComponent.class)).getZ();
            }
        }
    }

    public static class EntityQualifier {
        private EntitySet ents;

        private EntityQualifier(EntitySet entities){
            ents = entities;
        }

        public EntityQualifier all(Class<? extends Component>... components){
            if(components.length > 0) {
                ents = Engine.onlyEntitiesWithComponent(components[0]);
                for (int i = 1; i < components.length; i++) {
                    ents = ents.onlyEntitiesWithComponent(components[i]);
                }
            }

            return this;
        }

        public EntityQualifier not(Class<? extends Component>... components){
            if(components.length > 0) {
                ents = Engine.onlyEntitiesWithoutComponent(components[0]);
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

    static void entityChangedComponents(Entity ent){
        for(EntitySubscriber sub : subs){
            if(sub.containsEntity(ent)){
                if( !(ent.hasAll(sub.componentsToHave()) && ent.hasNone(sub.componentsNotToHave())) )
                    sub.entityNoLongerQualifies(ent);
            }else{
                if(ent.hasAll(sub.componentsToHave()) && ent.hasNone(sub.componentsNotToHave()))
                    sub.addQualifiedEntity(ent);
            }
        }
    }

    static void entityChangedZ(Entity ent){
        if(render.containsEntity(ent))
            render.entityChangedZ(ent);
    }

}
