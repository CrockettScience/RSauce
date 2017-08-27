package com.util.structures.saveable.wrappers;

import com.util.structures.saveable.util.SaveableData;

import java.util.Arrays;

/**
 * Created by John Crockett.
 */

public class SaveableString implements SaveableData {

    public static int TOTAL_BYTES = 32;
    private String string;

    public SaveableString(String str) {
        string = str;
    }

    public byte[] saveState() {
        if (string.length() >= TOTAL_BYTES) {
            System.out.println("\u001B[31mWARNING: String may have been truncated upon saving to file.");
        }

        return Arrays.copyOf(string.getBytes(), TOTAL_BYTES);
    }

    public void loadState(byte[] bytes) {
        bytes = Arrays.copyOf(bytes, TOTAL_BYTES);
        string = new String(bytes);
    }

    public int byteSize() {
        return TOTAL_BYTES;
    }

    public String getString() {
        return string;
    }

    public void setString(String str) {
        string = str;
    }

    public String toString() {
        return string;
    }

}