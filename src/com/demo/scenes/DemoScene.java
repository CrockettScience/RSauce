package com.demo.scenes;

import com.demo.entities.Eggy;
import com.sauce.asset.audio.AudioThread;
import com.sauce.asset.audio.Music;
import com.sauce.core.Main;
import com.sauce.core.Project;
import com.sauce.core.engine.Entity;
import com.sauce.core.engine.ParallaxBackground;
import com.sauce.core.scene.*;
import com.sauce.input.InputClient;
import com.sauce.input.InputEvent;

import java.util.Iterator;

import static com.sauce.input.InputServer.*;

import static com.sauce.core.Project.ASSET_ROOT;
import static com.sauce.core.scene.SceneManager.*;

/**
 * Created by John Crockett.
 */
public class DemoScene extends Scene implements InputClient {

    private static final int VIEW_SPEED = 50;
    private static final int ZOOM_SPEED = 100;

    @Override
    protected void loadResources() {
        // Setup Eggy
        Entity eggy1 = new Eggy(16, 16, SceneManager.getCamera().getWidth() / 2, SceneManager.getCamera().getHeight() / 2, 0);

        putEntity("eggy1", eggy1);

        // Setup Background
        BackgroundAttribute attr = new BackgroundAttribute();
        addAttribute(attr);

        ParallaxBackground triangles = new ParallaxBackground(ASSET_ROOT + "bg.png", 0, 0);

        attr.setBackground(triangles, 0);

        // Bind to recieve InputEvents
        bind(this);
    }

    @Override
    protected void destroyResources() {
        removeEntities();
        Iterator<ParallaxBackground> i = getAttribute(BackgroundAttribute.class).backgroundIterator();

        while(i.hasNext()){
            i.next().dispose();
        }
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
        v.resize((int)(v.getWidth() + ZOOM_SPEED * -y), (int)(v.getHeight() + (ZOOM_SPEED * Project.SCREEN_HEIGHT / Project.SCREEN_WIDTH) * -y));
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
