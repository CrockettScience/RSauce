package com.demo.scenes;

import com.demo.entities.Eggy;
import com.sauce.asset.graphics.TiledTexture;
import com.sauce.core.Main;
import com.sauce.core.engine.Engine;
import com.sauce.core.engine.Entity;
import com.sauce.core.scene.BackgroundAttribute;
import com.sauce.core.scene.Scene;
import com.sauce.input.InputClient;
import com.sauce.input.InputEvent;
import com.sauce.input.InputServer;

import static com.sauce.core.Project.ASSET_ROOT;
import static com.sauce.core.Project.FRAME_LIMIT;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by John Crockett.
 */
public class DemoScene extends Scene implements InputClient {

    @Override
    protected void loadResources() {
        // Setup Eggy
        Entity eggy = new Eggy();

        Engine engine = Engine.getEngine(FRAME_LIMIT);

        putEntity("eggy", eggy);

        // Setup Background
        BackgroundAttribute attr = new BackgroundAttribute();
        addAttribute(attr);

        attr.background_0 = new TiledTexture(ASSET_ROOT + "bg.png");
        attr.background_0.setXScale(8f);
        attr.background_0.setYScale(8f);

        // Bind to recieve InputEvents
        InputServer.bind(this);
    }

    @Override
    protected void destroyResources() {
        removeEntities();
        getAttribute(BackgroundAttribute.class).background_0.dispose();
    }

    @Override
    protected void sceneMain() {
        activateEntity("eggy");
    }

    @Override
    public void receivedKeyEvent(InputEvent event) {
        if(event.key() == GLFW_KEY_ESCAPE){
            if(event.action() == GLFW_RELEASE)
                Main.quitAtEndOfCycle();
        }
    }

    @Override
    public void receivedTextEvent(char character) {

    }

    @Override
    public void receivedMouseButtonEvent(InputEvent event) {

    }

    @Override
    public void mouseScrolled(double x, double y) {

    }

    @Override
    public void cursorPosChanged(double x, double y) {

    }

    @Override
    public void joystickConnected(int joyID) {

    }

    @Override
    public void joystickDisconnected(int joyID) {

    }
}
