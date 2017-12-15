package sauce.core;

import sauce.util.structures.nonsaveable.Set;

/**
 * Created by John Crockett.
 */
public interface EntitySubscriber {

    Class<? extends Component>[] componentsToHave();
    Class<? extends Component>[] componentsNotToHave();
    boolean containsEntity(Entity e);
    void addQualifiedEntity(Entity ent);
    void addQualifiedEntities(Set<Entity> ents);
    void entityRemovedFromEngine(Entity ent);
    void entityNoLongerQualifies(Entity ent);
}
