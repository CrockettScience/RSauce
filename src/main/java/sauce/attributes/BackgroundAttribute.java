
package sauce.attributes;


import sauce.core.Attribute;
import sauce.core.Background;
import sauce.core.Scene;

import java.util.Iterator;

/**
 *
 * @author Jonathan Crockett
 */
public class BackgroundAttribute implements Attribute {
    public Background[] backgrounds = new Background[10];
    public Background[] foregrounds = new Background[10];

    public void setBackground(Background para, int index){
        backgrounds[index] = para;
    }

    public void setForeground(Background para, int index){
        foregrounds[index] = para;
    }

    public Iterator<Background> backgroundIterator(){
        return new ParallaxIterator(true);
    }

    public Iterator<Background> foregroundIterator(){
        return new ParallaxIterator(false);
    }

    @Override
    public void removedFromScene(Scene scn) {
        dispose();
    }

    @Override
    public void dispose() {
        for(Background bg : backgrounds)
            if(bg != null)
                bg.dispose();

        for(Background fg : foregrounds)
            if(fg != null)
                fg.dispose();
    }

    private class ParallaxIterator implements Iterator<Background>{

        private int current = -1;
        private boolean type;

        private ParallaxIterator(boolean forBackgrounds){
            type = forBackgrounds;
        }

        @Override
        public boolean hasNext() {
            return current != 9;
        }

        @Override
        public Background next() {
            current++;
            if(type){
                return backgrounds[current];
            }
            else{
                return foregrounds[current];
            }
        }
    }
}
