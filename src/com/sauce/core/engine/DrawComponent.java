package com.sauce.core.engine;

import com.sauce.asset.graphics.Graphic;
import com.util.Vector3D;

/**
 * Created by John Crockett.
 */
public class DrawComponent implements Component {
    private Graphic image;
    private Vector3D position;

    public DrawComponent(Graphic asset, int x, int y, int z){
        image = asset;
        position = new Vector3D(x, y, z);
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
    }

}
