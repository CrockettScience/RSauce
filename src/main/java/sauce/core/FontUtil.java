package sauce.core;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;

class FontUtil {

    static IOFont ioResourceToFont(ResourceUtil.IOResource resource, FontInfo info){
        return new IOFont(resource.getBuffer(), info);
    }

    static FontInfo getFontInfo(ResourceUtil.IOResource resource, int fontsize){
        STBTTFontinfo rawInfo = STBTTFontinfo.create();

        if(!stbtt_InitFont(rawInfo, resource.getBuffer()))
            throw new IllegalStateException("Failed to initialize font information.");

        try (MemoryStack stack = stackPush()) {
            IntBuffer pAscent  = stack.mallocInt(1);
            IntBuffer pDescent = stack.mallocInt(1);
            IntBuffer pLineGap = stack.mallocInt(1);

            stbtt_GetFontVMetrics(rawInfo, pAscent, pDescent, pLineGap);

            int bmpBufferSize = 7 * fontsize;

            STBTTBakedChar.Buffer cdata = STBTTBakedChar.malloc(96);

            ByteBuffer bitmap = BufferUtils.createByteBuffer(bmpBufferSize * bmpBufferSize);
            stbtt_BakeFontBitmap(resource.getBuffer(), fontsize, bitmap, bmpBufferSize, bmpBufferSize, 32, cdata);

            return new FontInfo(pAscent.get(0), pDescent.get(0), pLineGap.get(0), bmpBufferSize, bitmap, cdata, rawInfo);
        }
    }


    static class FontInfo{
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

        int getAscent() {
            return ascent;
        }

        int getDescent() {
            return descent;
        }

        int getLineGap() {
            return lineGap;
        }

        ByteBuffer getBitmap() {
            return bitmap;
        }

        STBTTBakedChar.Buffer getcData() {
            return cData;
        }

        STBTTFontinfo getRawInfo() {
            return rawInfo;
        }

        int getBmpBufferSize() {
            return bmpBufferSize;
        }
    }

    static class IOFont extends ResourceUtil.IOResource {

        private FontInfo fontInfo;

        private IOFont(ByteBuffer aBuffer, FontInfo info) {
            super(aBuffer);
            fontInfo = info;
        }

        FontInfo getFontInfo(){
            return fontInfo;
        }
    }
}
