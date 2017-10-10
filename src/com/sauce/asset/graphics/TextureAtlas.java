package com.sauce.asset.graphics;

import static com.sauce.core.Preferences.TEXTURE_PAGE_SIZE;
import static com.sauce.util.io.GraphicsUtil.applyIOImageForDrawing;
import static org.lwjgl.opengl.GL11.*;

import com.util.Vector2D;
import com.util.structures.nonsaveable.LinkedList;
import com.util.structures.nonsaveable.Map;

import java.util.Iterator;


public class TextureAtlas<K> {

    private Map<K, TextureRegion> textureMap = new Map<>();
    private LinkedList<Page> pages = new LinkedList<>();

    public TextureRegion getTexture(K key){
        return textureMap.get(key);
    }

    public boolean containsTexture(K key){
        return textureMap.containsKey(key);
    }

    public void removeTexture(K key){
        textureMap.remove(key).remove();
    }

    public void putTexture(K key, Graphic graphic){
        Iterator<Page> i = pages.iterator();
        TextureRegion region = null;

        while(region == null){
            if (i.hasNext()) {
                region = i.next().insert(graphic);
            } else {
                Page newPage = new Page();
                pages.add(newPage);
                region = newPage.insert(graphic);
            }
        }

        textureMap.put(key, region);
    }

    public class TextureRegion implements Comparable<TextureRegion>{
        private Vector2D p00;
        private Vector2D p01;
        private Vector2D p10;
        private Vector2D p11;
        private int size;
        private Surface page;

        private boolean occupied = false;

        private TextureRegion(int x, int y, int width, int height, Surface id){
            p00 = new Vector2D(x, y);
            p01 = new Vector2D(x, y + height);
            p10 = new Vector2D(x + width, y);
            p11 = new Vector2D(x + width, y + height);
            page = id;
            setSize();
        }

        private TextureRegion(Surface id){
            p00 = null;
            p01 = null;
            p10 = null;
            p11 = null;
            page = id;
        }

        private TextureRegion divide(boolean horizontally, int size){
            TextureRegion other = new TextureRegion(page);
            if(horizontally){
                other.p00 = new Vector2D(p00.getX() + size + 1, p00.getY());
                other.p01 = new Vector2D(p01.getX() + size + 1, p01.getY());
                other.p10 = p10;
                other.p11 = p11;

                p10 = new Vector2D(p00.getX() + size, p00.getY());
                p11 = new Vector2D(p01.getX() + size, p01.getY());
            }
            else{
                other.p00 = new Vector2D(p01.getX(), p00.getY() + size + 1);
                other.p10 = new Vector2D(p10.getX(), p10.getY() + size + 1);
                other.p01 = p01;
                other.p11 = p11;

                p01 = new Vector2D(p00.getX(), p00.getY() + size);
                p11 = new Vector2D(p10.getX(), p11.getY() + size);
            }

            setSize();
            other.setSize();

            return other;
        }

        private void combine(TextureRegion other){
            // Is Horizontal
            if(p00.getX() == other.p00.getX()){
                p10 = other.p10;
                p11 = other.p11;
            }
            // Is Vertical
            else if(p00.getY() == other.p00.getY()){
                p01 = other.p01;
                p11 = other.p11;
            }

            else
                throw new RuntimeException("Merging of TextureRegions in Texture " + page.textureID() + "failed!");

            other.remove();

            setSize();
        }

        private void setSize(){
            size = (p11.getX() - p00.getX()) * (p11.getY() - p00.getY());
        }

        private void drawGraphicToRegion(Graphic graphic){
            if(compareGraphic(graphic) == -1){
                throw new RuntimeException("TextureRegion too small to draw graphic onto.");
            }

            glBindTexture(GL_TEXTURE_2D, page.textureID());

            if(graphic.getIOImage() != null)
                applyIOImageForDrawing(graphic.getIOImage(), graphic.absWidth(), graphic.absHeight(), graphic.components());

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

            glEnable(GL_TEXTURE_2D);

            page.bind();

            glPushMatrix();

            glBegin(GL_QUADS);
            {
                glTexCoord2f(0, 0);
                glVertex2f(p00.getX(), p00.getY());

                glTexCoord2f(1, 0);
                glVertex2f(p10.getX(), p10.getY());

                glTexCoord2f(1, 1);
                glVertex2f(p11.getX(), p11.getY());

                glTexCoord2f(0, 1);
                glVertex2f(p01.getX(), p01.getY());
            }
            glEnd();

            glPopMatrix();

            page.unbind();

            occupied = true;
        }

        private int compareGraphic(Graphic graphic){
            if(compareWidth(graphic) == 0 && compareHeight(graphic) == 0)
                return 0;
            if(compareWidth(graphic) < 0 || compareHeight(graphic) < 0)
                return -1;

            return 1;
        }

        private int compareWidth(Graphic graphic){
            if(graphic.absWidth() == p11.getX() - p00.getX())
                return 0;
            if(graphic.absWidth() > p11.getX() - p00.getX())
                return -1;

            return 1;
        }

        private int compareHeight(Graphic graphic){
            if(graphic.absHeight() == p11.getY() - p00.getY())
                return 0;
            if(graphic.absHeight() > p11.getY() - p00.getY())
                return -1;

            return 1;
        }

        @Override
        public int compareTo(TextureRegion o) {
            return size - o.size;
        }

        public void remove() {
            occupied = false;
        }
    }

    private class Node {
        private Node left;
        private Node right;
        private TextureRegion region;

        public boolean isLeaf(){
            return region != null;
        }
    }

    private class Page {
        private Node root = new Node();
        private Surface pageSurface;

        public Page(){
            pageSurface = new Surface(TEXTURE_PAGE_SIZE, TEXTURE_PAGE_SIZE);
            root.region = new TextureRegion(0, 0, TEXTURE_PAGE_SIZE, TEXTURE_PAGE_SIZE, pageSurface);

        }

        private TextureRegion insert(Graphic graphic){
            Node node = insert(root, graphic);
            if(node != null){
                node.region.drawGraphicToRegion(graphic);
                return node.region;
            }

            return null;
        }

        private Node insert(Node node, Graphic graphic){
            if(node.isLeaf()){
                TextureRegion region = node.region;

                if(region.occupied)
                    return null;

                if(region.compareGraphic(graphic) < 0)
                    return null;

                if(region.compareWidth(graphic) > 0){
                    TextureRegion otherRegion = region.divide(true, graphic.absWidth());
                    node.region = null;
                    node.left.region = region;
                    node.right.region = otherRegion;

                    return insert(node.left, graphic);
                }

                if(region.compareHeight(graphic) > 0){
                    TextureRegion otherRegion = region.divide(false, graphic.absWidth());
                    node.region = null;
                    node.left.region = region;
                    node.right.region = otherRegion;
                    return insert(node.left, graphic);
                }

                return node;
            }else{
                if(node.left.isLeaf() && !node.left.region.occupied && node.right.isLeaf() && !node.right.region.occupied){
                    node.region = node.left.region;
                    node.region.combine(node.right.region);
                    node.left = null;
                    node.right = null;

                    return insert(node, graphic);
                }


                Node newNode = insert(node.left, graphic);
                if(newNode != null)
                    return newNode;
                else
                    return insert(node.right, graphic);
            }
        }
    }

}
