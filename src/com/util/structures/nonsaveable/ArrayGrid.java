package com.util.structures.nonsaveable;

/**
 * Created by John Crockett.
 */

public class ArrayGrid<T> {
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
}
