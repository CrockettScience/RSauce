package sauce.core.engine;

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
