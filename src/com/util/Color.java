package com.util;

public class Color {
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
}
