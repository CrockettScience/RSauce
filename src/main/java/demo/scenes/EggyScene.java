package demo.scenes;

import sauce.asset.audio.AudioThread;
import sauce.asset.audio.Music;
import sauce.core.Preferences;
import demo.entities.Eggy;
import demo.systems.CollisionTest;
import sauce.core.engine.*;
import sauce.input.InputClient;
import sauce.input.InputEvent;
import sauce.input.InputServer;

import static demo.util.DemoUtil.HEIGHT;
import static demo.util.DemoUtil.WIDTH;
import static sauce.input.InputServer.*;

import static sauce.core.Preferences.ASSET_ROOT;
import static sauce.core.engine.SceneManager.*;

/**
 * Created by John Crockett.
 */
public class EggyScene extends Scene implements InputClient {

    private static final int VIEW_SPEED = 50;
    private static final int ZOOM_SPEED = 10;

    @Override
    protected void loadResources() {
        setCamera(new Camera(0, 0, WIDTH, HEIGHT, 0, 0), true);

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

        AudioThread.enqueue(new Music(Preferences.ASSET_ROOT + "Patriarchy.ogg", 21.391f));
    }

    @Override
    protected void destroyResources() {
        InputServer.unbind(this);
        Engine.getEngine().removeStepSystem(CollisionTest.class);
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
        v.resize((int)(v.getWidth() + ZOOM_SPEED * -y), (int)(v.getHeight() + (ZOOM_SPEED * Preferences.getCurrentScreenHeight() / Preferences.getCurrentScreenWidth()) * -y));
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
