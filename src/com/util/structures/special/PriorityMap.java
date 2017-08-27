package com.util.structures.special;

import com.util.structures.nonsaveable.LinkedList;
import com.util.structures.nonsaveable.Map;
import com.util.structures.nonsaveable.Queue;
import com.util.structures.nonsaveable.Set;

import java.util.Iterator;

/**
 * Created by John Crockett.
 * The same as a PriorityQueue<T>, but with the functionality of Map-like linear access and modification
 * to remove elements, user must use remove method with key. PriorityMap cycles through elements.
 */
public class PriorityMap<K, V>{

    private PriorityQueue<PriorityMapEntry<K, V>> queue;
    private Queue<PriorityMapEntry<K, V>> poolQueue = new Queue<>();
    private Map<K, PriorityMapEntry<K, V>> map;

    public PriorityMap(){
        queue = new PriorityQueue<>();
        map = new Map<>();
    }

    public PriorityMap(Map<K, V> otherMap, int priority){
        queue = new PriorityQueue<>();
        map = new Map<>();

        Set<K> keys = otherMap.keySet();

        for(K key : keys) {
            PriorityMapEntry<K, V> entry = new PriorityMapEntry(key, otherMap.get(key), priority);
            map.put(key, entry);
            queue.add(entry);

        }

    }

    public void put(K key, V value, int priority) {
        PriorityMapEntry<K, V> entry = new PriorityMapEntry<>(key, value, priority);
        queue.add(entry);
        map.put(key, entry);
    }

    public int size() {
        return map.size();
    }

    public void clear() {
        queue.clear();
        map.clear();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public V next() {
        skipToNextActiveElement();

        PriorityMapEntry<K, V> entry = queue.getNext();
        poolQueue.enqueue(entry);

        if(isEmpty()) {
            while(!poolQueue.isEmpty()){
                PriorityMapEntry<K, V> poolEntry = poolQueue.dequeue();
                put(poolEntry.key, poolEntry.value, poolEntry.priority);
            }
        }

        return entry.value;
    }

    public V get(K key){
        if(containsKey(key))
            return map.get(key).value;

        return null;
    }

    public boolean remove(K key){
        if(!containsKey(key))
            return false;

        map.get(key).isActive = false;
        map.remove(key);

        return true;
    }

    public boolean containsKey(K key){
        if(map.containsKey(key) && map.get(key).isActive){
            return true;
        }

        return false;
    }

    private void skipToNextActiveElement(){
        while(!queue.getNext().isActive) {
            PriorityMapEntry<K, V> entry = queue.removeNext();
            map.remove(entry.key);
        }
    }

    private class PriorityMapEntry<KeyType, T> implements Comparable<PriorityMapEntry<KeyType, V>>{
        private boolean isActive;
        private int priority;
        private T value;
        private KeyType key;

        private PriorityMapEntry(KeyType k, T v, int p){
            priority = p;
            value = v;
            key = k;
            isActive = true;
        }

        @Override
        public int compareTo(PriorityMapEntry<KeyType, V> o) {
            return priority - o.priority;
        }
    }
}
