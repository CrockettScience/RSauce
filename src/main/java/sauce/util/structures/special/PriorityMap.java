package sauce.util.structures.special;

import sauce.util.structures.nonsaveable.Map;
import sauce.util.structures.nonsaveable.PriorityQueue;
import sauce.util.structures.nonsaveable.Queue;
import sauce.util.structures.nonsaveable.Set;

/**
 * Created by John Crockett.
 * The same as a PriorityQueue<T>, but with the functionality of Map-like linear access and modification.
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

        PriorityMapEntry<K, V> entry = queue.removeNext();
        poolQueue.enqueue(entry);

        if(queue.isEmpty()) {
            recycle();
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
        return map.containsKey(key) && map.get(key).isActive;

    }

    private void recycle(){
        while(!poolQueue.isEmpty()){
            queue.add(poolQueue.dequeue());
        }
    }

    private void skipToNextActiveElement(){
        while(!queue.isEmpty() && !queue.getNext().isActive) {
            queue.removeNext();
        }

        if(queue.isEmpty())
            recycle();
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
