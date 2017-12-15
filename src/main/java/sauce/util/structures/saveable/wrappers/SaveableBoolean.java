package sauce.util.structures.saveable.wrappers;

import sauce.util.structures.saveable.util.SaveableData;

public class SaveableBoolean implements SaveableData {

    private boolean val;

    public SaveableBoolean(boolean value){
        val = value;
    }

    @Override
    public byte[] saveState() {
        byte[] bytes = new byte[1];
        bytes[0] = (byte)( val ? 1 : 0);
        return bytes;
    }

    @Override
    public void loadState(byte[] bytes) {
        val = bytes[0] == (byte) 1;
    }

    @Override
    public int byteSize() {
        return 1;
    }

    public boolean isTrue() {
        return val;
    }

    public void setValue(boolean value) {
        val = value;
    }
}
