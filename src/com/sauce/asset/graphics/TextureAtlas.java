package com.sauce.asset.graphics;

import com.util.Vector2DDouble;

public class TextureAtlas {

    public class TextureRegion {
        private Vector2DDouble p00;
        private Vector2DDouble p01;
        private Vector2DDouble p10;
        private Vector2DDouble p11;
        private int texPageID;

        private TextureRegion(double x, double y, double width, double height, int texID){
            p00 = new Vector2DDouble(x, y);
            p01 = new Vector2DDouble(x, y + height);
            p10 = new Vector2DDouble(x + width, y);
            p11 = new Vector2DDouble(x + width, y + height);
            texPageID = texID;
        }
    }
}
