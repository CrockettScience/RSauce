package sauce.core;


import sauce.util.RSauceLogger;
import sauce.util.structures.nonsaveable.Map;

/**
 *
 * @author Jonathan Crockett
 */
public abstract class Scene{
    private final Map<Class<? extends Attribute>, Attribute> attributes = new Map<>();
    private Map<String, EntityEntry> entities = new Map<>();

    protected abstract void loadResources();
    protected abstract void destroyResources();
    protected abstract void sceneMain();
    
    public void addAttribute(Attribute attr){
        if(!attributes.containsKey(attr.getClass())) {
            if(attr.addedToScene(this)){
                attributes.put(attr.getClass(), attr);

            }
            else
                attr.removedFromScene(this);
        }
        else
            RSauceLogger.printWarningln(attr.getClass().getSimpleName() + " could not be added to the Scene, the Scene already contians an attribute of this type");
    }
    
    public <AnyType extends Attribute> AnyType getAttribute(Class<AnyType> attr){
        return (AnyType) attributes.get(attr);
    }
    
    public boolean containsAttribute (Class<? extends Attribute> attr){
        return attributes.containsKey(attr);
    }
    
    public void removeAttribute(Class<? extends Attribute> attr){
        attributes.remove(attr).removedFromScene(this);
    }
    
    public void putEntity(String key, Entity ent){
        entities.put(key, new EntityEntry(ent));
    }
    
    public boolean activateEntity(String key){
        EntityEntry entry = entities.get(key);
        if(entry != null){
            if(!entry.isActive) {
                entry.activate();
            }
            return true;
        }
        RSauceLogger.printWarningln("Cannot activate entity; entity '" + key + "' could not be found");
        return false;
    }
    
    public boolean disableEntity(String key){
        EntityEntry entry = entities.get(key);
        if(entry != null){
            if(entry.isActive){
                entry.deactivate();
            }
            return true;
        }
        RSauceLogger.printWarningln("Cannot disable entity; entity '" + key + "' could not be found");
        return false;
    }

    public boolean removeEntity(String key){
        EntityEntry entry = entities.get(key);
        if(entry != null){
            entry.dispose();
            entities.remove(key);
            return true;
        }
        RSauceLogger.printWarningln("Cannot remove entity; entity '" + key + "' could not be found");
        return false;
    }
    
    public Entity getEntity(String key){
        EntityEntry entry = entities.get(key);
        
        if(entry == null) {
            RSauceLogger.printWarningln("Cannot get entity; entity '" + key + "' could not be found");
            return null;
        }

        return entry.entity;
    }
    
    public void disableEntities(){
        for(EntityEntry entry: entities.valueSet()){
            if(entry.isActive){
                entry.deactivate();
            }
        }
    }
    
    public void removeEntities(){
        for(EntityEntry entry: entities.valueSet()){
            entry.deactivate();
        }

        entities.clear();
    }

    void dispose(){
        for(EntityEntry entry: entities.valueSet()){
            entry.dispose();
        }

        entities.clear();

        for (Attribute attr : attributes.valueSet()){
            attr.removedFromScene(this);
        }

        attributes.clear();

        destroyResources();
    }

    private class EntityEntry{
        private Entity entity;
        private boolean isActive;

        private EntityEntry(Entity ent){
            entity = ent;
            isActive = false;
        }

        private void deactivate(){
            isActive = false;
            Engine.remove(entity);
        }

        private void activate(){
            isActive = true;
            Engine.add(entity);
        }

        private void dispose(){
            entity.dispose();
            if(isActive){
                Engine.remove(entity);
                isActive = false;
            }
        }
    }
}
