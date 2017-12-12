package sauce.asset.fonts;

import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.system.MemoryStack;
import sauce.core.coreutil.io.ResourceUtil;
import sauce.core.coreutil.misc.AssetDisposedException;
import sauce.core.coreutil.misc.Disposable;
import util.Color;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static sauce.core.coreutil.io.FontUtil.*;

public class Font implements Disposable {
    private boolean disposed = false;

    private IOFont font;
    private int fontHeight;
    private int texID;

    public Font(String fileName, int size){
        fontHeight = size;

        ResourceUtil.IOResource resource;
        try {
            resource = ResourceUtil.loadResource(fileName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to open TrueType font file " + fileName);
        }

        FontInfo info = getFontInfo(resource, size);

        font = ioResourceToFont(resource, info);

        texID = glGenTextures();
    }

    public void renderText(String text, Color color, float xPos, float yPos){
        if(disposed)
            throw new AssetDisposedException(this);


        glBindTexture(GL_TEXTURE_2D, texID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, font.getFontInfo().getBmpBufferSize(), font.getFontInfo().getBmpBufferSize(), 0, GL_ALPHA, GL_UNSIGNED_BYTE, font.getFontInfo().getBitmap());

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        glColor3f(color.getRed(), color.getGreen(), color.getBlue());

        glEnable(GL_TEXTURE_2D);

        float scale = stbtt_ScaleForPixelHeight(font.getFontInfo().getRawInfo(), fontHeight);

        try (MemoryStack stack = stackPush()) {
            IntBuffer pCodePoint = stack.mallocInt(1);

            FloatBuffer x = stack.floats(0.0f);
            FloatBuffer y = stack.floats(0.0f);

            STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);

            int i  = 0;
            int to = text.length();

            glBegin(GL_QUADS);
            while (i < to) {
                i += getCP(text, to, i, pCodePoint);

                int cp = pCodePoint.get(0);
                if (cp == '\n') {

                    y.put(0, y.get(0) + (font.getFontInfo().getAscent() - font.getFontInfo().getDescent() + font.getFontInfo().getLineGap()) * scale);
                    x.put(0, 0.0f);

                    continue;
                } else if (cp < 32 || 128 <= cp) {
                    continue;
                }

                stbtt_GetBakedQuad(font.getFontInfo().getcData(), font.getFontInfo().getBmpBufferSize(), font.getFontInfo().getBmpBufferSize(), cp - 32, x, y, q, true);

                if (i < to) {
                    getCP(text, to, i, pCodePoint);
                    x.put(0, x.get(0) + stbtt_GetCodepointKernAdvance(font.getFontInfo().getRawInfo(), cp, pCodePoint.get(0)) * scale);
                }

                glTexCoord2f(q.s0(), q.t0());
                glVertex2f(q.x0() + xPos, -q.y0() + yPos);

                glTexCoord2f(q.s1(), q.t0());
                glVertex2f(q.x1() + xPos, -q.y0() + yPos);

                glTexCoord2f(q.s1(), q.t1());
                glVertex2f(q.x1() + xPos, yPos);

                glTexCoord2f(q.s0(), q.t1());
                glVertex2f(q.x0() + xPos, yPos);
            }
            glEnd();
        }
        glPopMatrix();

        glColor3f(1, 1, 1);

    }

    public float getStringWidth(String text) {
        if(disposed)
            throw new AssetDisposedException(this);

        int width = 0;

        try (MemoryStack stack = stackPush()) {
            IntBuffer pCodePoint       = stack.mallocInt(1);
            IntBuffer pAdvancedWidth   = stack.mallocInt(1);
            IntBuffer pLeftSideBearing = stack.mallocInt(1);

            for(int i = 0; i < text.length();){
                i += getCP(text, text.length(), i, pCodePoint);
                int cp = pCodePoint.get(0);

                stbtt_GetCodepointHMetrics(font.getFontInfo().getRawInfo(), cp, pAdvancedWidth, pLeftSideBearing);
                width += pAdvancedWidth.get(0);

                if (i < text.length()) {
                    getCP(text, text.length(), i, pCodePoint);
                    width += stbtt_GetCodepointKernAdvance(font.getFontInfo().getRawInfo(), cp, pCodePoint.get(0));
                }
            }
        }

        return width * stbtt_ScaleForPixelHeight(font.getFontInfo().getRawInfo(), fontHeight);
    }

    public int getHeight(){
        if(disposed)
            throw new AssetDisposedException(this);

        return fontHeight;
    }

    private static int getCP(String text, int to, int i, IntBuffer cpOut) {
        char c1 = text.charAt(i);
        if (Character.isHighSurrogate(c1) && i + 1 < to) {
            char c2 = text.charAt(i + 1);
            if (Character.isLowSurrogate(c2)) {
                cpOut.put(0, Character.toCodePoint(c1, c2));
                return 2;
            }
        }
        cpOut.put(0, c1);
        return 1;
    }

    protected void checkDisposed(){
        if(disposed)
            throw new AssetDisposedException(this);

    }

    public void dispose(){
        checkDisposed();
        disposed = true;
        font.getFontInfo().getcData().free();
        glDeleteTextures(texID);
    }
}
