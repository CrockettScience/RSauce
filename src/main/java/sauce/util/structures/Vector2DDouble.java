package sauce.util.structures;

public class Vector2DDouble implements Comparable<Vector2DDouble> {
    private double x;
    private double y;

    public Vector2DDouble(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public void setX(double x){
        this.x = x;
    }

    public void setY(double y){
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Vector2DDouble))
            return false;

        Vector2DDouble other = (Vector2DDouble) obj;
         return x == other.x && y == other.y;
    }

    @Override
    public int compareTo(Vector2DDouble o) {
        return (int) (x == o.x ? Math.signum(y - o.y) : Math.signum(x - o.x));
    }
}
