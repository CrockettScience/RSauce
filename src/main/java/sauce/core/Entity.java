package sauce.core;

import util.RSauceLogger;
import util.structures.nonsaveable.Map;

/**
 * Created by John Crockett.
 */
public class Entity{
    private Map<Class<? extends Component>, Component> componentMap = new Map<>();
    private boolean isInEngine = false;

    public boolean addComponent(Component c){
        if(!componentMap.containsKey(c.getClass())) {
            if(c.addedToEntity(this)){
                componentMap.put(c.getClass(), c);
                notifyEngine();
                return true;
            }

            c.removedFromEntity(this);
            return false;
        }

        RSauceLogger.printWarningln(c.getClass().getSimpleName() + " could not be added to the Entity, the Entity already contains a component of that type.");
        return false;
    }

    public boolean removeComponent(Class<? extends Component> cClass){
        if(componentMap.containsKey(cClass)){
            componentMap.remove(cClass).removedFromEntity(this);
            notifyEngine();
            return true;
        }

        return false;
    }

    public void clearComponents(){
        for(Component c : componentMap.valueSet()){
            c.removedFromEntity(this);
        }

        componentMap.clear();
        notifyEngine();
    }

    public <T extends Component> T getComponent(Class<T> c){
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

    protected void addedToEngine(){
        isInEngine = true;
    }

    protected void removedFromEngine(){
        isInEngine = false;
    }

    void notifyEngine(){
        if(isInEngine)
            Engine.entityChangedComponents(this);
    }

    public void dispose(){
        for(Component c : componentMap.valueSet())
            c.dispose();
    }
}
