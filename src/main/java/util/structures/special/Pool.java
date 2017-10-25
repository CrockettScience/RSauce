package util.structures.special;

import util.structures.nonsaveable.Queue;

/**
 * Created by John Crockett.
 */
public abstract class Pool<T extends Pool.Poolable> {

    private Queue<T> pool;
    private int max;

    public Pool(int maxSize){
        pool = new Queue<>();
        max = maxSize;
    }

    public Pool(){
        pool = new Queue<>();
        max = 100;
    }

    public void toss(T element){
        if(pool.size() < max)
            pool.enqueue(element);

        element.clean();

    }

    public T grab(){
        return pool.isEmpty() ? newElement() : pool.dequeue();
    }

    protected abstract T newElement();

    public interface Poolable{
        void clean();
    }
}
