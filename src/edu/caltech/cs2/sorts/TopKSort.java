package edu.caltech.cs2.sorts;

import edu.caltech.cs2.datastructures.MinFourHeap;
import edu.caltech.cs2.interfaces.IPriorityQueue;

public class TopKSort {
    /**
     * Sorts the largest K elements in the array in descending order.
     * @param array - the array to be sorted; will be manipulated.
     * @param K - the number of values to sort
     * @param <E> - the type of values in the array
     */
    public static <E> void sort(IPriorityQueue.PQElement<E>[] array, int K) {
        if (K < 0) {
            throw new IllegalArgumentException("K cannot be negative!");
        }
        if(array.length == 0){
            return;
        }
        K = Math.min(K, array.length);
        if(K == 0){
            for(int i = 0; i < array.length; i++){
                array[i] = null;
            }
            return;
        }
        MinFourHeap<E> heap = new MinFourHeap<>();
        for (int i = 0; i < K; i++) {
            heap.enqueue(array[i]);
        }
        for(int j = K; j < array.length; j++) {
            if (array[j].priority > heap.peek().priority) {
                heap.dequeue();
                heap.enqueue(array[j]);
            }
        }

        for (int i = 1; i <= K; i++) {
            array[K - i] = heap.dequeue();
        }
        for(int p = K; p < array.length; p++){
            array[p] = null;
        }
        return;
    }
}
