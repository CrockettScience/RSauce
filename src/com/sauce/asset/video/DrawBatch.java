package com.sauce.asset.video;

import com.structures.nonsaveable.Queue;
import static org.lwjgl.opengl.GL11.*;

/**
 * Created by John Crockett.
 * This class houses data structures that maps Images
 * to drawing coordinates and handles the drawing to the binded
 * framebuffer.
 */
public class DrawBatch {

    public Queue<DrawableAsset> images;
    public Queue<DrawingCoordinates> coords;

    public DrawBatch(){
        images = new Queue<>();
        coords = new Queue<>();
    }

    public void add(Image image, int x, int y){
        images.enqueue(image);
        coords.enqueue(new DrawingCoordinates(x, y));
    }

    public void renderBatch(){
        DrawableAsset image;
        DrawingCoordinates coord;

        while(!images.isEmpty()){
            image = images.dequeue();
            coord = coords.dequeue();

            int texID = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, texID);

            if (image.components() == 3) {
                if ((image.width() & 3) != 0) {
                    glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (image.width() & 1));
                }
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, image.width(), image.height(), 0, GL_RGB, GL_UNSIGNED_BYTE, image.imageData());
            } else {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.width(), image.height(), 0, GL_RGBA, GL_UNSIGNED_BYTE, image.imageData());

            }

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

            glEnable(GL_TEXTURE_2D);

            glPushMatrix();
            glTranslatef(coord.x + image.halfwidth() * image.getScale(), coord.y + image.halfHeight() * image.getScale(), 0);
            glRotatef((float) image.getAngle(), 0, 0, 1);
            glTranslatef(-coord.x - image.halfwidth() * image.getScale(), -coord.y - image.halfHeight() * image.getScale(), 0);

            renderImage(image, coord);
            glPopMatrix();

            glDisable(GL_TEXTURE_2D);

            glDeleteTextures(texID);

        }


    }

    private void renderImage(DrawableAsset image, DrawingCoordinates coord){
        glBegin(GL_QUADS);
        {
            glTexCoord2f(0f, 0f);
            glVertex2f(coord.x, coord.y);

            glTexCoord2f(1f, 0f);
            glVertex2f(coord.x + (image.width() * image.getScale()), coord.y);

            glTexCoord2f(1f, 1f);
            glVertex2f(coord.x + (image.width() * image.getScale()), coord.y + (image.height() * image.getScale()));

            glTexCoord2f(0f, 1f);
            glVertex2f(coord.x, coord.y + (image.height() * image.getScale()));
        }
        glEnd();


    }

    private static class DrawingCoordinates{
        private int x;
        private int y;

        private DrawingCoordinates(int x, int y){
            this.x = x;
            this.y = y;
        }
    }
}
