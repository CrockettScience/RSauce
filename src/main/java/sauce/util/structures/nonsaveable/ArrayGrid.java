package sauce.util.structures.nonsaveable;

import java.util.Iterator;

/**
 * Created by John Crockett.
 */

public class ArrayGrid<T> implements Iterable<T> {
    protected T[][] grid;
    private int width;
    private int height;

    public ArrayGrid(int w, int h) {
        width = w;
        height = h;
        clear();
    }

    public void set(int x, int y, T element) {
        grid[x][y] = element;
    }

    public T get(int x, int y) {
        return grid[x][y];
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int size() {
        return width * height;
    }

    public void resize(int w, int h) {
        T[][] oldGrid = grid;
        width = w;
        height = h;
        clear();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j] = oldGrid[i][j];
            }
        }
    }

    public void clear() {
        grid = (T[][]) new Object[width][height];
    }

    public void clear(T o){
        clear();

        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                set(i, j, o);
            }
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new GridIterator();
    }

    private class GridIterator implements Iterator<T>{
        private int x = 0;
        private int y = 0;


        @Override
        public boolean hasNext() {
            return x < width && y < height;
        }

        @Override
        public T next() {
            return get(x++, y++);
        }
    }
}
