package com.util.structures.special;

import java.util.Comparator;
import java.util.Iterator;


/**
 * Created by John Crockett.
 */
public class SortedArrayList<T> implements Iterable<T> {
    private  static final int DEFAULT_CAPACITY = 10;
    private static final int NOT_FOUND = -1;

    private T[] elements;
    private int size;
    private Comparator<T> comp;

    public SortedArrayList(Comparator<T> comparator) {
        comp = comparator;
        clear( );
    }

    public SortedArrayList(int size, Comparator<T> comparator) {
        comp = comparator;
        clear(size);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty(){
        return size == 0;
    }

    public T get(int idx) {
        if( idx < 0 || idx >= size( ) )
            throw new ArrayIndexOutOfBoundsException( "Index " + idx + "; size " + size( ) );
        return elements[ idx ];
    }

    private T set(int idx, T newVal) {
        if( idx < 0 || idx >= size( ) )
            throw new ArrayIndexOutOfBoundsException( "Index " + idx + "; size " + size( ) );

        if(canGoHere(idx, newVal)){
            T old = elements[ idx ];
            elements[ idx ] = newVal;

            return old;
        }

        return set(findInsertIndex(newVal), newVal);

    }


    public boolean contains(Object x) {
        return findPos( x ) != NOT_FOUND;
    }

    private int findPos(Object x) {
        for( int i = 0; i < size( ); i++ )
            if( x == null )
            {
                if( elements[ i ] == null )
                    return i;
            }
            else if( x.equals( elements[ i ] ) )
                return i;

        return NOT_FOUND;

    }

    public boolean add(T element) {
        add(findInsertIndex(element), element);
        return true;
    }

    public void add(int index, T element) {
        if(isEmpty()){
            elements[0] = element;
            size++;
            return;
        }

        if(index < 0 || index > size())
            throw new ArrayIndexOutOfBoundsException( "Index " + index + "; size " + size( ) );

        if(elements.length == size())
            growArray();

        if(canGoHere(index, element))
            insert(index, element);

        else
            insert(findInsertIndex(element), element);

    }

    public boolean remove(Object x) {
        int pos = findPos( x );

        if( pos == NOT_FOUND )
            return false;
        else
        {
            remove( pos );
            return true;
        }
    }

    public T remove(int idx) {
        T removedItem = elements[ idx ];

        for( int i = idx; i < size( ) - 1; i++ )
            elements[ i ] = elements[ i + 1 ];
        size--;
        return removedItem;
    }

    public void clear() {
        size = 0;
        elements = (T[]) new Object[ DEFAULT_CAPACITY ];
    }

    public void clear(int size) {
        this.size = size;
        elements = (T[]) new Object[ size ];
    }

    public Iterator<T> iterator() {
        return new SortedArrayListIterator();
    }

    private boolean canGoHere(int index, T element) {
        /*In plain english: returns true if the item to my left is less or equal to me
                and the item at the index is greater or equal to me; check will never NPE
                if there are no null entries in the middle of the data
        */

        return index >= size - 1 ? true : comp.compare(elements[index], element) >= 0 && index == 0 ? true : comp.compare(elements[index - 1], element) <= 0;
    }

    private void insert(int index, T element) {
        size++;

        T lastElement = set(index, element);

        if(elements.length == size())
            growArray();

        for(int i = index + 1; i < size; i++){
            lastElement = set(i,lastElement);
        }
    }

    private int findInsertIndex(T element) {
        if(isEmpty())
            return 0;

        int low = 0;
        int high = size - 1;
        int mid;

        while(low < high){
            mid = (low + high) / 2;

            if(comp.compare(elements[mid], element) < 0)
                low = mid + 1;
            else
                high = mid;
        }

        if(comp.compare(elements[low], element) < 0)
            return low + 1;
        else
            return low;
    }

    private void growArray() {
        T [ ] old = elements;
        elements = (T []) new Comparable[ elements.length * 2 + 1 ];
        for( int i = 0; i < size( ); i++ )
            elements[ i ] = old[ i ];
    }

    private class SortedArrayListIterator<T> implements Iterator<T>{
        int current = 0;

        @Override
        public boolean hasNext() {
            return current < SortedArrayList.this.size();
        }

        @Override
        public T next() {
            return (T) SortedArrayList.this.elements[current++];

        }
    }
}