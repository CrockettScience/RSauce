package sauce.util.structures.nonsaveable;

/**
 * Created by John Crockett.
 */

public class Map<K, V> {
    private static final int DEFAULT_TABLE_SIZE = 101;

    protected MapEntry<K, V>[] mapTable;
    private int occupied;
    private int currentSize;

    public Map() {
        clear();
    }

    public int size() {
        return currentSize;
    }

    public boolean isEmpty() {
        return currentSize == 0;
    }

    public boolean containsKey(K key) {
        if(key == null){
            return false;
        }

        int currentPos = findPos(key);
        K other = mapTable[currentPos] == null ? null : mapTable[currentPos].key;

        return key.equals(other) && mapTable[currentPos].isActive;
    }

    public boolean put(K key, V value) {
        if(key == null)
            return false;

        return add(constructMapEntry(key, value));
    }

    public V get(K key) {
        if(key == null)
            return null;

        return mapTable[findPos(key)] == null ? null : mapTable[findPos(key)].value;
    }

    public Set<K> keySet() {
        Set<K> set = new Set<>();

        for (MapEntry<K, V> entry : mapTable) {
            if (entry != null)
                set.add(entry.key);
        }


        return set;
    }

    public Set<V> valueSet() {
        Set<V> set = new Set<>();

        for (MapEntry<K, V> entry : mapTable) {
            if (entry != null)
                set.add(entry.value);
        }


        return set;
    }

    protected boolean add(MapEntry<K, V> entry) {
        int currentPos = findPos(entry.key);

        if (mapTable[currentPos] == null)
            occupied++;

        mapTable[currentPos] = entry;
        currentSize++;

        if (occupied > mapTable.length / 2)
            rehash();

        return true;
    }

    public V remove(K key) {
        int currentPos = findPos(key);

        if (mapTable[currentPos] == null || !mapTable[currentPos].isActive)
            return null;

        mapTable[currentPos].isActive = false;
        currentSize--;

        return mapTable[currentPos].value;
    }

    public void clear() {
        allocateArray(DEFAULT_TABLE_SIZE);
        currentSize = 0;
        occupied = 0;
    }

    private int findPos(K key) {
        int offset = 1;
        int currentPos = Math.abs(key.hashCode() % mapTable.length);

        while (mapTable[currentPos] != null) {
            if (key.equals(mapTable[currentPos].key))
                break;

            currentPos += offset;
            offset += 2;

            if (currentPos >= mapTable.length)
                currentPos -= mapTable.length;
        }

        return currentPos;
    }

    private void rehash() {
        MapEntry[] oldArray = mapTable;

        allocateArray(nextPrime(4 * currentSize));
        currentSize = 0;
        occupied = 0;

        for (MapEntry entry : oldArray) {
            if (entry != null) {
                add(entry);
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
        mapTable = (MapEntry<K, V>[]) new MapEntry[nextPrime(arraySize)];
    }

    protected MapEntry<K, V> constructMapEntry(K key, V val) {
        return new MapEntry(key, val);
    }

    protected class MapEntry<K, V> {
        public V value;
        public K key;
        boolean isActive;

        protected MapEntry(K k, V v) {
            key = k;
            value = v;
            isActive = true;
        }
    }
}
