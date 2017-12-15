package sauce.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.BufferUtils.createByteBuffer;

class ResourceUtil {

    static IOResource loadResource(String resourcePath) throws IOException {
        ByteBuffer buffer;

        Path path = Paths.get(resourcePath);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = createByteBuffer((int)fc.size() + 1);
                while (fc.read(buffer) != -1) {}
            }
        } else {
            throw new IOException();
        }

        buffer.flip();
        return new IOResource(buffer);
    }

    static class IOResource{
        private ByteBuffer buffer;

        IOResource(ByteBuffer aBuffer){
            buffer = aBuffer;
        }

        ByteBuffer getBuffer() {
            return buffer;
        }
    }
}
