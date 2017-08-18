package com.structures.saveable.util;

/**
 * Created by John Crockett.
 */

public interface SaveableData {

    byte[] saveState();

    void loadState(byte[] bytes);

    int byteSize();
}


