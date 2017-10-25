package demo.scenes;

import sauce.asset.graphics.DrawBatch;
import sauce.asset.graphics.Surface;
import sauce.asset.graphics.TextureAtlas;
import sauce.core.Preferences;
import sauce.core.engine.*;
import sauce.input.InputClient;
import sauce.input.InputEvent;
import sauce.input.InputServer;
import util.Color;

import static sauce.input.InputServer.*;

public class AtlasDemo extends Scene implements InputClient{

    private TextureAtlas<Double> atlas = new TextureAtlas<>();

    @Override
    protected void loadResources() {
        SceneManager.setCamera(new Camera(0, 0, Preferences.TEXTURE_PAGE_SIZE, Preferences.TEXTURE_PAGE_SIZE), true);


        Engine.getEngine().add(new DrawSystem(0){

            @Override
            public void addedToEngine(Engine engine) {

            }

            @Override
            public void update(double delta) {
                DrawBatch batch = new DrawBatch();
                batch.add(atlas.getPageSurface(0), 0, 0);
                batch.renderBatch();
            }

            @Override
            public void removedFromEngine(Engine engine) {

            }
        });

        InputServer.bind(this);

    }

    @Override
    protected void destroyResources() {
        InputServer.unbind(this);
    }

    @Override
    protected void sceneMain() {
        activateEntity("atlas");
    }

    @Override
    public void receivedKeyEvent(InputEvent event) {
        if(event.action() == ACTION_RELEASED && event.key() == KEY_ESCAPE)
            SceneManager.setScene(new Demo());


    }

    @Override
    public void receivedTextEvent(char character) {
        if(character == ' '){
            int width = (int)(Math.random() * 100) + 10;
            int height = (int)(Math.random() * 100) + 10;

            Surface surface = new Surface(width, height);
            surface.clear(new Color((float)Math.random(), (float)Math.random(), (float)Math.random()), 1);
            atlas.putTexture(Math.random(), surface);

        }
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
