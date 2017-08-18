package com.structures.saveable.wrappers;

import com.structures.saveable.util.SaveableData;

/**
 * Created by John Crockett.
 */

public class SaveableInteger implements SaveableData {

    private int value;

    public SaveableInteger(int val) {
        value = val;
    }

    public byte[] saveState() {
        int temp = getValue();
        byte[] b = new byte[4];

        b[0] = (byte) ((temp >> 24) & 0xFF);
        b[1] = (byte) ((temp >> 16) & 0xFF);
        b[2] = (byte) ((temp >> 8) & 0xFF);
        b[3] = (byte) (temp & 0xFF);

        return b;
    }

    public void loadState(byte[] bytes) {
        byte[] b = bytes;
        setValue(b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24);
    }

    public String toString() {
        return String.valueOf(getValue());
    }

    @Override
    public int byteSize() {
        return 4;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
