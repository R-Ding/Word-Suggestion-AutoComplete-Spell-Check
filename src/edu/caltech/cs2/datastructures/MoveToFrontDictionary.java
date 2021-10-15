package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDictionary;

import java.util.Iterator;

public class MoveToFrontDictionary<K, V> implements IDictionary<K,V> {
    private Node<K, V> head;
    private int size;

    public MoveToFrontDictionary() {
        this.head = null;
        this.size = 0;
    }

    private V addFront(K key, V value) {
        Node<K, V> newNode = new Node<K, V>(key, value);
        newNode.next = this.head;
        this.head = newNode;
        this.size++;
        return value;
    }

    @Override
    public V remove(K key) {
        Node<K, V> current = this.head;
        if (this.head == null) {
            return null;
        }
        else if (current.key.equals(key)) {
            V val = current.value;
            this.head = current.next;
            size--;
            return val;
        }
        else {
            while (current.next != null) {
                if (current.next.key.equals(key)) {
                    Node<K, V> node = current.next;
                    current.next = node.next;
                    size--;
                    return node.value;
                }
                else {
                    current = current.next;
                }
            }
        }
        return null;
    }

    @Override
    // move to front
    public V put(K key, V value) {
        V val = remove(key);
        addFront(key, value);
        return val;
    }

    //move to front
    public V get(K key) {
        V val = remove(key);
        if (val == null) {
            return null;
        }
        addFront(key, val);
        return val;
    }


    @Override
    //move to front
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(V value) {
        Node<K, V> current = this.head;
        if (this.head == null) {
            return false;
        }
        else if (current.value.equals(value)) {
            return true;
        }
        else {
            while (current.next != null) {
                if (current.next.equals(value)) {
                    Node<K, V> node = current.next;
                    current.next = node.next;
                    size--;
                    addFront(node.key, node.value);
                    return true;
                }
                else {
                    current = current.next;
                }
            }
        }
        return false;
    }

    @Override
    public int size() { return this.size; }

    @Override
    public ICollection<K> keySet() {
        ICollection<K> keys = new LinkedDeque<K>();
        Node<K, V> current = this.head;
        while (current != null) {
            keys.add(current.key);
            current = current.next;
        }
        return keys;
    }

    @Override
    public ICollection<V> values() {
        ICollection<V> values = new LinkedDeque<V>();
        Node<K, V> current = this.head;
        while (current != null) {
            values.add(current.value);
            current = current.next;
        }
        return values;
    }

    public class MoveToFrontDictionaryIterator implements Iterator<K> {
        private MoveToFrontDictionary.Node<K, V> current = MoveToFrontDictionary.this.head;

        public boolean hasNext() {
            return this.current != null;
        }

        public K next() {
            MoveToFrontDictionary.Node<K, V> nextNode = this.current;
            this.current = this.current.next;
            return nextNode.key;
        }
    }

    @Override
    public Iterator<K> iterator() {
        return new MoveToFrontDictionary.MoveToFrontDictionaryIterator();
    }

    private static class Node<K, V> {
        public final K key;
        public final V value;
        public Node<K, V> next;

        public Node(K key, V value) {
            this(key, value, null);
        }

        public Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}
