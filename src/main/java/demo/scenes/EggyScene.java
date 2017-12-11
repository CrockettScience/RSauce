package demo.scenes;

import demo.entities.Eggy;
import demo.systems.CollisionTest;
import sauce.asset.audio.AudioManager;
import sauce.asset.audio.Music;
import sauce.core.*;
import sauce.input.InputClient;
import sauce.input.InputEvent;
import sauce.input.InputServer;

import static demo.util.DemoUtil.HEIGHT;
import static demo.util.DemoUtil.WIDTH;
import static sauce.core.Preferences.ASSET_ROOT;
import static sauce.input.InputServer.*;

/**
 * Created by John Crockett.
 */
public class EggyScene extends Scene implements InputClient {

    private static final int VIEW_SPEED = 50;
    private static final int ZOOM_SPEED = 10;

    @Override
    protected void loadResources() {

        Engine.setCamera(new Camera(0, 0, WIDTH, HEIGHT, 0, 0), true);

        // Setup Eggy
        Entity eggy1 = new Eggy(1, 1, Engine.getCamera().getWidth() / 2, Engine.getCamera().getHeight() / 2, 0);

        putEntity("eggy", eggy1);

        // Setup Background
        BackgroundAttribute attr = new BackgroundAttribute();
        addAttribute(attr);

        ParallaxBackground grass = new ParallaxBackground(ASSET_ROOT + "grass.png", 0, 0);
        attr.setBackground(grass, 0);

        // Bind to recieve InputEvents
        bind(this);

        AudioManager.enqueue(new Music(Preferences.ASSET_ROOT + "Patriarchy.ogg", 21.391f));
    }

    @Override
    protected void destroyResources() {
        InputServer.unbind(this);
        Engine.remove(CollisionTest.class);
    }

    @Override
    protected void sceneMain() {
        activateEntity("eggy");
    }

    @Override
    public void receivedKeyEvent(InputEvent event) {
        if(event.key() == KEY_ESCAPE && event.action() == ACTION_RELEASED){
            Engine.setScene(new Demo());
        }

        else if(event.key() == KEY_UP || event.key() == KEY_DOWN || event.key() == KEY_LEFT || event.key() == KEY_RIGHT ) {
            if (event.key() == KEY_UP) {
                Engine.getCamera().move(Engine.getCamera().getX(), Engine.getCamera().getY() + VIEW_SPEED);
            }

            if (event.key() == KEY_DOWN) {
                Engine.getCamera().move(Engine.getCamera().getX(),Engine.getCamera().getY() - VIEW_SPEED);
            }

            if (event.key() == KEY_LEFT) {
                Engine.getCamera().move(Engine.getCamera().getX() - VIEW_SPEED, Engine.getCamera().getY());
            }

            if (event.key() == KEY_RIGHT) {
                Engine.getCamera().move(Engine.getCamera().getX() + VIEW_SPEED, Engine.getCamera().getY());
            }
        }

        else if(event.key() == KEY_ENTER)
            Engine.add(new CollisionTest(0, (Eggy) getEntity("eggy")));

        else if(event.key() == KEY_F && event.action() == ACTION_RELEASED){
            if(Preferences.isFullscreen())
                Preferences.setFullscreen(false);
            else
                Preferences.setFullscreen(true);
        }

        else if(event.key() == KEY_U && event.action() == ACTION_PRESSED){
            AudioManager.setVolMaster(AudioManager.getVolMaster() + 0.1f);
        }
        else if(event.key() == KEY_J && event.action() == ACTION_PRESSED){
            AudioManager.setVolMaster(AudioManager.getVolMaster() - 0.1f);
        }
        else if(event.key() == KEY_I && event.action() == ACTION_PRESSED){
            AudioManager.setVolSFX(AudioManager.getVolSFX() + 0.1f);
        }
        else if(event.key() == KEY_K && event.action() == ACTION_PRESSED){
            AudioManager.setVolSFX(AudioManager.getVolSFX() - 0.1f);
        }
        else if(event.key() == KEY_O && event.action() == ACTION_PRESSED){
            AudioManager.setVolMusic(AudioManager.getVolMusic() + 0.1f);
        }
        else if(event.key() == KEY_L && event.action() == ACTION_PRESSED){
            AudioManager.setVolMusic(AudioManager.getVolMusic() - 0.1f);
        }
    }

    @Override
    public void mouseScrolled(double x, double y) {
        Camera v = Engine.getCamera();
        v.resize((int)(v.getWidth() + ZOOM_SPEED * -y), (int)(v.getHeight() + (ZOOM_SPEED * Preferences.getCurrentScreenHeight() / Preferences.getCurrentScreenWidth()) * -y));
    }
}
