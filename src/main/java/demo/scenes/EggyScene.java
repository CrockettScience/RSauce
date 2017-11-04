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

/**
 * Created by John Crockett.
 */
public class EggyScene extends Scene implements InputClient {

    private static final int VIEW_SPEED = 50;
    private static final int ZOOM_SPEED = 10;

    @Override
    protected void loadResources() {
        Engine e = Engine.getEngine();

        e.setCamera(new Camera(0, 0, WIDTH, HEIGHT, 0, 0), true);

        // Setup Eggy
        Entity eggy1 = new Eggy(1, 1, e.getCamera().getWidth() / 2, e.getCamera().getHeight() / 2, 0);

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
        Engine e = Engine.getEngine();
        if(event.key() == KEY_ESCAPE && event.action() == ACTION_RELEASED){
            e.setScene(new Demo());
        }

        if(event.key() == KEY_UP || event.key() == KEY_DOWN || event.key() == KEY_LEFT || event.key() == KEY_RIGHT ) {
            if (event.key() == KEY_UP) {
                e.getCamera().move(e.getCamera().getX(), e.getCamera().getY() + VIEW_SPEED);
            }

            if (event.key() == KEY_DOWN) {
                e.getCamera().move(e.getCamera().getX(),e.getCamera().getY() - VIEW_SPEED);
            }

            if (event.key() == KEY_LEFT) {
                e.getCamera().move(e.getCamera().getX() - VIEW_SPEED, e.getCamera().getY());
            }

            if (event.key() == KEY_RIGHT) {
                e.getCamera().move(e.getCamera().getX() + VIEW_SPEED, e.getCamera().getY());
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
    public void mouseScrolled(double x, double y) {
        Camera v = Engine.getEngine().getCamera();
        v.resize((int)(v.getWidth() + ZOOM_SPEED * -y), (int)(v.getHeight() + (ZOOM_SPEED * Preferences.getCurrentScreenHeight() / Preferences.getCurrentScreenWidth()) * -y));
    }
}
