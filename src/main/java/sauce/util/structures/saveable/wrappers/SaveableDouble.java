package sauce.util.structures.saveable.wrappers;

import sauce.util.structures.saveable.util.SaveableData;

import java.nio.ByteBuffer;

/**
 * Created by John Crockett.
 */
public class SaveableDouble implements SaveableData{

    private double val;

    public SaveableDouble(double value){
        val = value;
    }

    @Override
    public byte[] saveState() {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putDouble(val);
        return bytes;
    }

    @Override
    public void loadState(byte[] bytes) {
        val = ByteBuffer.wrap(bytes).getDouble();
    }

    @Override
    public int byteSize() {
        return 8;
    }

    public double getValue() {
        return val;
    }

    public void setValue(double value) {
        val = value;
    }
}
