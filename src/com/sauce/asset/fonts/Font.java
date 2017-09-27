package com.sauce.asset.fonts;

import static com.sauce.util.io.FontUtil.*;

import com.sauce.asset.graphics.Surface;
import com.sauce.util.io.FontUtil;
import com.util.structures.nonsaveable.Map;

public class Font {
    private FontUtil.IOFont font;
    private Map<String, LetterTexCoord> letterMap;
    private Surface fontSurface;

    public Font(String fileName){

    }

    public Surface getText(String text){
        return null;
    }

    private class LetterTexCoord{
        private int x;
        private int y;
        private int width;
        private int height;

        private LetterTexCoord(int posX, int posY, int wide, int high){
            x = posX;
            y = posY;
            width = wide;
            height = high;
        }
    }
}
