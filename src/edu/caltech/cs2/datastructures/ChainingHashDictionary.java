package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDictionary;

import java.util.Iterator;
import java.util.function.Supplier;

public class ChainingHashDictionary<K, V> implements IDictionary<K, V> {
    private Supplier<IDictionary<K, V>> chain;
    private IDictionary<K, V>[] buckets = new IDictionary[257];
    private int size;
    private int resize;
    private int[] primes = {529, 1031, 2053, 4099, 8209, 16411, 32771, 65537, 131101, 262147};

    public ChainingHashDictionary(Supplier<IDictionary<K, V>> chain) {
        this.chain = chain;
        for (int i = 0; i < this.buckets.length; i++) {
            this.buckets[i] = chain.get();
        }
        this.size = 0;
        this.resize = 0;
    }

    /**
     * @param key
     * @return value corresponding to key
     */
    @Override
    public V get(K key) {
        int hash_code = key.hashCode();
        int idx = hash_code % this.buckets.length;
        if (idx < 0) {
            idx += this.buckets.length;
        }
        return this.buckets[idx].get(key);
    }

    @Override
    public V remove(K key) {
        if (containsKey(key)) {
            this.size--;
        }
        int hash_code = key.hashCode();
        int idx = hash_code % this.buckets.length;
        if (idx < 0) {
            idx += this.buckets.length;
        }
        return this.buckets[idx].remove(key);
    }

    @Override
    public V put(K key, V value) {
        if ((this.size + 1) / this.buckets.length > 1) {
            rehash();
        }
        int hash_code = key.hashCode();
        if (!containsKey(key)) {
            this.size++;
        }
        int idx = hash_code % this.buckets.length;
        if (idx < 0) {
            idx += this.buckets.length;
        }
        return this.buckets[idx].put(key, value);
    }

    private void rehash() {
        ICollection<K> keys = keySet();
        IDictionary<K, V>[] newBuckets = new IDictionary[this.primes[this.resize]];
        for (int i = 0; i < newBuckets.length; i++) {
            newBuckets[i] = chain.get();
        }
        for (K k : keys) {
            V val = get(k);
            int hash_code = k.hashCode();
            int idx = hash_code % newBuckets.length;
            if (idx < 0) {
                idx += newBuckets.length;
            }
            newBuckets[idx].put(k, val);
        }
        this.buckets = newBuckets;
        this.resize++;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    /**
     * @param value
     * @return true if the HashDictionary contains a key-value pair with
     * this value, and false otherwise
     */
    @Override
    public boolean containsValue(V value) {
        return values().contains(value);
    }

    /**
     * @return number of key-value pairs in the HashDictionary
     */
    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ICollection<K> keySet() {
        ICollection<K> keys = new LinkedDeque<K>();
        for (int i = 0; i < this.buckets.length;i++) {
            Iterator iterator = this.buckets[i].iterator();
            while (iterator.hasNext()) {
                K k = (K) iterator.next();
                    keys.add(k);
            }
        }
        return keys;
    }

    @Override
    public ICollection<V> values() {
        ICollection<V> values = new LinkedDeque<V>();
        for (int i = 0; i < this.buckets.length;i++) {
            Iterator iterator = this.buckets[i].iterator();
            while (iterator.hasNext()) {
                V v = buckets[i].get((K) iterator.next());
                values.add(v);
            }
        }
        return values;
    }

    /**
     * @return An iterator for all entries in the HashDictionary
     */
    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }
}
