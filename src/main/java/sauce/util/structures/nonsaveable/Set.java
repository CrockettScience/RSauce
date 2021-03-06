package sauce.util.structures.nonsaveable;

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

    public boolean contains(T e) {
        int currentPos = findPos(e);

        if (entryTable[currentPos] != null) {
            SetEntry i = entryTable[currentPos];

            while (i != null) {
                if (i.element.equals(e) && i.isActive)
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
        if(contains(e))
            return false;

        add(new SetEntry<>(e));

        return true;
    }

    public Set<T> union(Set<? extends T> otherSet) {
        Set<T> union = new Set<>();

        for (SetEntry<T> entry : entryTable) {
            union.add(entry.element);
        }

        for (int i = 0; i < otherSet.entryTable.length; i++) {
            union.add(otherSet.entryTable[i].element);
        }

        return union;
    }

    public <OT extends T> Set<T> intersection(Set<OT> otherSet) {
        Set<T> intersection = new Set<>();

        for (Set<OT>.SetEntry<OT> entry : otherSet.entryTable) {
            if (contains(entry.element)) {
                intersection.add(entry.element);
            }
        }

        return intersection;
    }

    public <OT extends T> Set<T> not(Set<OT> otherSet) {
        Set<T> not = new Set<>();

        for (Set<OT>.SetEntry<OT> entry : otherSet.entryTable) {
            if (!contains(entry.element)) {
                not.add(entry.element);
            }
        }

        return not;
    }

    public <OT extends T> Set<T> xor(Set<OT> otherSet) {
        Set<T> xor = new Set<>();

        for (Set<OT>.SetEntry<OT> entry : otherSet.entryTable) {
            if (!contains(entry.element)) {
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

    protected void add(SetEntry<T> entry) {
        int pos = findPos(entry.element);

        if(entryTable[pos] == null){
            entryTable[pos] = entry;
            occupied++;
            currentSize++;

            if(occupied >= entryTable.length)
                rehash();

        } else{
            SetEntry<T> currentEntry = entryTable[pos];
            while(true){
                if(currentEntry.next == null){
                    currentEntry.next = entry;
                    currentSize++;
                    return;
                }

                if(!currentEntry.next.isActive){
                    entry.next = currentEntry.next.next;
                    currentEntry.next = entry;
                    currentSize++;
                    return;
                }

                currentEntry = currentEntry.next;
            }

        }
    }

    public T remove(T e) {
        int currentPos = findPos(e);

        if (entryTable[currentPos] != null) {
            SetEntry<T> i = entryTable[currentPos];

            while (i != null) {
                if (i.element.equals(e) && i.isActive) {
                    i.isActive = false;
                    currentSize--;

                    if(occupied >= currentSize * 2 && occupied >= DEFAULT_TABLE_SIZE)
                        rehash();

                    return i.element;
                }

                i = i.next;
            }
        }

        return null;
    }

    public void clear() {
        allocateArray(DEFAULT_TABLE_SIZE);
        currentSize = 0;
        occupied = 0;
    }

    public T[] toArray(){
        T[] arr = (T[]) new Object[currentSize];
        int i = 0;
        Iterator<T> itr = iterator();
        while(itr.hasNext()){
            arr[i] = itr.next();
            i++;
        }
        return arr;
    }

    protected int findPos(T e) {
        return Math.abs(e.hashCode() % entryTable.length);
    }

    private void rehash() {
        SetEntry[] oldArray = entryTable;

        allocateArray(nextPrime(Math.max(2 * currentSize, DEFAULT_TABLE_SIZE)));
        currentSize = 0;
        occupied = 0;

        for (SetEntry entry : oldArray) {
            SetEntry<T> i = entry;

            while (i != null) {

                if(i.isActive)
                    add(i.element);

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
        return new SetIterator();
    }

    protected class SetEntry<T> {
        public T element;
        public boolean isActive;
        public SetEntry<T> next = null;

        protected SetEntry(T e) {
            element = e;
            isActive = true;
        }
    }

    protected class SetIterator implements Iterator<T> {
        private int current = 0;
        private int currentChainIndex = 0;

        @Override
        public boolean hasNext() {
            while(true){
                if (current >= entryTable.length)
                    return false;

                if(entryTable[current] != null){
                    SetEntry<T> entry = entryTable[current];

                    if(currentChainIndex == 0){
                        while(entry != null && !entry.isActive){
                            currentChainIndex++;
                            entry = entry.next;
                        }
                    }else {
                        for (int i = 0; i < currentChainIndex; i++) {
                            entry = entry.next;
                            if (entry != null && !entry.isActive && i == currentChainIndex - 1)
                                currentChainIndex++;
                        }
                    }

                    if(entry != null)
                        return true;

                }

                current++;
                currentChainIndex = 0;
            }

        }

        @Override
        public T next() {
            SetEntry<T> entry = entryTable[current];

            for(int i = 0; i < currentChainIndex; i++){
                entry = entry.next;
            }

            currentChainIndex++;

            return entry.element;

        }
    }
}
