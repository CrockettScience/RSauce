package util.structures.safeiterable;

import util.structures.nonsaveable.ArrayList;

import java.util.Iterator;

public class SafeArrayList<T> extends ArrayList<T> {

    @Override
    public Iterator<T> iterator() {
        return new SafeArraylistIterator();
    }

    protected class SafeArraylistIterator extends ArrayList<T>.ArrayListIterator{

        T[] savedElements = (T[]) new Object[size()];
        int current = 0;

        private SafeArraylistIterator(){
            System.arraycopy(elements, 0, savedElements, 0, size());
        }

        @Override
        public boolean hasNext() {
            return current < savedElements.length;
        }

        @Override
        public T next() {
            return savedElements[current++];

        }


    }
}
