
package sauce.core;


import java.util.Iterator;

/**
 *
 * @author Jonathan Crockett
 */
public class BackgroundAttribute implements Attribute {
    public ParallaxBackground[] backgrounds = new ParallaxBackground[10];
    public ParallaxBackground[] foregrounds = new ParallaxBackground[10];

    public void setBackground(ParallaxBackground para, int index){
        backgrounds[index] = para;
    }

    public void setForeground(ParallaxBackground para, int index){
        foregrounds[index] = para;
    }

    public Iterator<ParallaxBackground> backgroundIterator(){
        return new ParallaxIterator(true);
    }

    public Iterator<ParallaxBackground> foregroundIterator(){
        return new ParallaxIterator(false);
    }

    @Override
    public void dispose() {
        for(ParallaxBackground bg : backgrounds)
            if(bg != null)
                bg.dispose();

        for(ParallaxBackground fg : foregrounds)
            if(fg != null)
                fg.dispose();
    }

    private class ParallaxIterator implements Iterator<ParallaxBackground>{

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
        public ParallaxBackground next() {
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
