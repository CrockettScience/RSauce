package com.util.structures.nonsaveable;

/**
 * Created by John Crockett.
 */

public class Queue<T> {
    private QueueNode<T> last;
    private QueueNode<T> first;
    private int size;

    public Queue() {
        clear();
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size(){
        return size;
    }

    public void enqueue(T element) {
        last.data = element;
        last.next = new QueueNode<>(null, null);
        last = last.next;
        size++;
    }

    public T dequeue() {
        T element = first.next.data;

        if (!isEmpty()) {
            first.next = first.next.next;
        }

        size--;

        return element;
    }

    public void clear() {
        last = new QueueNode<T>(null, null);
        first = new QueueNode<T>(null, last);
        size = 0;
    }

    private class QueueNode<T> {
        private T data;
        private QueueNode<T> next;

        public QueueNode(T element, QueueNode<T> nextNode) {

            data = element;
            next = nextNode;

        }
    }
}
