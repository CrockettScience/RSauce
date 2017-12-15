package sauce.util.structures.saveable.wrappers;

import sauce.util.structures.saveable.util.SaveableData;

import java.nio.ByteBuffer;

public class SaveableFloat implements SaveableData {

    private float val;

    public SaveableFloat(float value){
        val = value;
    }

    @Override
    public byte[] saveState() {
        byte[] bytes = new byte[4];
        ByteBuffer.wrap(bytes).putFloat(val);
        return bytes;

    }

    @Override
    public void loadState(byte[] bytes) {
        val = ByteBuffer.wrap(bytes).getFloat();

    }

    @Override
    public int byteSize() {
        return 4;
    }

    public float getValue() {
        return val;
    }

    public void setValue(float value) {
        val = value;
    }
}
