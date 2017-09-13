package com.util.structures.saveable.wrappers;

import com.util.structures.saveable.util.SaveableData;

import java.nio.ByteBuffer;

public class SaveableShort implements SaveableData {

    private short val;

    public SaveableShort(short value){
        val = value;
    }

    @Override
    public byte[] saveState() {
        byte[] bytes = new byte[2];
        ByteBuffer.wrap(bytes).putShort(val);
        return bytes;
    }

    @Override
    public void loadState(byte[] bytes) {
        val = ByteBuffer.wrap(bytes).getShort();
    }

    @Override
    public int byteSize() {
        return 2;
    }

    public short getValue() {
        return val;
    }

    public void setValue(short value) {
        val = value;
    }
}
