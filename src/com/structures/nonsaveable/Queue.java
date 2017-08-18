package com.structures.nonsaveable;

/**
 * Created by John Crockett.
 */

public class Queue<T> {
    private QueueNode<T> last;
    private QueueNode<T> first;

    public Queue() {
        clear();
    }

    public boolean isEmpty() {
        return first.next == last;
    }

    public void enqueue(T element) {
        last.data = element;
        last.next = new QueueNode<T>(null, null);
        last = last.next;
    }

    public T dequeue() {
        T element = first.next.data;

        if (!isEmpty()) {
            first.next = first.next.next;
        }

        return element;
    }

    public void clear() {
        last = new QueueNode<T>(null, null);
        first = new QueueNode<T>(null, last);
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
