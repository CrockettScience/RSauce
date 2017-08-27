package com.util.structures.special;

import com.util.structures.nonsaveable.Queue;

/**
 * Created by John Crockett.
 */
public abstract class RecyclePool<T extends RecyclePool.Poolable> {

    private Queue<T> pool;
    private int max;

    public RecyclePool(int maxSize){
        pool = new Queue<>();
        max = maxSize;
    }

    public RecyclePool(){
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
