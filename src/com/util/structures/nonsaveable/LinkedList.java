package com.util.structures.nonsaveable;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * Created by John Crockett.
 */

public class LinkedList<T> implements Iterable<T> {

    private LinkedListNode header;
    private LinkedListNode footer;
    private int size;

    public LinkedList() {
        header = new LinkedListNode();
        footer = new LinkedListNode();

        header.next = footer;
        footer.next = header;
    }

    public final void clear() {
        LinkedListNode current = header;
        while (current.next != footer) {
            current.prev = null;
            current = current.next;
        }
        footer.prev = header;
        header.next = footer;

    }

    public int size(){
        return size;
    }

    public boolean isEmpty(){
        return size == 0;
    }

    public T get(int i) {
        return findNode(i).element;
    }

    public void set(int i, T element) {
        findNode(i).element = element;
    }

    public void add(T e) {
        LinkedListNode<T> node = new LinkedListNode<>();

        node.element = e;
        node.prev = footer.prev;
        node.next = footer;
        footer.prev.next = node;
        footer.prev = node;
    }

    public T remove(int i) {
        LinkedListNode<T> node = findNode(i);

        node.next.prev = node.prev;
        node.prev.next = node.next;

        size--;

        return node.element;
    }

    private LinkedListNode<T> findNode(int i) {
        if (size == 0) {
            throw new ArrayIndexOutOfBoundsException();
        }

        LinkedListNode current;
        int index;

        if (i < size / 2) {
            current = header.next;
            index = 0;

            while (index < i) {
                current = current.next;
                index++;
            }

            return current;
        }

        current = footer.prev;
        index = size - 1;

        while (index > i) {
            current = current.prev;
            index--;
        }

        return current;

    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListIterator();
    }

    public ListIterator listIterator() {
        return (ListIterator) iterator();
    }

    private class LinkedListIterator<T> implements ListIterator<T> {

        private int index;
        private LinkedListNode<T> current;

        public LinkedListIterator() {
            current = LinkedList.this.header.next;
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return current != LinkedList.this.footer;
        }

        @Override
        public T next() {
            T element = current.element;
            current = current.next;
            index++;

            return element;
        }

        @Override
        public boolean hasPrevious() {
            return current.prev != LinkedList.this.header;
        }

        @Override
        public T previous() {
            current = current.prev;
            index--;

            return current.element;
        }

        @Override
        public int nextIndex() {
            return index;
        }

        @Override
        public int previousIndex() {
            return index - 1;
        }

        @Override
        public void remove() {
            current.next.prev = current.prev;
            current.prev.next = current.next;
        }

        @Override
        public void set(T e) {
            current.element = e;
        }

        @Override
        public void add(T e) {
            LinkedListNode node = new LinkedListNode();
            node.element = e;

            node.next = current;
            node.prev = current.prev;
            current.prev.next = node;
            current.prev = node;

            LinkedList.this.size++;
            index++;
        }

    }

    private class LinkedListNode<T> {
        private T element;
        private LinkedListNode next;
        private LinkedListNode prev;
    }
}
