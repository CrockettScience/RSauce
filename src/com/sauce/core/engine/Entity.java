package com.sauce.core.engine;

import com.structures.nonsaveable.Map;

/**
 * Created by John Crockett.
 */
public class Entity implements Comparable<Entity>{
    private Map<Class<? extends Component>, Component> componentMap = new Map<>();
    private Integer zPos;

    public Entity(int zPosition){
        zPos = zPosition;
    }

    public Entity(){
        zPos = null;
    }

    public boolean addComponent(Component c){
        if(!componentMap.containsKey(c.getClass())) {
            componentMap.put(c.getClass(), c);
            return true;
        }

        return false;
    }

    public boolean removeComponent(Class<? extends Component> cClass){
        if(componentMap.containsKey(cClass)){
            componentMap.remove(cClass);
            return true;
        }

        return false;
    }

    public void clearComponents(){
        componentMap.clear();
    }

    protected Map<Class<? extends Component>, Component> getComponentMap(){
        return componentMap;
    }

    @Override
    public int compareTo(Entity o) {
        if(zPos == null){
            if(o.zPos == null)
                return 0;

            return 1;
        }

        if(o.zPos == null)
            return -1;

        return zPos - o.zPos;
    }
}
