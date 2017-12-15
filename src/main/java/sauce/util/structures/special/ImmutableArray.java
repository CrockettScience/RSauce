package sauce.util.structures.special;

/**
 * Created by John Crockett.
 * Special array whose only purpose is to "save" a state of a collection of data the quick and dirty way, while riskily preserving state.
 * Cannot be modified or iterated.
 * If elements are modified, throws IllegalModificationError, only use if certain the elements are safe;
 */
public class ImmutableArray<T> {

    private T[] elements;
    private int dataHash;

    public ImmutableArray(T[] data){
        elements = data;
        dataHash = getHash(elements);

    }

    public T[] get(){
        if(dataHash != getHash(elements)){
            throw new IllegalModificationError();
        }

        return elements;
    }

    private static <T> int getHash(T[] data){
        int hash = data[0].hashCode();

        for(int i = 1; i < data.length; i++){
            hash = hash ^ data[i].hashCode();
        }

        return hash;
    }

    public static class IllegalModificationError extends Error{

    }
}
