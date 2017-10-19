package com.sauce.core.scene;

import com.sauce.asset.audio.AudioThread;
import com.sauce.core.Preferences;
import com.sauce.util.ogl.OGLCoordinateSystem;
import com.util.RSauceLogger;
import com.util.structures.nonsaveable.Set;

/**
 *
 * @author Jonathan Crockett
 */
public class SceneManager{
        
    private static Scene scene;
    private static Camera camera = new Camera(0,0, Preferences.getWindowScreenWidth(), Preferences.getWindowScreenHeight(), 0, 0);
    private static Set<CameraChangeSubscriber> cameraChangeSubscribers = new Set<>();

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

        AudioThread.clear();
        AudioThread.clearAudioCache();

        setCamera(new Camera(0, 0, Preferences.getWindowScreenWidth(), Preferences.getWindowScreenHeight(), 0, 0), true);
        
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
}
