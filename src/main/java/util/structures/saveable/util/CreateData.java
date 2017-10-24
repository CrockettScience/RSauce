package util.structures.saveable.util;

/**
 * Created by John Crockett.
 */

public interface CreateData<T extends SaveableData> {
    public T createElement();
}
