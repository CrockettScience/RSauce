package com.sauce.asset.graphics;

import com.util.Vector2DDouble;
import com.util.structures.nonsaveable.Map;

public class TextureAtlas<K> {
    private static int PAGE_SIZE = 2048;
    private static double TEXEL_SIZE = 1 / PAGE_SIZE;

    private Map<K, TextureRegion> textureMap = new Map<>();
    private Node root = new Node();

    public TextureRegion getTexture(K key){
        return textureMap.get(key);
    }

    public boolean containsTexture(K key){
        return textureMap.containsKey(key);
    }

    public void putTexture(K key, Graphic graphic){
    }

    private Node insert(Graphic graphic){
        return null;
    }

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

        private TextureRegion(int texID){
            p00 = null;
            p01 = null;
            p10 = null;
            p11 = null;
            texPageID = texID;
        }

        private TextureRegion divide(boolean horizontally, double size){
            TextureRegion other = new TextureRegion(texPageID);
            if(horizontally){
                other.p00 = new Vector2DDouble(p00.getX() + size + TEXEL_SIZE, p00.getY());
                other.p01 = new Vector2DDouble(p01.getX() + size + TEXEL_SIZE, p01.getY());
                other.p10 = p10;
                other.p11 = p11;

                p10 = new Vector2DDouble(p00.getX() + size, p00.getY());
                p11 = new Vector2DDouble(p01.getX() + size, p01.getY());
            }
            else{
                other.p00 = new Vector2DDouble(p01.getX(), p00.getY() + size + TEXEL_SIZE);
                other.p10 = new Vector2DDouble(p10.getX(), p10.getY() + size + TEXEL_SIZE);
                other.p01 = p01;
                other.p11 = p11;

                p01 = new Vector2DDouble(p00.getX(), p00.getY() + size);
                p11 = new Vector2DDouble(p10.getX(), p11.getY() + size);
            }

            return other;
        }
    }

    private class Node {
        private Node left;
        private Node Right;
        private TextureRegion reg;

        public boolean isLeaf(){
            return reg != null;
        }
    }

}
