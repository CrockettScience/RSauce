package sauce.concurrent;

import sauce.core.Scene;

public abstract class LoadingScene extends ConcurrentScene {
    private int totalPoints;
    private int progress;
    private LoadingScreen informer;

    public LoadingScene(int pointsToCompletion){
        totalPoints = pointsToCompletion;
        progress = 0;
        informer = getLoadingScreen();
    }

    protected final void setProgress(int newProgress){
        progress = newProgress;
        informer.loadingSceneProgress(progress, totalPoints);
    }

    protected final void incrementProgress(){
        progress++;
        informer.loadingSceneProgress(progress, totalPoints);
    }

    protected abstract LoadingScreen getLoadingScreen();

    @Override
    protected final Scene getForegroundScene() {
        return getLoadingScreen();
    }

    public abstract class LoadingScreen extends Scene{
        protected void loadingSceneProgress(int pointsCompleted, int totalPoints){

        }
    }
}
