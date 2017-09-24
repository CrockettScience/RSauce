package com.util.components;

import com.sauce.core.engine.Component;
import com.util.Vector2D;

public class BoundBox implements Component {
    private int x;
    private int y;
    private int width;
    private int height;
    private int radius;

    public BoundBox(int posX, int posY, int wide, int high){
        x = posX;
        y = posY;

        width = wide;
        height = high;
    }

    public boolean detectCollision(BoundBox other){
        return( x          < other.x + other.width  &&
                y          < other.y + other.height &&
                x + width  > other.x                &&
                y + height > other.y                );
    }

    public boolean detectPointInside(Vector2D point){
        return( x < width  + x && x > x &&
                y < height + y && y > y);
    }

}
