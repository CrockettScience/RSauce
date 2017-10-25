package demo.scenes;

import demo.entities.Button;
import demo.entities.Text;
import sauce.asset.audio.AudioThread;
import sauce.asset.audio.Music;
import sauce.asset.graphics.Image;
import sauce.asset.scripts.Argument;
import sauce.asset.scripts.Return;
import sauce.asset.scripts.Script;
import sauce.core.Main;
import sauce.core.Preferences;
import sauce.core.engine.*;
import sauce.core.scene.BackgroundAttribute;
import sauce.core.engine.Camera;
import sauce.core.engine.Scene;
import sauce.core.engine.SceneManager;
import sauce.input.InputClient;
import sauce.input.InputEvent;
import sauce.input.InputServer;
import util.Color;
import util.collision.BoundBox;
import util.collision.CollisionUtil;
import util.structures.nonsaveable.ArrayList;

import static demo.util.DemoUtil.HEIGHT;
import static demo.util.DemoUtil.WIDTH;
import static sauce.core.engine.SceneManager.*;
import static sauce.input.InputServer.ACTION_RELEASED;
import static sauce.input.InputServer.KEY_ESCAPE;
import static sauce.input.InputServer.MOUSE_LEFT;

public class Demo extends Scene implements InputClient{
    private ArrayList<Button> buttons = new ArrayList<>();

    @Override
    protected void loadResources() {
        setCamera(new Camera(0, 0, WIDTH, HEIGHT), true);

        BackgroundAttribute bg  = new BackgroundAttribute();
        addAttribute(bg);

        ParallaxBackground sky = new ParallaxBackground(Preferences.ASSET_ROOT + "sky.png", 0, 0);
        ParallaxBackground clouds = new ParallaxBackground(Preferences.ASSET_ROOT + "cloudParallax.png", -1, 0);
        ParallaxBackground flare = new ParallaxBackground(Preferences.ASSET_ROOT + "flare.png", 0, 0);

        bg.setBackground(sky, 0);
        bg.setBackground(clouds, 1);
        bg.setBackground(flare, 2);

        Entity title = new Entity();
        {
            Image rsauce = new Image(Preferences.ASSET_ROOT + "main.png");
            rsauce.setOrigin(64, 32);

            DrawComponent draw = new DrawComponent(rsauce, 128, 168, 0);

            title.addComponent(draw);
        }

        Button eggy = new Button<>(Preferences.ASSET_ROOT + "button.png", "Eggy Test", 128, 92, 0, new Script<Argument, Return>(){

            @Override
            protected Return scriptMain(Argument args) {
                SceneManager.setScene(new EggyScene());
                return null;
            }

        });

        Button atlas = new Button<>(Preferences.ASSET_ROOT + "button.png", "Atlas Demo", 128, 56, 0, new Script<Argument, Return>(){

            @Override
            protected Return scriptMain(Argument args) {
                SceneManager.setScene(new AtlasDemo());
                return null;
            }

        });

        Button exit = new Button<>(Preferences.ASSET_ROOT + "button.png", "Exit", 128, 20, 0, new Script<Argument, Return>(){

            @Override
            protected Return scriptMain(Argument args) {
                Main.quitAtEndOfCycle();
                return null;
            }

        });



        putEntity("RSauce", title);
        putEntity("Version String", new Text(Preferences.ASSET_ROOT + "coderCrux.ttf", Color.C_BLACK, Preferences.ENGINE_VERSION, 8, 128, 128, 0, 6));
        putEntity("Eggy", eggy);
        putEntity("Atlas", atlas);
        putEntity("Exit", exit);

        buttons.add(eggy);
        buttons.add(atlas);
        buttons.add(exit);

        InputServer.bind(this);
        AudioThread.enqueue(new Music(Preferences.ASSET_ROOT + "waves.ogg", 0));

        if(Preferences.DEBUG)
            Engine.getEngine().add(new CollisionUtil.DrawBBoxWires());

    }

    @Override
    protected void destroyResources() {
        InputServer.unbind(this);
    }

    @Override
    protected void sceneMain() {
        activateEntity("RSauce");
        activateEntity("Version String");
        activateEntity("Eggy");
        activateEntity("Atlas");
        activateEntity("Exit");
    }

    @Override
    public void receivedKeyEvent(InputEvent event) {
        if(event.key() == KEY_ESCAPE && event.action() == ACTION_RELEASED){
            Main.quitAtEndOfCycle();
        }
    }

    @Override
    public void receivedTextEvent(char character) {

    }

    @Override
    public void mouseScrolled(double x, double y) {

    }

    Button highlight;

    @Override
    public void cursorPosChanged(double x, double y) {
        boolean highlighted = false;
        for(Button button : buttons){
            if(button.getComponent(BoundBox.class).detectPointInside(InputServer.mouseScenePosition())){
                button.turnOn();
                highlight = button;
                highlighted = true;
            }
            else
                button.turnOff();
        }

        if(!highlighted)
            highlight = null;
    }

    @Override
    public void receivedMouseButtonEvent(InputEvent event) {
        if(highlight != null && event.key() == MOUSE_LEFT && event.action() == ACTION_RELEASED)
            highlight.invoke();

    }

    @Override
    public void joystickConnected(int joyID) {

    }

    @Override
    public void joystickDisconnected(int joyID) {

    }
}