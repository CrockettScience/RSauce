package sauce.util.structures;

import sauce.util.structures.nonsaveable.HashGrid;

/**
 * Created by John Crockett.
 */
public class Vector2D implements Comparable<Vector2D> {

    private static HashGrid<Vector2D> vecMap = new HashGrid<>();

    private int xx;
    private int yy;

    private Vector2D(int x, int y) {
        xx = x;
        yy = y;
    }

    public static Vector2D create(int x, int y) {
        Vector2D vec = vecMap.get(x, y);

        if(vec != null)
            return vec;

        vec = new Vector2D(x, y);
        vecMap.set(x, y, vec);
        return vec;
    }

    public int getX(){
        return xx;
    }

    public int getY(){
        return yy;
    }

    public void setX(int x){
        setMap(xx, yy, x, yy);
    }

    public void setY(int y){
        setMap(xx, yy, xx, y);
    }

    private void setMap(int oldX, int oldY, int x, int y){
        vecMap.set(oldX, oldY, null);
        vecMap.set(x, y, null);

        xx = x;
        yy = y;
    }

    public static Vector2DDouble toVector2DDouble(Vector2D v){
        return new Vector2DDouble((double) v.xx, (double) v.yy);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Vector2D))
            return false;

        Vector2D other = (Vector2D) obj;
        return xx == other.xx && yy == other.yy;
    }

    @Override
    public int compareTo(Vector2D o) {
        return xx == o.xx ? yy - o.yy : xx - o.xx;
    }
}
