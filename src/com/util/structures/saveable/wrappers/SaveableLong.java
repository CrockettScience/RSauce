package com.util.structures.saveable.wrappers;

import com.util.structures.saveable.util.SaveableData;

import java.nio.ByteBuffer;

public class SaveableLong implements SaveableData {

    private long val;

    public SaveableLong(long value) {
        val = value;
    }
    @Override
    public byte[] saveState() {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putLong(val);
        return bytes;
    }

    @Override
    public void loadState(byte[] bytes) {

    }

    @Override
    public int byteSize() {
        return 8;
    }

    public long getValue() {
        return val;
    }

    public void setValue(long value) {
        val = value;
    }
}
