package com.sauce.core.engine;

import com.sauce.asset.graphics.Graphic;
import com.sauce.asset.scripts.Script;
import com.util.Vector3D;

/**
 * Created by John Crockett.
 */
public class DrawComponent implements Component {
    private Graphic image;
    private Vector3D position;
    private Entity entity = null;
    private Script<?, ?> script;

    public DrawComponent(Graphic asset, int x, int y, int z){
        image = asset;
        position = new Vector3D(x, y, z);
    }

    public DrawComponent(Graphic asset, int x, int y, int z, Script<?, ?> drawScript){
        image = asset;
        position = new Vector3D(x, y, z);
        script = drawScript;
    }

    public DrawComponent(Script<?, ?> drawScript, int z){
        script = drawScript;
        position = new Vector3D(0, 0, z);
    }

    boolean setEntity(Entity ent){
        if(entity == null) {
            entity = ent;
            return true;
        } else {
            return false;
        }
    }

    public void attachDrawScript(Script<?, ?> drawScript){
        script = drawScript;
    }

    Script<?, ?> getScript(){
        return script;
    }

    public Graphic getImage(){
        return image;
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
        Engine.getEngine().entityChangedZ(entity);

    }

}
