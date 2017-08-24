package com.sauce.core.engine;

/**
 * Created by John Crockett.
 */
public abstract class System implements Comparable<System> {

    private int prio;

    public System(int priority){
        prio = priority;
    }

    public abstract void update(int delta);

    @Override
    public int compareTo(System o) {
        return prio - o.prio;
    }
}
