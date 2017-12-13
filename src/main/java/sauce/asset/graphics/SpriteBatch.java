package sauce.asset.graphics;

import sauce.asset.scripts.Script;
import util.Vector2D;
import util.structures.nonsaveable.Queue;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by John Crockett.
 * This class houses data structures that maps Images
 * to drawing coordinates and handles the drawing to the binded
 * framebuffer.
 */
public class SpriteBatch {

    private Queue<Graphic> images;
    private Queue<Vector2D> coords;
    private Queue<Script<?, ?>> scripts;

    public SpriteBatch(){
        images = new Queue<>();
        coords = new Queue<>();
        scripts = new Queue<>();
    }

    public void add(Graphic image, int x, int y){
        images.enqueue(image);
        coords.enqueue(Vector2D.create(x, y));
        scripts.enqueue(null);
    }

    public void add(Graphic image, int x, int y, Script<?, ?> script){
        images.enqueue(image);
        coords.enqueue(Vector2D.create(x, y));
        scripts.enqueue(script);
    }

    public void renderBatch(){
        Graphic image;
        Vector2D coord;
        Script<?, ?> script;

        while(!images.isEmpty()){
            image = images.dequeue();
            coord = coords.dequeue();
            script = scripts.dequeue();

            if(image != null) {

                glBindTexture(GL_TEXTURE_2D, image.textureID());

                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

                glEnable(GL_TEXTURE_2D);

                glPushMatrix();

                if(image instanceof Surface || image instanceof TiledTexture) {
                    glTranslatef(coord.getX() + image.getOrigin().getX(), coord.getY() + image.getOrigin().getY(), 0);
                    glRotatef(image.getAngle(), 0, 0, 1);
                    glScalef(image.getXScale(), image.getYScale(), 1f);
                    glTranslatef(-coord.getX() - image.getOrigin().getX() * 2, -coord.getY(), 0);
                }

                else {
                    glTranslatef(coord.getX() + image.center.getX(), coord.getY() + image.center.getY(), 0);
                    glScalef(1, -1, 1f);
                    glTranslatef(image.getOrigin().getX() - image.center.getX(), image.getOrigin().getY() - image.center.getY(), 0);
                    glRotatef(image.getAngle(), 0, 0, 1);
                    glScalef(image.getXScale(), image.getYScale(), 1f);
                    glTranslatef(-coord.getX() - image.getOrigin().getX() * 2, -coord.getY(), 0);
                }

                renderImage(image, coord);

                glPopMatrix();
            }

            if(script != null)
                script.execute(null);


        }


    }

    private void renderImage(Graphic image, Vector2D coord){
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
