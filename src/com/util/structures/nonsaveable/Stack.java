package com.util.structures.nonsaveable;

/**
 * Created by John Crockett.
 */

public class Stack<T> {
    private StackNode<T> bottom = new StackNode<T>(null, null);
    private StackNode<T> current;

    public Stack() {
        clear();
    }

    public boolean isEmpty() {
        return current == bottom;
    }

    public void push(T data) {
        current = new StackNode<T>(data, current);
    }

    public void pop() {
        if (!isEmpty()) {
            current = current.next;
        }
    }

    public T top() {
        return current.data;
    }

    public T popAndTop() {
        T data = top();
        pop();
        return data;
    }

    public void clear() {
        bottom.next = null;
        current = bottom;
    }

    private class StackNode<T> {
        private StackNode<T> next;
        private T data;

        public StackNode(T element, StackNode<T> node) {
            next = node;
            data = element;
        }
    }
}
