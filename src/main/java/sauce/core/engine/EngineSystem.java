package sauce.core.engine;

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

    public void addedToEngine(){

    }

    public abstract void update(double delta);

    public void removedFromEngine(){

    }

    @Override
    public int compareTo(EngineSystem o) {
        return prio - o.prio;
    }
}