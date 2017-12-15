package sauce.util.structures.threadsafe;


public class ThreadSafeQueue<T> {
    private QueueNode<T> last;
    private QueueNode<T> first;
    private int size;

    public ThreadSafeQueue() {
        clear();
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size(){
        return size;
    }

    public synchronized void enqueue(T element) {
        last.data = element;
        last.next = new QueueNode<>(null, null);
        last = last.next;
        size++;
    }

    public synchronized T dequeue() {
        T element = first.next.data;

        if (!isEmpty()) {
            first.next = first.next.next;
        }

        size--;

        return element;
    }

    public synchronized void clear() {
        last = new QueueNode<>(null, null);
        first = new QueueNode<>(null, last);
        size = 0;
    }

    private class QueueNode<T> {
        private T data;
        private QueueNode<T> next;

        private QueueNode(T element, QueueNode<T> nextNode) {

            data = element;
            next = nextNode;

        }
    }
}
