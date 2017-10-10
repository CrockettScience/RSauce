package com.sauce.asset.graphics;

import static com.sauce.core.Preferences.TEXTURE_PAGE_SIZE;

import com.util.Vector2D;
import com.util.structures.nonsaveable.Map;
import com.util.structures.special.SortedArrayList;

import java.util.Comparator;


public class TextureAtlas<K> {

    private Map<K, TextureRegion> textureMap = new Map<>();
    private PageList pages = new PageList();

    public TextureRegion getTexture(K key){
        return textureMap.get(key);
    }

    public boolean containsTexture(K key){
        return textureMap.containsKey(key);
    }

    public void putTexture(K key, Graphic graphic){
        int size = graphic.absWidth() * graphic.absHeight();
        int i = pages.binSearch(size, 0, pages.size() - 1);
        Page page = pages.get(i);
        Node node = page.insert(graphic);

        while(node == null){
            i++;

            if(i >= pages.size()){
                Page newPage = new Page();
                pages.add(newPage);
                node = page.insert(graphic);
            }

            else{
                node = pages.get(i).insert(graphic);
            }
        }

        textureMap.put(key, node.region);
    }

    public class TextureRegion implements Comparable<TextureRegion>{
        private Vector2D p00;
        private Vector2D p01;
        private Vector2D p10;
        private Vector2D p11;
        private int size;
        private int texPageID;

        private TextureRegion(int x, int y, int width, int height, int texID){
            p00 = new Vector2D(x, y);
            p01 = new Vector2D(x, y + height);
            p10 = new Vector2D(x + width, y);
            p11 = new Vector2D(x + width, y + height);
            texPageID = texID;
            setSize();
        }

        private TextureRegion(int texID){
            p00 = null;
            p01 = null;
            p10 = null;
            p11 = null;
            texPageID = texID;
        }

        private TextureRegion divide(boolean horizontally, int size){
            TextureRegion other = new TextureRegion(texPageID);
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
                throw new RuntimeException("Merging of TextureRegions in Texture " + texPageID + "failed!");

            setSize();
        }

        private void setSize(){
            size = (p11.getX() - p00.getX()) * (p11.getY() - p00.getY());
        }

        @Override
        public int compareTo(TextureRegion o) {
            return size - o.size;
        }
    }

    private class Node {
        private Node left;
        private Node Right;
        private TextureRegion region;

        public boolean isLeaf(){
            return region != null;
        }
    }

    private class Page implements Comparable<Page>{
        private Node root = new Node();
        private Surface pageSurface;
        private int largestSize;

        public Page(){
            pageSurface = new Surface(TEXTURE_PAGE_SIZE, TEXTURE_PAGE_SIZE);
            largestSize = TEXTURE_PAGE_SIZE * TEXTURE_PAGE_SIZE;
            root.region = new TextureRegion(0, 0, TEXTURE_PAGE_SIZE, TEXTURE_PAGE_SIZE, pageSurface.textureID());

        }

        private Node insert(Graphic graphic){
            return null;
        }

        @Override
        public int compareTo(Page o) {
            return largestSize - o.largestSize;
        }
    }

    private class PageList extends SortedArrayList<Page>{

        public PageList() {
            super(Comparator.naturalOrder());
        }

        public int binSearch(int size, int start, int end){
            int index = (start + end) / 2;

            if(index == start)
                return index;
            if(elements[index].largestSize < size)
                return binSearch(size, index, end);
            if(elements[index].largestSize > size)
                return binSearch(size, start, index);

            return index;

        }
    }

}
