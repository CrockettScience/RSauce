package com.sauce.core.engine;

import com.util.structures.nonsaveable.Set;

/**
 * Created by John Crockett.
 */
public interface EntitySubscriber {

    Class<? extends Component>[] componentsToHave();
    Class<? extends Component>[] componentsNotToHave();
    void addQualifiedEntity(Entity ent);
    void addQualifiedEntities(Set<Entity> ents);
    void entityRemovedFromEngine(Entity ent);
}
