package sauce.core.engine;

import org.lwjgl.BufferUtils;
import sauce.core.coreutil.io.ResourceUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

class GraphicsUtil {

    static IOGraphic ioResourceToImage(ResourceUtil.IOResource resource, GraphicInfo info){
        IOGraphic image = new IOGraphic(stbi_load_from_memory(resource.getBuffer(), info.width, info.height, info.components, 0), info);

        if(image.getBuffer() == null){
            throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
        }

        return image;
    }

    static GraphicInfo getGraphicInfo(ResourceUtil.IOResource resource){
        return new GraphicInfo(resource.getBuffer());

    }

    static void applyBufferToTexture(ByteBuffer buffer, int width, int height, int components, int texID){

        glBindTexture(GL_TEXTURE_2D, texID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);


        if (components == 3) {
            if ((width & 3) != 0) {
                glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (width & 1));
            }
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, buffer);
        } else if(buffer != null){
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        }else{
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_INT, (java.nio.ByteBuffer) null);
        }

    }

    static float[] getRegionCoordinatesAdjustedForTextureRegion(TextureAtlas.TextureRegion region){
        return new float[]{(float)region.p00.getX() / Preferences.TEXTURE_PAGE_SIZE, (float)region.p00.getY() / Preferences.TEXTURE_PAGE_SIZE,
                (float)region.p10.getX() / Preferences.TEXTURE_PAGE_SIZE, (float)region.p10.getY() / Preferences.TEXTURE_PAGE_SIZE,
                (float)region.p11.getX() / Preferences.TEXTURE_PAGE_SIZE, (float)region.p11.getY() / Preferences.TEXTURE_PAGE_SIZE,
                (float)region.p01.getX() / Preferences.TEXTURE_PAGE_SIZE, (float)region.p01.getY() / Preferences.TEXTURE_PAGE_SIZE};
    }

    static class GraphicInfo {
        private IntBuffer width;
        private IntBuffer height;
        private IntBuffer components;

        private GraphicInfo(ByteBuffer buffer){
            width = BufferUtils.createIntBuffer(1);
            height = BufferUtils.createIntBuffer(1);
            components = BufferUtils.createIntBuffer(1);

            if (!stbi_info_from_memory(buffer, width, height, components)) {
                throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
            }
        }

        GraphicInfo(int w, int h, int comp){

            width = BufferUtils.createIntBuffer(1);
            height = BufferUtils.createIntBuffer(1);
            components = BufferUtils.createIntBuffer(1);

            width.put(0, w);
            height.put(0, h);
            components.put(0, comp);
        }

        public int getWidth(){
            return width.get(0);
        }

        public int getHeight(){
            return height.get(0);
        }

        public int getComponents(){
            return components.get(0);
        }
    }

    static class IOGraphic extends ResourceUtil.IOResource{

        private GraphicInfo graphicInfo;

        IOGraphic(ByteBuffer aBuffer, GraphicInfo info) {
            super(aBuffer);
            graphicInfo = info;
        }

        public GraphicInfo getGraphicInfo() {
            return graphicInfo;
        }

    }
}
