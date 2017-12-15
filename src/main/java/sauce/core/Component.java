package sauce.core;


/**
 * Created by John Crockett.
 */
public interface Component {

    default boolean addedToEntity(Entity ent){
        return true;
    }

    default void removedFromEntity(Entity ent){}
    default void dispose(){

    }
}
