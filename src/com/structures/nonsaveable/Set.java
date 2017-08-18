package com.structures.nonsaveable;

import java.util.Iterator;

/**
 * Created by John Crockett.
 */

public class Set<T> implements Iterable<T> {
    private static final int DEFAULT_TABLE_SIZE = 101;

    protected SetEntry<T>[] entryTable;
    private int occupied;
    private int currentSize;

    public Set() {
        clear();
    }

    public int size() {
        return currentSize;
    }

    public boolean isEmpty() {
        return currentSize == 0;
    }

    public boolean contains(Object e) {

        T element;
        try {
            element = (T) e;
        } catch (ClassCastException ex) {
            return false;
        }

        int currentPos = findPos(element);

        if (entryTable[currentPos] != null) {
            SetEntry i = entryTable[currentPos];

            while (i.next != null) {
                if (i.element.equals(element) && i.isActive)
                    return true;

                i = i.next;
            }
        }

        return false;
    }

    public boolean containsAll(Set<T> otherSet) {
        for (T e : otherSet) {
            if (!contains(e))
                return false;
        }

        return true;
    }

    public boolean add(T e) {
        return add(new SetEntry(e));
    }

    public Set<T> union(Set<? extends T> otherSet) {
        Set<T> union = new Set<T>();

        for (SetEntry<T> entry : entryTable) {
            union.add(entry.element);
        }

        for (int i = 0; i < otherSet.entryTable.length; i++) {
            union.add(otherSet.entryTable[i].element);
        }

        return union;
    }

    public Set<T> intersection(Set<? extends T> otherSet) {
        Set<T> intersection = new Set<T>();

        for (SetEntry<T> entry : entryTable) {
            if (otherSet.contains(entry.element)) {
                intersection.add(entry.element);
            }
        }

        return intersection;
    }

    public Set<T> not(Set<? extends T> otherSet) {
        Set<T> not = new Set<T>();

        for (SetEntry<T> entry : entryTable) {
            if (!otherSet.contains(entry.element)) {
                not.add(entry.element);
            }
        }

        return not;
    }

    public Set<T> xor(Set<? extends T> otherSet) {
        Set<T> xor = new Set<T>();

        for (SetEntry<T> entry : entryTable) {
            if (!otherSet.contains(entry.element)) {
                xor.add(entry.element);
            }
        }

        for (int i = 0; i < otherSet.entryTable.length; i++) {
            if (!this.contains(otherSet.entryTable[i].element)) {
                xor.add(otherSet.entryTable[i].element);
            }
        }

        return xor;
    }

    private boolean add(SetEntry<T> entry) {
        int currentPos = findPos(entry.element);

        if (entryTable[currentPos] != null) {
            SetEntry i = entryTable[currentPos];

            while (i.next != null && !i.element.equals(entry.element))
                i = i.next;

            if (i.element.equals(entry.element)) {
                if (i.isActive)
                    return false;
                else {
                    i.element = entry.element;
                    currentSize++;
                    return true;
                }
            } else
                occupied++;

            i.next = entry;
            currentSize++;
        } else {
            occupied++;
            entryTable[currentPos] = entry;
            currentSize++;
        }

        if (occupied > entryTable.length)
            rehash();

        return true;
    }

    public boolean remove(T e) {
        int currentPos = findPos(e);

        if (entryTable[currentPos] != null) {
            SetEntry i = entryTable[currentPos];

            while (i.next != null) {
                if (i.element.equals(e) && i.isActive) {
                    i.isActive = false;
                    currentSize--;
                    return true;
                }

                i = i.next;
            }
        }

        return false;
    }

    public void clear() {
        allocateArray(DEFAULT_TABLE_SIZE);
        currentSize = 0;
        occupied = 0;
    }

    private int findPos(T e) {
        return Math.abs(e.hashCode() % entryTable.length);
    }

    private void rehash() {
        SetEntry[] oldArray = entryTable;

        allocateArray(nextPrime(2 * currentSize));
        currentSize = 0;

        for (SetEntry entry : oldArray) {
            SetEntry i = entry;
            while (i != null) {
                add(i);
                i = i.next;
            }
        }
    }

    private static int nextPrime(int n) {
        if (n % 2 == 0)
            n++;

        for (; !isPrime(n); )
            n += 2;

        return n;
    }

    private static boolean isPrime(int n) {
        if (n == 2 || n == 3)
            return true;

        if (n == 1 || n % 2 == 0)
            return false;

        for (int i = 3; i * i <= n; i += 2)
            if (n % i == 0)
                return false;

        return true;
    }

    private void allocateArray(int arraySize) {
        entryTable = new SetEntry[nextPrime(arraySize)];
    }

    @Override
    public Iterator<T> iterator() {
        return (Iterator<T>) new SetIterator();
    }

    protected class SetEntry<T> {
        public T element;
        private boolean isActive;
        public SetEntry next = null;

        protected SetEntry(T e) {
            element = e;
            isActive = true;
        }
    }

    private class SetIterator implements Iterator {
        private int current = 0;

        @Override
        public boolean hasNext() {
            while (Set.this.entryTable[current] == null) {
                current++;
                if (current >= Set.this.entryTable.length)
                    return false;
            }

            return true;
        }

        @Override
        public Object next() {
            return Set.this.entryTable[current++].element;
        }
    }
}
