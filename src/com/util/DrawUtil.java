package com.util;

import static org.lwjgl.opengl.GL11.*;

public class DrawUtil {

    public static void line(float thickness, Vector2D p1, Vector2D p2, Color c, float opacity){
        glDisable(GL_TEXTURE_2D);
        glLineWidth(thickness);
        glColor4f(c.red, c.green, c.blue, opacity);
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

    public static final Color C_RED = new Color(1, 0, 0);
    public static final Color C_ORANGE = new Color(1, 0.65f, 0);
    public static final Color C_YELLOW = new Color(1, 1, 0);
    public static final Color C_GREEN = new Color(0, 1, 0);
    public static final Color C_BLUE = new Color(0, 0, 1);
    public static final Color C_PURPLE = new Color(0.5f, 0, 0.5f);
    public static final Color C_CYAN = new Color(0, 1, 1);
    public static final Color C_BROWN = new Color(0.55f, 0.27f, 0.07f);
    public static final Color C_WHITE = new Color(1, 1, 1);
    public static final Color C_GREY = new Color(0.5f, 0.5f, 0.5f);
    public static final Color C_BLACK = new Color(0, 0, 0);

    public static class Color{
        private float red;
        private float green;
        private float blue;

        public Color(float r, float g, float b){
            red = r;
            green = g;
            blue = b;
        }

        public float getRed() {
            return red;
        }

        public float getGreen() {
            return green;
        }

        public float getBlue() {
            return blue;
        }
    }
}
