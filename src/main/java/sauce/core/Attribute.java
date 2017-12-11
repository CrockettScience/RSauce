package sauce.core;

/**
 *
 * @author Jonathan Crockett
 */
public interface Attribute {
    default boolean addedToScene(Scene scn){
        return true;
    }
    default void removedFromScene(Scene scn){}
    default void dispose(){}

}
