package com.sauce.asset.graphics;

import com.sauce.core.Project;
import com.util.structures.nonsaveable.Queue;
import com.util.Vector2D;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by John Crockett.
 * This class houses data structures that maps Images
 * to drawing coordinates and handles the drawing to the binded
 * framebuffer.
 */
public class DrawBatch {

    private Queue<DrawableAsset> images;
    private Queue<Vector2D> coords;

    public DrawBatch(){
        images = new Queue<>();
        coords = new Queue<>();
    }

    public void add(DrawableAsset image, int x, int y){
        images.enqueue(image);
        coords.enqueue(new Vector2D(x, y));
    }

    public void renderBatch(){
        DrawableAsset image;
        Vector2D coord;

        while(!images.isEmpty()){
            image = images.dequeue();
            coord = coords.dequeue();

            int texID = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, texID);

            if (image.components() == 3) {
                if ((image.absWidth() & 3) != 0) {
                    glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (image.absWidth() & 1));
                }
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, image.absWidth(), image.absHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, image.imageData());
            } else {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.absWidth(), image.absHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, image.imageData());

            }

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, Project.INTERPOLATION ? GL_LINEAR : GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, Project.INTERPOLATION ? GL_LINEAR : GL_NEAREST);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

            glEnable(GL_TEXTURE_2D);

            glPushMatrix();
            glTranslatef(coord.getX() + image.halfwidth(), coord.getY() + image.halfHeight(), 0);
            glRotatef(image.getAngle(), 0, 0, 1);
            glScalef(image.getXScale(), -image.getYScale(), 1f);
            glTranslatef(-coord.getX() - image.halfwidth(), -coord.getY() - image.halfHeight(), 0);

            renderImage(image, coord);

            glPopMatrix();

            glDeleteTextures(texID);

        }


    }

    private void renderImage(DrawableAsset image, Vector2D coord){
        float[] texCoords = image.regionCoordinates();
        glBegin(GL_QUADS);
        {
            glTexCoord2f(texCoords[0], texCoords[1]);
            glVertex2f(coord.getX(), coord.getY());

            glTexCoord2f(texCoords[2], texCoords[3]);
            glVertex2f(coord.getX() + image.width(), coord.getY());

            glTexCoord2f(texCoords[4], texCoords[5]);
            glVertex2f(coord.getX() + image.width(), coord.getY() + image.height());

            glTexCoord2f(texCoords[6], texCoords[7]);
            glVertex2f(coord.getX(), coord.getY() + image.height());
        }
        glEnd();


    }

}
