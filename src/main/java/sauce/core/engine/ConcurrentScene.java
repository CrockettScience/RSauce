package sauce.core.engine;


/**
 *
 * @author Jonathan Crockett
 */
public abstract class ConcurrentScene extends Scene implements Runnable {
    protected abstract Scene getForegroundScene();
    protected abstract void beforeBackgroundThreadStarts();
    protected abstract void beforeForgroundSceneStarts();
    protected abstract void backgroundThread();
    
    @Override
    protected final void sceneMain() {
        (new Thread(this)).start();
        Engine.getEngine().setScene(getForegroundScene());
    }
    
    protected final void loadResources(){
        beforeBackgroundThreadStarts();
    }
    
    protected final void destroyResources(){
        beforeForgroundSceneStarts();
    }
    
    @Override
    public final void run(){
        backgroundThread();
    }
}
