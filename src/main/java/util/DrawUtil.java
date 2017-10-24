package util;

import static org.lwjgl.opengl.GL11.*;

public class DrawUtil {

    public static void line(float thickness, Vector2D p1, Vector2D p2, Color c, float opacity){
        glDisable(GL_TEXTURE_2D);
        glLineWidth(thickness);
        glColor4f(c.getRed(), c.getGreen(), c.getBlue(), opacity);
        glBegin(GL_LINES);
        {
            glVertex2i(p1.getX(), p1.getY());
            glVertex2i(p2.getX(), p2.getY());
        }
        glEnd();

        glColor4f(1, 1, 1, 1);
    }

    public static void rect(float thickness, Vector2D xy, Vector2D wh, Color c, float opacity){
        line(thickness, xy,                                                               new Vector2D(xy.getX(),             xy.getY() + wh.getY()), c, opacity);
        line(thickness, xy,                                                               new Vector2D(xy.getX() + wh.getX(), xy.getY())            , c, opacity);
        line(thickness, new Vector2D(xy.getX() + wh.getX(), xy.getY() + wh.getY()), new Vector2D(xy.getX(),             xy.getY() + wh.getY()), c, opacity);
        line(thickness, new Vector2D(xy.getX() + wh.getX(), xy.getY() + wh.getY()), new Vector2D(xy.getX() + wh.getX(), xy.getY())            , c, opacity);
    }

    public static void quad(float thickness, Vector2D ul, Vector2D ur, Vector2D ll, Vector2D lr, Color c, float opacity){
        line(thickness, ul, ur, c, opacity);
        line(thickness, ul, ll, c, opacity);
        line(thickness, lr, ur, c, opacity);
        line(thickness, lr, ll, c, opacity);
    }

}
