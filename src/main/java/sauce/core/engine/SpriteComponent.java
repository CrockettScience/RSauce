package sauce.core.engine;

import sauce.asset.scripts.Script;
import util.RSauceLogger;
import util.Vector3D;

/**
 * Created by John Crockett.
 */
public class SpriteComponent implements Component {
    private Sprite sprite;
    private Vector3D position;
    private Script<?, ?> script;

    public SpriteComponent(Sprite sprite, int x, int y, int z){
        this.sprite = sprite;
        position = new Vector3D(x, y, z);
    }

    public SpriteComponent(Sprite sprite, int x, int y, int z, Script<?, ?> drawScript){
        this.sprite = sprite;
        position = new Vector3D(x, y, z);
        script = drawScript;
    }

    public SpriteComponent(Script<?, ?> drawScript, int z){
        script = drawScript;
        position = new Vector3D(0, 0, z);
    }

    public void attachDrawScript(Script<?, ?> drawScript){
        script = drawScript;
    }

    Script<?, ?> getScript(){
        return script;
    }

    public Sprite getSprite(){
        return sprite;
    }

    public int getX(){
        return position.getX();
    }

    public int getY(){
        return position.getY();
    }

    public int getZ(){
        return position.getZ();
    }

    public void setX(int x){
        position.setX(x);
    }

    public void setY(int y){
        position.setY(y);
    }

    public void setZ(int z){
        position.setZ(z);
        Engine.entityChangedZ(entity);

    }

    private Entity entity = null;

    @Override
    public boolean addedToEntity(Entity ent) {
        if(entity == null) {
            entity = ent;
            return true;
        }

        RSauceLogger.printWarningln("This SpriteComponent already belongs to another Entity");
        return false;
    }

    @Override
    public void removedFromEntity(Entity ent) {
        entity = null;
    }

    @Override
    public void dispose() {
        if(sprite != null)
            sprite.dispose();
    }
}
