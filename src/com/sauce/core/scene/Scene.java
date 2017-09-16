package com.sauce.core.scene;


import com.sauce.core.Project;
import com.sauce.core.engine.Engine;
import com.sauce.core.engine.Entity;
import com.util.RSauceLogger;
import com.util.structures.nonsaveable.Map;

/**
 *
 * @author Jonathan Crockett
 */
public abstract class Scene{
    private static final Engine ENGINE = Engine.getEngine();

    private final Map<Class<? extends Attribute>, Attribute> attributes = new Map<>();
    private Map<String, Entity> entities = new Map<>();
    private Map<String, Boolean> isInEngine = new Map<>();

    protected abstract void loadResources();
    protected abstract void destroyResources();
    protected abstract void sceneMain();
    
    public void addAttribute(Attribute attr){
        attributes.put(attr.getClass(), attr);
    }
    
    public <AnyType extends Attribute> AnyType getAttribute(Class<AnyType> attr){
        return (AnyType) attributes.get(attr);
    }
    
    public boolean containsAttribute (Class<? extends Attribute> attr){
        return attributes.containsKey(attr);
    }
    
    public void removeAttribute(Class<? extends Attribute> attr){
        attributes.remove(attr);
    }
    
    public void putEntity(String key, Entity ent){
        entities.put(key, ent);
        isInEngine.put(key, false);
    }
    
    public boolean activateEntity(String key){
        if(entities.containsKey(key)){
            if(!isInEngine.get(key)){
                ENGINE.add(entities.get(key));
                isInEngine.put(key, true);
            }
            return true;
        }
        RSauceLogger.printWarningln("Cannot activate entity; entity '" + key + "' could not be found");
        return false;
    }
    
    public boolean disableEntity(String key){
        if(entities.containsKey(key)){
            if(isInEngine.get(key)){
                ENGINE.removeEntity(entities.get(key));
                isInEngine.put(key, false);
            }
            return true;
        }
        RSauceLogger.printWarningln("Cannot disable entity; entity '" + key + "' could not be found");
        return false;
    }

    public boolean removeEntity(String key){
        if(entities.containsKey(key)){
            ENGINE.removeEntity(entities.get(key));
            entities.get(key).dispose();
            entities.remove(key);
            isInEngine.remove(key);
            return true;
        }
        RSauceLogger.printWarningln("Cannot remove entity; entity '" + key + "' could not be found");
        return false;
    }
    
    public Entity getEntity(String key){
        Entity ent = entities.get(key);
        
        if(ent == null) {
            RSauceLogger.printWarningln("Cannot get entity; entity '" + key + "' could not be found");
            ent = new Entity();
        }
        
        return ent;
    }
    
    public void disableEntities(){
        for(String key: entities.keySet()){
            if(isInEngine.get(key)){
                ENGINE.removeEntity(entities.get(key));
                isInEngine.put(key, false);
            }
        }
    }
    
    public void removeEntities(){
        for(Entity ent : entities.valueSet()) {
            ent.dispose();
            ENGINE.removeEntity(ent);
        }

        entities.clear();
        isInEngine.clear();
    }
}
