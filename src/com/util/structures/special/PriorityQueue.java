package com.util.structures.special;

import java.util.NoSuchElementException;

/**
 * Created by John Crockett.
 */

public class PriorityQueue<T extends Comparable<? super T>> {

    private static final int DEFAULT_CAPACITY = 100;

    private int size;   // Number of elements in heap
    private T[] array; // The heap array

    public PriorityQueue() {
        size = 0;
        array = (T[]) new Object[DEFAULT_CAPACITY + 1];
    }

    public boolean add(T e) {
        if (size + 1 == array.length)
            reAllocate();

        // Percolate up
        int hole = ++size;
        array[0] = e;

        for (; e.compareTo(array[hole / 2]) < 0; hole /= 2)
            array[hole] = array[hole / 2];

        array[hole] = e;

        return true;
    }

    public int size() {
        return size;
    }

    public void clear() {
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public T getNext() {
        if (isEmpty())
            throw new NoSuchElementException();
        return array[1];
    }

    public T removeNext() {
        T minItem = getNext();
        array[1] = array[size--];
        percolateDown(1);

        return minItem;
    }

    private void percolateDown(int hole) {
        int child;
        T tmp = array[hole];

        for (; hole * 2 <= size; hole = child) {
            child = hole * 2;
            if (child != size &&
                    array[child + 1].compareTo(array[child]) < 0)
                child++;
            if (array[child].compareTo(tmp) < 0)
                array[hole] = array[child];
            else
                break;
        }
        array[hole] = tmp;
    }

    private void reAllocate() {
        T[] newArray;

        newArray = (T[]) new Object[array.length * 2];
        for (int i = 0; i < array.length; i++)
            newArray[i] = array[i];
        array = newArray;
    }
}
