package sauce.util.io;

import util.Vector2D;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class FontUtil {

    public static IOFont ioResourceToFont(ResourceUtil.IOResource resource, FontInfo info){
        return new IOFont(resource.buffer, info);
    }

    public static FontInfo getFontInfo(ResourceUtil.IOResource resource, int fontsize){
        STBTTFontinfo rawInfo = STBTTFontinfo.create();

        if(!stbtt_InitFont(rawInfo, resource.buffer))
            throw new IllegalStateException("Failed to initialize font information.");

        try (MemoryStack stack = stackPush()) {
            IntBuffer pAscent  = stack.mallocInt(1);
            IntBuffer pDescent = stack.mallocInt(1);
            IntBuffer pLineGap = stack.mallocInt(1);

            stbtt_GetFontVMetrics(rawInfo, pAscent, pDescent, pLineGap);

            int bmpBufferSize = 7 * fontsize;

            STBTTBakedChar.Buffer cdata = STBTTBakedChar.malloc(96);

            ByteBuffer bitmap = BufferUtils.createByteBuffer(bmpBufferSize * bmpBufferSize);
            stbtt_BakeFontBitmap(resource.buffer, fontsize, bitmap, bmpBufferSize, bmpBufferSize, 32, cdata);

            return new FontInfo(pAscent.get(0), pDescent.get(0), pLineGap.get(0), bmpBufferSize, bitmap, cdata, rawInfo);
        }
    }


    public static class FontInfo{
        private int ascent;
        private int descent;
        private int lineGap;
        private int bmpBufferSize;
        private ByteBuffer bitmap;
        private STBTTBakedChar.Buffer cData;
        private STBTTFontinfo rawInfo;

        private FontInfo(int asc, int desc, int lg, int bmpSize, ByteBuffer bmp, STBTTBakedChar.Buffer data, STBTTFontinfo info){
            ascent = asc;
            descent = desc;
            lineGap = lg;
            bitmap = bmp;
            cData = data;
            rawInfo = info;
            bmpBufferSize = bmpSize;

        }

        public int getAscent() {
            return ascent;
        }

        public int getDescent() {
            return descent;
        }

        public int getLineGap() {
            return lineGap;
        }

        public ByteBuffer getBitmap() {
            return bitmap;
        }

        public STBTTBakedChar.Buffer getcData() {
            return cData;
        }

        public STBTTFontinfo getRawInfo() {
            return rawInfo;
        }

        public int getBmpBufferSize() {
            return bmpBufferSize;
        }
    }

    public static class IOFont extends ResourceUtil.IOResource {

        private FontInfo fontInfo;

        private IOFont(ByteBuffer aBuffer, FontInfo info) {
            super(aBuffer);
            fontInfo = info;
        }

        public FontInfo getFontInfo(){
            return fontInfo;
        }
    }
}
