package com.structures.nonsaveable;

/**
 * Created by John Crockett.
 */

public class ArrayList<T> {
    private T[] elements;
    private int size;

    public ArrayList() {
        clear();
    }

    public int size() {
        return size;
    }

    public T get(int i) {
        if (i < size) {
            return elements[i];
        }

        throw new ArrayIndexOutOfBoundsException();
    }

    public void set(int i, T element) {
        if (i < size) {
            elements[i] = element;
        }

        throw new ArrayIndexOutOfBoundsException();
    }

    public void add(T element) {
        elements[size] = element;

        if (elements.length == size)
            reAllocate();
    }

    public final void clear() {
        elements = (T[]) new Object[10];
        size = 0;
    }

    private void reAllocate() {
        elements = (T[]) new Object[elements.length * 2];
    }
}
