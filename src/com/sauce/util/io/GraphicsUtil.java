package com.sauce.util.io;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_info_from_memory;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public class GraphicsUtil {

    public static IOGraphic ioResourceToImage(ResourceUtil.IOResource resource, GraphicInfo info){
        IOGraphic image = new IOGraphic(stbi_load_from_memory(resource.buffer, info.width, info.height, info.components, 0), info);

        if(image.buffer == null){
            throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
        }

        return image;
    }

    public static GraphicInfo getImageInfo(ResourceUtil.IOResource resource){
        return new GraphicInfo(resource.buffer);

    }

    public static void applyIOImageForDrawing(IOGraphic image, int width, int height, int components){

        if (components == 3) {
            if ((width & 3) != 0) {
                glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (width & 1));
            }
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, image.buffer);
        } else {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image.buffer);

        }

    }

    public static class GraphicInfo {
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

    public static class IOGraphic extends ResourceUtil.IOResource{

        private GraphicInfo graphicInfo;

        private IOGraphic(ByteBuffer aBuffer, GraphicInfo info) {
            super(aBuffer);
            graphicInfo = info;
        }

        public GraphicInfo getGraphicInfo() {
            return graphicInfo;
        }

        public void setGraphicInfo(GraphicInfo graphicInfo) {
            this.graphicInfo = graphicInfo;
        }
    }
}
