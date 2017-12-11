package sauce.core;

/**
 * Created by John Crockett.
 */
abstract class EngineSystem implements Comparable<EngineSystem>{
    int prio;

    public EngineSystem(int priority){
        prio = priority;
    }

    public EngineSystem(){
        prio = 0;
    }

    public abstract void addedToEngine();
    public abstract void update(double delta);
    public abstract void removedFromEngine();

    @Override
    public int compareTo(EngineSystem o) {
        return prio - o.prio;
    }
}
