package util;

public class Vector2DFloat implements Comparable<Vector2DFloat>{
    private float x;
    private float y;

    public Vector2DFloat(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public void setX(float x){
        this.x = x;
    }

    public void setY(float y){
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Vector2DFloat))
            return false;

        Vector2DFloat other = (Vector2DFloat) obj;
        return x == other.x && y == other.y;
    }

    @Override
    public int compareTo(Vector2DFloat o) {
        return (int) (x == o.x ? Math.signum(y - o.y) : Math.signum(x - o.x));
    }
    
}
