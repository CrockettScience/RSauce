package util.structures.threadsafe;

import util.structures.nonsaveable.Set;

import java.util.Iterator;

public class SafeSet<T> extends Set<T> {

    @Override
    public Iterator<T> iterator() {
        return new SafeSetIterator(entryTable);
    }

    protected class SafeSetIterator extends Set<T>.SetIterator{

        SetEntry<T>[] savedTable;
        private int current = 0;
        private int currentChainIndex = 0;

        private SafeSetIterator(SetEntry<T>[] table){

            savedTable = (SetEntry<T>[]) new Object[table.length];
            System.arraycopy(table, 0, savedTable, 0, table.length);
        }

        @Override
        public boolean hasNext() {
            while(true){
                if (current >= savedTable.length)
                    return false;

                if(savedTable[current] != null){
                    SetEntry<T> entry = savedTable[current];

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
            SetEntry<T> entry = savedTable[current];

            for(int i = 0; i < currentChainIndex; i++){
                entry = entry.next;
            }

            currentChainIndex++;

            return entry.element;

        }

    }
}
