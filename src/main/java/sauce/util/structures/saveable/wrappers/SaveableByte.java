package sauce.util.structures.saveable.wrappers;

import sauce.util.structures.saveable.util.SaveableData;

public class SaveableByte implements SaveableData {

    private byte val;

    public SaveableByte(byte value){
        val = value;
    }

    @Override
    public byte[] saveState() {
        byte[] bytes = new byte[1];
        bytes[0] = val;
        return bytes;
    }

    @Override
    public void loadState(byte[] bytes) {
        val = bytes[0];
    }

    @Override
    public int byteSize() {
        return 1;
    }

    public byte getValue() {
        return val;
    }

    public void setValue(byte value) {
        val = value;
    }
}
