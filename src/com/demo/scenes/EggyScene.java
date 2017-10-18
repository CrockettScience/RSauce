package com.demo.scenes;

import com.sauce.core.Preferences;
import com.demo.entities.Eggy;
import com.demo.systems.CollisionTest;
import com.sauce.core.Main;
import com.sauce.core.engine.Engine;
import com.sauce.core.engine.Entity;
import com.sauce.core.engine.ParallaxBackground;
import com.sauce.core.scene.*;
import com.sauce.input.InputClient;
import com.sauce.input.InputEvent;
import com.sauce.input.InputServer;

import java.util.Iterator;

import static com.sauce.input.InputServer.*;

import static com.sauce.core.Preferences.ASSET_ROOT;
import static com.sauce.core.scene.SceneManager.*;
import static com.demo.util.DemoUtil.*;

/**
 * Created by John Crockett.
 */
public class EggyScene extends Scene implements InputClient {

    private static final int VIEW_SPEED = 50;
    private static final int ZOOM_SPEED = 100;

    @Override
    protected void loadResources() {
        SceneManager.setCamera(new Camera(0, 0, WIDTH, HEIGHT, 0, 0));

        // Setup Eggy
        Entity eggy1 = new Eggy(1, 1, SceneManager.getCamera().getWidth() / 2, SceneManager.getCamera().getHeight() / 2, 0);

        putEntity("eggy", eggy1);

        // Setup Background
        BackgroundAttribute attr = new BackgroundAttribute();
        addAttribute(attr);

        ParallaxBackground grass = new ParallaxBackground(ASSET_ROOT + "grass.png", 0, 0);
        attr.setBackground(grass, 0);

        // Bind to recieve InputEvents
        bind(this);


    }

    @Override
    protected void destroyResources() {
        InputServer.unbind(this);
    }

    @Override
    protected void sceneMain() {
        activateEntity("eggy");
    }

    @Override
    public void receivedKeyEvent(InputEvent event) {
        if(event.key() == KEY_ESCAPE && event.action() == ACTION_RELEASED){
            SceneManager.setScene(new Demo());
        }

        if(event.key() == KEY_UP || event.key() == KEY_DOWN || event.key() == KEY_LEFT || event.key() == KEY_RIGHT ) {
            if (event.key() == KEY_UP) {
                getCamera().move(getCamera().getX(), getCamera().getY() + VIEW_SPEED);
            }

            if (event.key() == KEY_DOWN) {
                getCamera().move(getCamera().getX(),getCamera().getY() - VIEW_SPEED);
            }

            if (event.key() == KEY_LEFT) {
                getCamera().move(getCamera().getX() - VIEW_SPEED, getCamera().getY());
            }

            if (event.key() == KEY_RIGHT) {
                getCamera().move(getCamera().getX() + VIEW_SPEED, getCamera().getY());
            }
        }

        if(event.key() == KEY_ENTER)
            Engine.getEngine().add(new CollisionTest(0, (Eggy) getEntity("eggy")));

        if(event.key() == KEY_F && event.action() == ACTION_RELEASED){
            if(Preferences.isFullscreen())
                Preferences.setFullscreen(false);
            else
                Preferences.setFullscreen(true);
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
        Camera v = SceneManager.getCamera();
        v.resize((int)(v.getWidth() + ZOOM_SPEED * -y), (int)(v.getHeight() + (ZOOM_SPEED * Preferences.getScreenHeight() / Preferences.getScreenWidth()) * -y));
    }

    @Override
    public void cursorPosChanged(double x, double y) {

    }

    @Override
    public void joystickConnected(int joyID) {
        System.out.println("Joystick " + joyID + " was connected!");
    }

    @Override
    public void joystickDisconnected(int joyID) {
        System.out.println("Joystick " + joyID + " was disconnected!");

    }
}
