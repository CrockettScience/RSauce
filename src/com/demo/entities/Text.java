package com.demo.entities;

import com.sauce.asset.fonts.Font;
import com.sauce.asset.graphics.Surface;
import com.sauce.asset.scripts.Argument;
import com.sauce.asset.scripts.Return;
import com.sauce.asset.scripts.Script;
import com.sauce.core.engine.DrawComponent;
import com.sauce.core.engine.Entity;
import com.util.Color;

public class Text extends Entity {
    private Font font;
    DrawComponent draw;
    private int width;
    private int height;

    private int[] pX = new int[1];
    private int[] pY = new int[1];

    public Text(String fontPath, Color color, String text, int size, int x, int y, int z){

            font = new Font(fontPath, size);
            width = (int) font.getStringWidth(text) + 1;
            height = font.getHeight();

            pX[0] = x;
            pY[0] = y;

            draw = new DrawComponent(new Script<Argument, Return>() {

                @Override
                protected Return scriptMain(Argument args) {
                    font.renderText(text, color, pX[0], pY[0]);

                    return null;
                }
            }, z);

            addComponent(draw);

    }

    public Text(String fontPath, Color color, String text, int size, int x, int y, int z, int oversampleFactor){

        font = new Font(fontPath, size * oversampleFactor);

        Font metricFont = new Font(fontPath, size);
        width = ((int) metricFont.getStringWidth(text) + 1);
        height = metricFont.getHeight();
        metricFont.dispose();

        Surface scaleDown = new Surface((int) font.getStringWidth(text) + 1, font.getHeight());

        scaleDown.bind();
        {
            font.renderText(text, color, 0, 0);
        }
        scaleDown.unbind();

        scaleDown.setXScale(1f / oversampleFactor);
        scaleDown.setYScale(1f / oversampleFactor);

        draw = new DrawComponent(scaleDown, x, y, z);

        addComponent(draw);

    }

    @Override
    public void dispose() {
        font.dispose();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setX(int x){
        draw.setX(x);
        pX[0] = x;
    }

    public void setY(int y){
        draw.setX(y);
        pY[0] = y;
    }

    public void setZ(int z){
        draw.setX(z);
    }
}