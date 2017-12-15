package sauce.util.structures.saveable.wrappers;

import sauce.util.structures.saveable.util.SaveableData;

import java.nio.ByteBuffer;

public class SaveableChar implements SaveableData {

    private char val;

    public SaveableChar(char value){
        val = value;
    }

    @Override
    public byte[] saveState() {
        byte[] bytes  = new byte[2];
        ByteBuffer.wrap(bytes).putChar(val);
        return bytes;
    }

    @Override
    public void loadState(byte[] bytes) {
        val = ByteBuffer.wrap(bytes).getChar();
    }

    @Override
    public int byteSize() {
        return 2;
    }

    public char getValue() {
        return val;
    }

    public void setValue(char value) {
        val = value;
    }
}
