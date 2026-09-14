package edutrack.core;

/**
 * Hand-built Hash Map using separate chaining with zero java.util.* dependencies.
 * Provides O(1) expected get, put, and remove operations.
 */
public class MyHashMap<K, V> {
    public static class Entry<K, V> {
        public final K key;
        public V value;
        public Entry<K, V> next;

        public Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private Entry<K, V>[] table;
    private int size;
    private static final int INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    @SuppressWarnings("unchecked")
    public MyHashMap() {
        this.table = (Entry<K, V>[]) new Entry[INITIAL_CAPACITY];
        this.size = 0;
    }

    private int hash(K key) {
        if (key == null) return 0;
        int h = key.hashCode();
        h ^= (h >>> 16);
        return (h & 0x7fffffff) % table.length;
    }

    public void put(K key, V value) {
        int index = hash(key);
        Entry<K, V> head = table[index];
        while (head != null) {
            if (keyEquals(head.key, key)) {
                head.value = value;
                return;
            }
            head = head.next;
        }

        Entry<K, V> newEntry = new Entry<>(key, value, table[index]);
        table[index] = newEntry;
        size++;

        if ((float) size / table.length >= LOAD_FACTOR) {
            resize();
        }
    }

    public V get(K key) {
        int index = hash(key);
        Entry<K, V> head = table[index];
        while (head != null) {
            if (keyEquals(head.key, key)) {
                return head.value;
            }
            head = head.next;
        }
        return null;
    }

    public boolean containsKey(K key) {
        int index = hash(key);
        Entry<K, V> head = table[index];
        while (head != null) {
            if (keyEquals(head.key, key)) {
                return true;
            }
            head = head.next;
        }
        return false;
    }

    public V remove(K key) {
        int index = hash(key);
        Entry<K, V> head = table[index];
        Entry<K, V> prev = null;
        while (head != null) {
            if (keyEquals(head.key, key)) {
                if (prev != null) {
                    prev.next = head.next;
                } else {
                    table[index] = head.next;
                }
                size--;
                return head.value;
            }
            prev = head;
            head = head.next;
        }
        return null;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public MyArrayList<K> keys() {
        MyArrayList<K> list = new MyArrayList<>(size);
        for (int i = 0; i < table.length; i++) {
            Entry<K, V> curr = table[i];
            while (curr != null) {
                list.add(curr.key);
                curr = curr.next;
            }
        }
        return list;
    }

    public MyArrayList<V> values() {
        MyArrayList<V> list = new MyArrayList<>(size);
        for (int i = 0; i < table.length; i++) {
            Entry<K, V> curr = table[i];
            while (curr != null) {
                list.add(curr.value);
                curr = curr.next;
            }
        }
        return list;
    }

    public MyArrayList<Entry<K, V>> entries() {
        MyArrayList<Entry<K, V>> list = new MyArrayList<>(size);
        for (int i = 0; i < table.length; i++) {
            Entry<K, V> curr = table[i];
            while (curr != null) {
                list.add(curr);
                curr = curr.next;
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<K, V>[] oldTable = table;
        table = (Entry<K, V>[]) new Entry[oldTable.length * 2];
        size = 0;
        for (int i = 0; i < oldTable.length; i++) {
            Entry<K, V> curr = oldTable[i];
            while (curr != null) {
                put(curr.key, curr.value);
                curr = curr.next;
            }
        }
    }

    private boolean keyEquals(K k1, K k2) {
        if (k1 == k2) return true;
        if (k1 == null || k2 == null) return false;
        return k1.equals(k2);
    }
}
