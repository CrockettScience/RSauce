package com.structures.nonsaveable;

import java.util.Iterator;

/**
 * Created by John Crockett.
 */

public class ArrayList<T> implements Iterable<T> {
    private T[] elements;
    private int size;

    public ArrayList() {
        clear();
    }

    public ArrayList(T[] array){
        clear();

        for(T e : array){
            add(e);
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty(){
        return size() == 0;
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
        size++;

        if (elements.length == size)
            reAllocate();
    }

    public T remove(T element){
        for(int i = 0; i < size(); i++){
            if(element.equals(elements[i])) {
                T e = elements[i];
                elements[i] = null;
                size--;
                scoot(i);
                return e;
            }
        }

        return null;
    }

    public final void clear() {
        elements = (T[]) new Object[10];
        size = 0;
    }

    private void scoot(int emptyIndex){
        int i = emptyIndex;
        while(i < size()){
            elements[i] = elements[i + 1];
            i++;
        }
        elements[i] = null;
    }

    private void reAllocate() {
        elements = (T[]) new Object[elements.length * 2];
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayListIterator<>();
    }

    private class ArrayListIterator<T> implements Iterator<T>{

        int current = 0;

        @Override
        public boolean hasNext() {
            return current < ArrayList.this.elements.length;
        }

        @Override
        public T next() {
            return (T) ArrayList.this.elements[current];

        }
    }
}
