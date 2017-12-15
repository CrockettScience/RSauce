package sauce.attributes;

import sauce.core.Attribute;
import sauce.core.Scene;
import sauce.util.structures.Color;

import static org.lwjgl.opengl.GL11.glClearColor;

public class ColorAttribute implements Attribute {
    private Color color;

    public ColorAttribute(Color c){
        color = c;
    }

    @Override
    public boolean addedToScene(Scene scn) {
        glClearColor(color.getRed(), color.getGreen(), color.getBlue(), 1);
        return true;
    }

    @Override
    public void removedFromScene(Scene scn) {
        glClearColor(1, 1, 1, 1);
    }

    public void setColor(Color c){
        color = c;
    }

    public Color getColor(){
        return color;
    }
}
