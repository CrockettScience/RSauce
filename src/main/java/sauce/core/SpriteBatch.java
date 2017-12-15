package sauce.core;

import sauce.concurrent.Script;
import sauce.util.structures.nonsaveable.Queue;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by John Crockett.
 * This class houses data structures that maps Images
 * to drawing coordinates and handles the drawing to the binded
 * framebuffer.
 */
public class SpriteBatch {

    private Queue<DrawEntry> entries;

    public SpriteBatch(){
        entries = new Queue();
    }

    public void add(Sprite sprite, float x, float y){
        entries.enqueue(new DrawEntry(x, y, sprite, null));
    }

    public void add(Sprite sprite, float x, float y, Script<?, ?> script){
        entries.enqueue(new DrawEntry(x, y, sprite, script));
    }

    public void render(){
        Sprite sprite;
        Script<?, ?> script;

        while(!entries.isEmpty()){
            DrawEntry entry = entries.dequeue();
            sprite = entry.sprite;
            float x = entry.x;
            float y = entry.y;
            script = entry.script;

            if(sprite != null) {

                glBindTexture(GL_TEXTURE_2D, sprite.textureID());

                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

                glEnable(GL_TEXTURE_2D);

                glPushMatrix();

                glTranslatef(x + sprite.getOrigin().getX() + sprite.getRegion().p00.getX(), y + sprite.getOrigin().getY() + sprite.getRegion().p00.getY(), 0);
                glRotatef(sprite.getAngle(), 0, 0, 1);
                glScalef(sprite.getXScale(), sprite.getYScale(), 1f);
                glTranslatef(-x - sprite.getRegion().p00.getX() / sprite.getXScale() - sprite.getOrigin().getX() * 2 , -y - sprite.getRegion().p00.getY() / sprite.getYScale() - sprite.getOrigin().getY() * 2, 0);

                renderImage(sprite, x, y);

                glPopMatrix();
            }

            if(script != null)
                script.execute(null);


        }


    }

    private void renderImage(Sprite image, float x, float y){
        float[] texCoords = image.regionCoordinates();
        glBegin(GL_QUADS);
        {
            glTexCoord2f(texCoords[0], texCoords[1]);
            glVertex2f(x, y);

            glTexCoord2f(texCoords[2], texCoords[3]);
            glVertex2f(x + image.width(), y);

            glTexCoord2f(texCoords[4], texCoords[5]);
            glVertex2f(x + image.width(), y + image.height());

            glTexCoord2f(texCoords[6], texCoords[7]);
            glVertex2f(x, y + image.height());
        }
        glEnd();


    }

    private class DrawEntry{
        float x = 0;
        float y = 0;
        Sprite sprite;
        Script<?, ?> script;

        public DrawEntry(float ax, float ay, Sprite aSprite, Script<?, ?> aScript){
           x = ax;
           y = ay;
           sprite = aSprite;
           script = aScript;
        }
    }

}
