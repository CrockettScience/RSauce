package demo.scenes;

import demo.entities.Button;
import demo.entities.Text;
import sauce.core.AudioManager;
import sauce.core.Music;
import sauce.core.Sprite;
import sauce.concurrent.Argument;
import sauce.concurrent.Return;
import sauce.concurrent.Script;
import sauce.attributes.BackgroundAttribute;
import sauce.core.*;
import sauce.util.structures.Color;
import sauce.collision.BoundBox;
import sauce.collision.CollisionUtil;
import sauce.util.structures.nonsaveable.ArrayList;

import static demo.util.DemoUtil.HEIGHT;
import static demo.util.DemoUtil.WIDTH;
import static sauce.core.InputServer.*;

public class Demo extends Scene implements InputClient{
    private ArrayList<Button> buttons = new ArrayList<>();

    @Override
    protected void loadResources() {

        Engine.setCamera(new Camera(0, 0, WIDTH, HEIGHT), true);

        BackgroundAttribute bg  = new BackgroundAttribute();
        addAttribute(bg);

        Background sky = new Background(Preferences.ASSET_ROOT + "sky.png", 0, 0);
        Background clouds = new Background(Preferences.ASSET_ROOT + "cloudParallax.png", -10f, 0);
        Background flare = new Background(Preferences.ASSET_ROOT + "flare.png", 0, 0);

        bg.setBackground(sky, 0);
        bg.setBackground(clouds, 1);
        bg.setBackground(flare, 2);

        Entity title = new Entity();
        {
            Sprite rsauce = new Sprite(Preferences.ASSET_ROOT + "main.png");
            rsauce.setOrigin(64, 32);

            SpriteComponent draw = new SpriteComponent(rsauce, 128, 168, 0);

            title.addComponent(draw);
        }

        Button eggy = new Button<>(Preferences.ASSET_ROOT + "button.png", "Eggy Test", 128, 92, 0, new Script<Argument, Return>(){

            @Override
            protected Return scriptMain(Argument args) {
                Engine.setScene(new EggyScene());
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
        putEntity("Exit", exit);

        buttons.add(eggy);
        buttons.add(exit);

        InputServer.bind(this);
        AudioManager.enqueue(new Music(Preferences.ASSET_ROOT + "waves.ogg", 0));

        if(Preferences.DEBUG) {
            Engine.add(new CollisionUtil.DrawBBoxWires());
        }

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
        activateEntity("Exit");
    }

    @Override
    public void receivedKeyEvent(InputEvent event) {
        if(event.key() == KEY_ESCAPE && event.action() == ACTION_RELEASED){
            Main.quitAtEndOfCycle();
        }
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
}
