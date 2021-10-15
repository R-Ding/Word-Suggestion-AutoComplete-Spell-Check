package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IStack;

import java.util.Iterator;

// Your data structure should initially be empty and should add/remove nodes
//         one at a time as needed.

public class LinkedDeque<E> implements IDeque<E>, IQueue<E>, IStack<E> {
    private Node<E> head;
    private Node<E> tail;
    private int size;

    // initializes an empty linkedDeque
    public LinkedDeque() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    private static class Node<E> {
        public final E data;
        public Node<E> next;
        public Node<E> prev;

        public Node(E data) {
            this(data, null, null);
        }

        public Node(E data, Node<E> next, Node<E> prev) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    @Override
    public void addFront(E e) {
        if (this.size == 0) {
            Node<E> newNode = new Node<E>(e);
            this.head = newNode;
            this.tail = newNode;
//            this.head = new Node<E>(e);
//            this.tail = new Node<E>(e);
        }
        else if (this.size == 1) {
            Node<E> newNode = new Node<E>(e);
            this.head = newNode;
            this.head.next = this.tail;
            this.tail.prev = this.head;
//            this.head = new Node<E>(e);
//            this.head.next = this.tail;
//            this.tail.prev = this.head;
        }
        else {
            Node<E> newNode = new Node<E>(e);
            newNode.next = this.head;
            this.head.prev = newNode;
            this.head = newNode;
        }
        this.size++;
    }

    @Override
    public void addBack(E e) {
        if (this.size == 0) {
            Node<E> newNode = new Node<E>(e);
            this.head = newNode;
            this.tail = newNode;
//            this.head = new Node<E>(e);
//            this.tail = this.head;
        }
        else if (this.size == 1) {
            Node<E> newNode = new Node<E>(e);
            this.tail = newNode;
            this.head.next = this.tail;
            this.tail.prev = this.head;
//            this.tail = new Node<E>(e);
//            this.head.next = this.tail;
//            this.tail.prev = this.head;
        }
        else {
            Node<E> newNode = new Node<E>(e);
            newNode.prev = this.tail;
            this.tail.next = newNode;
            this.tail = newNode;
        }
        this.size++;
    }

    @Override
    public E removeFront() {
        if (this.size == 0) {
            return null;
        }
        E front = this.head.data;
        if (this.size == 1) {
            this.head = null;
            this.tail = null;
        }
        else {
            this.head = this.head.next;
            this.head.prev = null;
        }
        this.size--;
        return front;
    }

    @Override
    public E removeBack() {
        if (this.size == 0) {
            return null;
        }
        E back = this.tail.data;
        if (this.size == 1) {
            this.head = null;
            this.tail = null;
        }
        else {
            this.tail = this.tail.prev;
            this.tail.next = null;
        }
        this.size--;
        return back;
    }

    @Override
    public boolean enqueue(E e) {
        this.addBack(e);
        return true;
    }

    @Override
    public E dequeue() {
        return this.removeFront();
    }

    @Override
    public boolean push(E e) {
        this.addFront(e);
        return true;
    }

    @Override
    public E pop() {
        return removeFront();
    }

    @Override
    public E peek() {
        return peekFront();
    }

    @Override
    public E peekFront() {
        if (this.size == 0) {
            return null;
        }
        return this.head.data;
    }

    @Override
    public E peekBack() {
        if (this.size == 0) {
            return null;
        }
        return this.tail.data;
    }

    public class LinkedDequeIterator implements Iterator<E> {
        private Node<E> current = LinkedDeque.this.head;

        public boolean hasNext() {
            return this.current != null;
        }

        public E next() {
            Node<E> nextNode = current;
            current = current.next;
            return nextNode.data;
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new LinkedDequeIterator();
    }

    public String toString() {
        if (this.head == null) {
            return "[]";
        }

        Node<E> current = this.head;
        String result = "";
        while (current != null && current.next != null) {
            result += current.data + ", ";
            current = current.next;
        }

        return "[" + result + current.data + "]";
    }

    @Override
    public int size() {
        return this.size;
    }
}
