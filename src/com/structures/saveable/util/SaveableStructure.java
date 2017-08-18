package com.structures.saveable.util;

import java.io.File;

/**
 * Created by John Crockett.
 */

public interface SaveableStructure<T extends SaveableData> {

    boolean save(File file);

    boolean load(File file, CreateData<T> func);
}
