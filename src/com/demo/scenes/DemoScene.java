package com.demo.scenes;

import com.demo.entities.Eggy;
import com.sauce.core.Main;
import com.sauce.core.engine.Engine;
import com.sauce.core.engine.Entity;
import com.sauce.core.scene.*;
import com.sauce.input.InputClient;
import com.sauce.input.InputEvent;
import static com.sauce.input.InputServer.*;

import static com.sauce.core.Project.ASSET_ROOT;
import static com.sauce.core.scene.SceneManager.*;

/**
 * Created by John Crockett.
 */
public class DemoScene extends Scene implements InputClient {

    private static final int VIEW_SPEED = 20;
    private static final int ZOOM_SPEED = 5;

    @Override
    protected void loadResources() {
        // Setup Eggy
        Entity eggy1 = new Eggy(4, 4, SceneManager.getView().getWidth() / 2, SceneManager.getView().getHeight() / 2, 0);

        putEntity("eggy1", eggy1);

        // Setup Background
        BackgroundAttribute attr = new BackgroundAttribute();
        addAttribute(attr);

        attr.background_0 = new ParallaxBackground(ASSET_ROOT + "bg.png", 1, 0);
        attr.background_0.setXScale(1f);
        attr.background_0.setYScale(1f);

        // Bind to recieve InputEvents
        bind(this);
    }

    @Override
    protected void destroyResources() {
        removeEntities();
        getAttribute(BackgroundAttribute.class).background_0.dispose();
    }

    @Override
    protected void sceneMain() {
        activateEntity("eggy1");
    }

    @Override
    public void receivedKeyEvent(InputEvent event) {
        if(event.key() == KEY_ESCAPE && event.action() == ACTION_RELEASED){
            Main.quitAtEndOfCycle();
        }

        if(event.key() == KEY_UP || event.key() == KEY_DOWN || event.key() == KEY_LEFT || event.key() == KEY_RIGHT ) {
            if (event.key() == KEY_UP) {
                getView().setY(getView().getY() - VIEW_SPEED);
            }

            if (event.key() == KEY_DOWN) {
                getView().setY(getView().getY() + VIEW_SPEED);
            }

            if (event.key() == KEY_LEFT) {
                getView().setX(getView().getX() - VIEW_SPEED);
            }

            if (event.key() == KEY_RIGHT) {
                getView().setX(getView().getX() + VIEW_SPEED);
            }
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
        View v = SceneManager.getView();
        v.resize((int)(v.getWidth() + ZOOM_SPEED * y), (int)(v.getHeight() + ZOOM_SPEED * y));
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
