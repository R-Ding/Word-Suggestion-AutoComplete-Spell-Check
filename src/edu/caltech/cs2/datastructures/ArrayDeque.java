package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IStack;

import java.util.Iterator;

public class ArrayDeque<E> implements IDeque<E>, IQueue<E>, IStack<E> {
    private E[] data;
    private static final int DEFAULT_CAPACITY = 10;
    private static final int GROW_FACTOR = 2;
    private int size;

    public ArrayDeque() {
        this(DEFAULT_CAPACITY);
    }

    public ArrayDeque(int initialCapacity) {
        this.data = (E[])new Object[initialCapacity];
    }

    @Override
    public void addFront(E e) {
        ensureCapacity(this.size + 1);
        for (int i = this.size; i > 0; i--) {
            this.data[i] = this.data[i - 1];
        }
        this.data[0] = e;
        this.size++;
    }

    @Override
    public void addBack(E e) {
        ensureCapacity(this.size + 1);
        this.data[size] = e;
        this.size++;
    }

    @Override
    public E removeFront() {
        if (this.size == 0) {
            return null;
        }
        E front = this.data[0];
        for (int i = 0; i < this.size - 1; i++) {
            this.data[i] = this.data[i + 1];
        }
        this.size--;
        return front;
    }

    @Override
    public E removeBack() {
        if (this.size == 0) {
            return null;
        }
        this.size--;
        return this.data[this.size];
    }

    // front of queue same as top of stack
    @Override
    public boolean enqueue(E e) {
        this.addFront(e);
        return true;
    }

    @Override
    public E dequeue() {
        return this.removeBack();
    }

    @Override
    public boolean push(E e) {
        this.addBack(e);
        return true;
    }

    @Override
    public E pop() {
        return removeBack();
    }

    @Override
    public E peek() {
        return peekBack();
    }

    @Override
    public E peekFront() {
        if (this.size == 0) {
            return null;
        }
        return this.data[0];
    }

    @Override
    public E peekBack() {
        if (this.size == 0) {
            return null;
        }
        return this.data[this.size - 1];
    }

    @Override
    public Iterator<E> iterator() {
        return new ArrayDequeIterator();
    }

    public class ArrayDequeIterator implements Iterator<E> {
        private int idx;

        public ArrayDequeIterator() {
            this.idx = -1;
        }

        public boolean hasNext() {
            return this.idx < ArrayDeque.this.size - 1;
        }

        public E next() {
            this.idx++;
            return ArrayDeque.this.data[this.idx];
        }
    }

    @Override
    public int size() {
        return this.size;
    }

    private void ensureCapacity(int size) {
        if (this.capacity() < size) {
            E[] newData = (E[])new Object[(int)(this.capacity()*GROW_FACTOR)];
            for (int i = 0; i < this.size; i++) {
                newData[i] = this.data[i];
            }
            this.data = newData;
        }
    }

    private int capacity() {
        return this.data.length;
    }

    public String toString() {
        if (this.size == 0) {
            return "[]";
        }

        String result = "[";
        for (int i = 0; i < this.size; i++) {
            result += this.data[i] + ", ";
        }

        result = result.substring(0, result.length() - 2);
        return result + "]";
    }
}

