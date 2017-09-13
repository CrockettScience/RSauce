package com.sauce.core.engine;

import com.util.structures.nonsaveable.Map;

/**
 * Created by John Crockett.
 */
public class Entity{
    private Map<Class<? extends Component>, Component> componentMap = new Map<>();

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

    public <T extends Component>T getComponent(Class<T> c){
        return (T) componentMap.get(c);
    }

    public boolean hasComponent(Class<? extends Component> c){
        return componentMap.containsKey(c);
    }

    public boolean hasAll(Class<? extends Component>... comps){
        for(Class<? extends Component> c : comps){
            if(!componentMap.containsKey(c))
                return false;
        }

        return true;
    }

    public boolean hasNone(Class<? extends Component>... comps){
        for(Class<? extends Component> c : comps){
            if(componentMap.containsKey(c))
                return false;
        }

        return true;
    }

    protected Map<Class<? extends Component>, Component> getComponentMap(){
        return componentMap;
    }

    public void dispose(){}
}
