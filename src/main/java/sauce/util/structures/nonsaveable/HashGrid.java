package sauce.util.structures.nonsaveable;

import java.util.Iterator;

/**
 * Created by John Crockett.
 */

public class HashGrid<T> implements Iterable<T>{
    protected Map<Coordinates, T> gridMap;

    public HashGrid() {
        clear();
    }

    public void set(int x, int y, T data) {

        Coordinates point = makeCoordinates(x, y);
        gridMap.put(point, data);
    }

    public T get(int x, int y) {

        return gridMap.get(makeCoordinates(x, y));
    }

    public int size() {
        return gridMap.size();
    }

    public void clear() {
        gridMap = new Map<>();
    }



    protected Coordinates makeCoordinates(int x, int y) {
        return new Coordinates(x, y);
    }

    @Override
    public Iterator<T> iterator() {
        return gridMap.valueSet().iterator();
    }

    protected static class Coordinates {
        protected Integer x;
        protected Integer y;

        Coordinates(int xx, int yy) {
            x = xx;
            y = yy;
        }

        @Override
        public int hashCode() {
            return x.hashCode() ^ y.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Coordinates)) {
                return false;
            }
            Coordinates other = (Coordinates) obj;
            return (other.x.equals(x) && other.y.equals(y));
        }
    }
}
