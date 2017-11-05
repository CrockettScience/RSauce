package util.structures.saveable;

import util.structures.nonsaveable.Set;
import util.structures.saveable.util.CreateData;
import util.structures.saveable.util.SaveableData;
import util.structures.saveable.util.SaveableStructure;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by John Crockett.
 */

public class SaveableSet<T extends SaveableData> extends Set<T> implements SaveableStructure<T> {

    @Override
    public boolean save(File file) {
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            for (SetEntry<T> x : entryTable) {
                SetEntry<T> entry = x;
                while (entry != null) {
                    fOut.write(entry.element.saveState());
                    entry = entry.next;
                }
            }

            fOut.close();
            return true;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(SaveableSet.class.getName()).log(Level.WARNING, file.toString(), ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(SaveableSet.class.getName()).log(Level.WARNING, "Could not save: an unexpected error has occurred", ex);
            return false;
        }
    }

    @Override
    public boolean load(File file, CreateData<T> func) {
        try {
            FileInputStream fIn = new FileInputStream(file);
            byte[] buffer = new byte[func.createElement().byteSize()];

            while (fIn.available() > 0) {
                fIn.read(buffer);
                T entry = func.createElement();
                entry.loadState(buffer);
                add(entry);
            }

            fIn.close();
            return true;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(SaveableSet.class.getName()).log(Level.WARNING, file.toString(), ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(SaveableSet.class.getName()).log(Level.WARNING, "Could not load: an unexpected error has occurred", ex);
            return false;
        }
    }
}
