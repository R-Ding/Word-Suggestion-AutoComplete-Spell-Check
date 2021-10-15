package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.IPriorityQueue;

import java.util.Iterator;

import static java.lang.Math.min;

public class MinFourHeap<E> implements IPriorityQueue<E> {

    private static final int DEFAULT_CAPACITY = 5;

    private int size;
    private PQElement<E>[] data;
    private IDictionary<E, Integer> keyToIndexMap;

    /**
     * Creates a new empty heap with DEFAULT_CAPACITY.
     */
    public MinFourHeap() {
        this.size = 0;
        this.data = new PQElement[DEFAULT_CAPACITY];
        this.keyToIndexMap = new ChainingHashDictionary<>(MoveToFrontDictionary::new);
    }

    private void percolateUp(int n) {
        while (n > 0 && (this.data[n].priority < this.data[getParent(n)].priority)) {
            int p = getParent(n);
            IPriorityQueue.PQElement<E> first = this.data[n];
            IPriorityQueue.PQElement<E> second = this.data[p];

            this.data[n] = second;
            keyToIndexMap.put(second.data, n);
            this.data[p] = first;
            keyToIndexMap.put(first.data, p);

            n = p;
        }
    }

    private void percolateDown(int n) {
        boolean flag = true;
        while(flag){
            if (hasChildren(n)) {
                int min = getMinChild(n);
                if (this.data[n].priority > this.data[min].priority) {
                    IPriorityQueue.PQElement<E> first = this.data[n];
                    IPriorityQueue.PQElement<E> second = this.data[min];
                    this.data[n] = second;
                    keyToIndexMap.put(second.data, n);
                    this.data[min] = first;
                    keyToIndexMap.put(first.data, min);
                    n = min;
                } else {
                    flag = false;
                }
            }
            else {
                flag = false;
            }
        }
    }

    private int[] getChildren(int n) {
        int[] children = new int[4];
        for (int i = 0; i < 4; i++) {
            children[i] = 4*(n+1) - (3 - i);
        }
        return children;
    }

    private int getMinChild(int n) {
        int[] children = getChildren(n);
        int minChild = children[0];
        for (int i = 1; i < 4; i++) {
            if (this.data[children[i]] != null && this.data[children[i]].priority < this.data[minChild].priority) {
                minChild = children[i];
            }
        }
        return minChild;
    }

    private int getParent(int n) {
        return (n - 1) / 4;
    }

    private boolean hasChildren(int n) {
        return (this.size - 1 >= 4*(n+1) - 3);
    }

    /**
     * Increase the priority of the key at idx
     * @param key - the new key with the new priority
     */
    @Override
    //percolate down
    public void increaseKey(IPriorityQueue.PQElement<E> key) {
        if (!this.keyToIndexMap.containsKey(key.data)) {
            throw new IllegalArgumentException("Key not present");
        }

        int n = this.keyToIndexMap.get(key.data);
        this.data[n] = new IPriorityQueue.PQElement<E>(key.data, key.priority);
        percolateDown(n);
    }

    @Override
    public void decreaseKey(IPriorityQueue.PQElement<E> key) {
        if (!this.keyToIndexMap.containsKey(key.data)) {
            throw new IllegalArgumentException("Key not present");
        }
        int n = this.keyToIndexMap.get(key.data);
        this.data[n] = new IPriorityQueue.PQElement<E>(key.data, key.priority);
        percolateUp(n);
    }

    @Override
    public boolean enqueue(IPriorityQueue.PQElement<E> epqElement) {
        if (epqElement == null) {
            return false;
        }

        if (this.keyToIndexMap.containsKey(epqElement.data)) {
           throw new IllegalArgumentException("element already exists or is null");
        }

        if (this.data.length == this.size) {
            PQElement<E>[] data2 = new PQElement[DEFAULT_CAPACITY * this.size];
            for(int i = 0; i < this.data.length; i++ ){
                data2[i] = this.data[i];
            }
            this.data = data2;
        }
        size++;
        int n = this.size - 1;
        this.data[n] = epqElement;
        this.keyToIndexMap.put(epqElement.data, n);
        percolateUp(n);

        return true;
    }

    @Override
    public IPriorityQueue.PQElement<E> dequeue() {
        if (this.size == 0) {
            return null;
        }

        IPriorityQueue.PQElement<E> output = this.data[0];
        this.keyToIndexMap.remove(this.data[0].data);
        this.data[0] = this.data[size - 1];
        this.data[this.size-1] = null;
        int n = 0;
        size--;
        percolateDown(n);
        return output;
    }

    @Override
    public IPriorityQueue.PQElement<E> peek() {
        if (this.size == 0) {
            return null;
        }
        return data[0];
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Iterator<IPriorityQueue.PQElement<E>> iterator() {
        return null;
    }
}