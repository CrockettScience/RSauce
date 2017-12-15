package sauce.util.structures;

/**
 * Created by John Crockett.
 */
public class Vector3D implements Comparable<Vector3D> {
    private int x;
    private int y;
    private int z;

    public Vector3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public int getZ(){
        return z;
    }

    public void setX(int x){
        this.x = x;
    }

    public void setY(int y){
        this.y = y;
    }

    public void setZ(int z){
        this.z = z;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Vector3D))
            return false;

        Vector3D other = (Vector3D) obj;
        return x == other.x && y == other.y && z == other.z;
    }

    @Override
    public int compareTo(Vector3D o) {
        return x == o.x ? y == o.y ? z - o.z : y - o.y : x - o.x;
    }
}
