package util.structures.saveable;

import util.structures.nonsaveable.ArrayGrid;
import util.structures.saveable.util.*;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by John Crockett.
 */

public class SaveableArrayGrid<T extends SaveableData> extends ArrayGrid<T> implements SaveableStructure<T> {

    public SaveableArrayGrid(int w, int h) {
        super(w, h);
    }

    @Override
    public boolean save(File file) {
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            for (T[] x : grid) {
                for (T data : x) {
                    fOut.write(data.saveState());
                }
            }

            fOut.close();
            return true;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(SaveableArrayGrid.class.getName()).log(Level.WARNING, file.toString(), ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(SaveableArrayGrid.class.getName()).log(Level.WARNING, "Could not save: an unexpected error has occurred", ex);
            return false;
        }
    }

    @Override
    public boolean load(File file, CreateData<T> func) {
        try {
            FileInputStream fIn = new FileInputStream(file);
            byte[] buffer = new byte[func.createElement().byteSize()];

            for (int x = 0; x < grid.length; x++) {
                for (int y = 0; y < grid[x].length; y++) {
                    if (fIn.available() > 0) {
                        fIn.read(buffer);
                        T entry = func.createElement();
                        entry.loadState(buffer);
                        grid[x][y] = entry;
                    }
                }
            }

            fIn.close();
            return true;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(SaveableArrayGrid.class.getName()).log(Level.WARNING, file.toString(), ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(SaveableArrayGrid.class.getName()).log(Level.WARNING, "Could not load: an unexpected error has occurred", ex);
            return false;
        }
    }

}
