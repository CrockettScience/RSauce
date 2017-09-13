package com.sauce.core.scene;

/**
 *
 * @author Jonathan Crockett
 */
public class SceneManager{
        
    private static Scene scene;
    private static View view;

    public static Scene getCurrentScene() {
        return scene;
    }
    
    public static View getView() {
        return view;
    }

    public static void setScene(Scene aScene) {
        if(scene != null)
            scene.destroyResources();
        
        scene = aScene;
        scene.loadResources();
        scene.sceneMain();
    }
    
    public static void setView(View aView) {
        view = aView;
    }
}
