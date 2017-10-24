package util;

/**
 * Created by John Crockett.
 */
public class Vector2D implements Comparable<Vector2D> {
    private int x;
    private int y;

    public Vector2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public void setX(int x){
        this.x = x;
    }

    public void setY(int y){
        this.y = y;
    }

    public static Vector2DDouble toVector2DDouble(Vector2D v){
        return new Vector2DDouble((double) v.x, (double) v.y);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Vector2D))
            return false;

        Vector2D other = (Vector2D) obj;
        return x == other.x && y == other.y;
    }

    @Override
    public int compareTo(Vector2D o) {
        return x == o.x ? y - o.y : x - o.x;
    }
}
