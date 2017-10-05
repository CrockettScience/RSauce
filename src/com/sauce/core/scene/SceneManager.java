package com.sauce.core.scene;

import com.Project;
import com.sauce.util.ogl.OGLCoordinateSystem;
import com.util.RSauceLogger;
import com.util.structures.nonsaveable.Set;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.glMatrixMode;

/**
 *
 * @author Jonathan Crockett
 */
public class SceneManager{
        
    private static Scene scene;
    private static Camera camera = new Camera(0,0, Project.getScreenWidth(), Project.getScreenHeight(), 0, 0);
    private static Set<CameraChangeSubscriber> cameraChangeSubscribers = new Set<>();

    public static Scene getCurrentScene() {
        return scene;
    }
    
    public static Camera getCamera() {
        return camera;
    }

    public static void setScene(Scene aScene) {
        if(scene != null)
            scene.destroyResources();
        
        scene = aScene;
        scene.loadResources();
        scene.sceneMain();
    }
    
    public static void setCamera(Camera aCamera) {
        if(aCamera == null) {
            RSauceLogger.printWarningln("You cannot set camera to a null value.");
            return;
        }

        camera.dispose();
        camera = aCamera;

        for (CameraChangeSubscriber sub : cameraChangeSubscribers) {
            sub.cameraChanged(camera);
            camera.bindSubscriber(sub);
        }

        OGLCoordinateSystem.setCoordinateState(0, 0, camera.getWidth(), camera.getHeight());
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
