package com.demo.scenes;

import com.demo.entities.Button;
import com.demo.entities.Text;
import com.sauce.asset.audio.AudioThread;
import com.sauce.asset.audio.Music;
import com.sauce.asset.graphics.Image;
import com.sauce.asset.scripts.Argument;
import com.sauce.asset.scripts.Return;
import com.sauce.asset.scripts.Script;
import com.sauce.core.Main;
import com.sauce.core.Preferences;
import com.sauce.core.engine.*;
import com.sauce.core.scene.BackgroundAttribute;
import com.sauce.core.scene.Camera;
import com.sauce.core.scene.Scene;
import com.sauce.core.scene.SceneManager;
import com.sauce.input.InputClient;
import com.sauce.input.InputEvent;
import com.sauce.input.InputServer;
import com.util.Color;
import com.util.collision.BoundBox;
import com.util.collision.CollisionUtil;
import com.util.structures.nonsaveable.ArrayList;

import static com.sauce.core.scene.SceneManager.*;
import static com.demo.util.DemoUtil.*;
import static com.sauce.input.InputServer.ACTION_RELEASED;
import static com.sauce.input.InputServer.KEY_ESCAPE;

public class Demo extends Scene implements InputClient{

    @Override
    protected void loadResources() {
        setCamera(new Camera(0, 0, WIDTH, HEIGHT, 0, 0));

        BackgroundAttribute bg  = new BackgroundAttribute();
        addAttribute(bg);

        ParallaxBackground sky = new ParallaxBackground(Preferences.ASSET_ROOT + "sky.png", 0, 0);
        ParallaxBackground clouds = new ParallaxBackground(Preferences.ASSET_ROOT + "cloudParallax.png", 1, 0);
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

        putEntity("RSauce", title);
        putEntity("Version String", new Text(Preferences.ASSET_ROOT + "coderCrux.ttf", Color.C_BLACK, Preferences.ENGINE_VERSION, 8, 128, 128, 0, 6));

        Button button = new Button<>(Preferences.ASSET_ROOT + "button.png", 128, 56, 0, new Script<Argument, Return>(){

            @Override
            protected Return scriptMain(Argument args) {
                SceneManager.setScene(new EggyScene());
                return null;
            }

        });

        putEntity("Button", button);
        putEntity("Start String", new Text(Preferences.ASSET_ROOT + "coderCrux.ttf", Color.C_BLACK, "Start", 12, 108, 50, -1, 4));

        MouseButtonSystem mbs = new MouseButtonSystem(0);
        mbs.addButton(button);

        Engine.getEngine().add(mbs);

        InputServer.bind(this);

        AudioThread.enqueue(new Music(Preferences.ASSET_ROOT + "waves.ogg", 0));

    }

    @Override
    protected void destroyResources() {
        Engine.getEngine().removeStepSystem(MouseButtonSystem.class);
        InputServer.unbind(this);
    }

    @Override
    protected void sceneMain() {
        activateEntity("RSauce");
        activateEntity("Version String");
        activateEntity("Start String");
        activateEntity("Button");
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

    private class MouseButtonSystem extends StepSystem{

        private ArrayList<Button> buttons = new ArrayList<>();

        public MouseButtonSystem(int priority) {
            super(priority);
        }

        @Override
        public void addedToEngine(Engine engine) {

        }

        @Override
        public void update(double delta) {
            for(Button button : buttons){
                if(button.getComponent(BoundBox.class).detectPointInside(InputServer.mouseScenePosition())){
                    button.turnOn();
                    if(InputServer.isButtonPressed(InputServer.MOUSE_LEFT)){
                        button.invoke();
                    }
                }
                else
                    button.turnOff();
            }
        }

        @Override
        public void removedFromEngine(Engine engine) {

        }

        private void addButton(Button button){
            buttons.add(button);
        }
    }
}
