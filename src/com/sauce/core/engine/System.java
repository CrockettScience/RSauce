package com.sauce.core.engine;

/**
 * Created by John Crockett.
 */
abstract class System implements Comparable<System> {
    int prio;

    public System(int priority){
        prio = priority;
    }

    public abstract void addedToEngine(Engine engine);
    public abstract void update(int delta);
    public abstract void removedFromEngine(Engine engine);

    @Override
    public int compareTo(System o) {
        return prio - o.prio;
    }
}
