package sauce.concurrent;


import sauce.core.Engine;
import sauce.core.Main;
import sauce.core.Scene;
import sauce.util.RSauceLogger;

/**
 *
 * @author Jonathan Crockett
 */
public abstract class ConcurrentScene extends Scene implements Runnable {
    protected abstract Scene getForegroundScene();

    protected void beforeBackgroundThreadStarts(){

    }

    protected void beforeForegroundSceneStarts(){

    }

    protected abstract void backgroundThread();
    
    @Override
    protected final void sceneMain() {
        (new Thread(this)).start();
        Engine.setScene(getForegroundScene());
    }
    
    protected final void loadResources(){
        beforeBackgroundThreadStarts();
    }
    
    protected final void destroyResources(){
        beforeForegroundSceneStarts();
    }
    
    @Override
    public final void run(){
        try {
            backgroundThread();
        }catch (Exception e){
            RSauceLogger.printErrorln(e);
            Main.quitAtEndOfCycle();
        }
    }
}
