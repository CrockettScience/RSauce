package sauce.asset.graphics;

import sauce.core.engine.Preferences;
import util.Color;
import util.Vector2D;
import util.structures.nonsaveable.LinkedList;
import util.structures.nonsaveable.Map;

import java.util.Iterator;

import static org.lwjgl.opengl.GL11.*;
import static sauce.core.engine.Preferences.TEXTURE_PAGE_SIZE;

public class TextureAtlas{

    private static LinkedList<Page> pages = new LinkedList<>();
    private static Map<String, TextureRegion> regionMap = new Map<>();

    private TextureAtlas(){
        pages.add(new Page());
    }

    static TextureRegion register(String source, GraphicsUtil.IOGraphic ioGraphic, boolean exclusive){

        if(regionMap.containsKey(source))
            return regionMap.get(source).addReference();

        TextureRegion region = null;

        if(ioGraphic.getGraphicInfo().getWidth() > Preferences.TEXTURE_PAGE_SIZE || ioGraphic.getGraphicInfo().getHeight() > Preferences.TEXTURE_PAGE_SIZE || exclusive){
            // Texture is to big to fit on page
            region = new TextureRegion(0, 0, ioGraphic.getGraphicInfo().getWidth(), ioGraphic.getGraphicInfo().getHeight(), new Surface(ioGraphic.getGraphicInfo().getWidth(), ioGraphic.getGraphicInfo().getHeight())){
                @Override
                public void removeReference() {
                    super.removeReference();
                    page.dispose();
                }
            };

            region.drawGraphicToRegion(new GraphicEntry(ioGraphic));

        } else {

            Iterator<Page> i = pages.iterator();
            GraphicEntry entry = new GraphicEntry(ioGraphic);

            while (region == null) {
                if (i.hasNext()) {
                    region = i.next().insert(entry);
                } else {
                    Page newPage = new Page();
                    pages.add(newPage);
                    region = newPage.insert(entry);
                }
            }

        }

        regionMap.put(source, region);
        region.key = source;
        return region;
    }

    public static int getPageCount(){
        return pages.size();
    }

    public static  Surface getPageSurface(int i){
        return pages.get(i).pageSurface;
    }

    static class TextureRegion implements Comparable<TextureRegion>{
        Vector2D p00;
        Vector2D p01;
        Vector2D p10;
        Vector2D p11;
        Surface page;

        private int size;
        private int referenceCount;
        private String key = "";

        private boolean occupied = false;

        private TextureRegion(int x, int y, int width, int height, Surface id){
            p00 = Vector2D.create(x, y);
            p01 = Vector2D.create(x, y + height);
            p10 = Vector2D.create(x + width, y);
            p11 = Vector2D.create(x + width, y + height);
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
                other.p00 = Vector2D.create(p00.getX() + size + 1, p00.getY());
                other.p01 = Vector2D.create(p01.getX() + size + 1, p01.getY());
                other.p10 = p10;
                other.p11 = p11;

                p10 = Vector2D.create(p00.getX() + size, p00.getY());
                p11 = Vector2D.create(p01.getX() + size, p01.getY());
            }
            else{
                other.p00 = Vector2D.create(p01.getX(), p00.getY() + size + 1);
                other.p10 = Vector2D.create(p10.getX(), p10.getY() + size + 1);
                other.p01 = p01;
                other.p11 = p11;

                p01 = Vector2D.create(p00.getX(), p00.getY() + size);
                p11 = Vector2D.create(p10.getX(), p10.getY() + size);
            }

            setSize();
            other.setSize();

            return other;
        }

        private void combine(TextureRegion other){
            // Is Horizontal
            if(p00.getY() == other.p00.getY()){
                p10 = other.p10;
                p11 = other.p11;
            }
            // Is Vertical
            else if(p00.getX() == other.p00.getX()){
                p01 = other.p01;
                p11 = other.p11;
            }

            else
                throw new RuntimeException("Merging of TextureRegions in Texture " + page.textureID() + "failed!");

            other.removeReference();

            setSize();
        }

        private void setSize(){
            size = (p11.getX() - p00.getX()) * (p11.getY() - p00.getY());
        }

        private void drawGraphicToRegion(GraphicEntry graphic){

            page.bind();
            {

                glDisable(GL_BLEND);

                glBindTexture(GL_TEXTURE_2D, graphic.texID);

                GraphicsUtil.applyIOImageForDrawing(graphic.ioGraphic, graphic.width, graphic.height, graphic.ioGraphic.getGraphicInfo().getComponents());

                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

                glEnable(GL_TEXTURE_2D);

                glPushMatrix();

                glBegin(GL_QUADS);
                {
                    glTexCoord2f(0, 0);
                    glVertex2f(p00.getX(), p00.getY());

                    glTexCoord2f(1, 0);
                    glVertex2f(p00.getX() + graphic.width, p00.getY());

                    glTexCoord2f(1, 1);
                    glVertex2f(p00.getX() + graphic.width, p00.getY() + graphic.height);

                    glTexCoord2f(0, 1);
                    glVertex2f(p00.getX(), p00.getY() + graphic.height);
                }
                glEnd();

                glPopMatrix();


                glEnable(GL_BLEND);
            }
            page.unbind();

            occupied = true;
            referenceCount = 1;
            graphic.dispose();
        }

        private int compareGraphic(GraphicEntry graphic){
            if(compareWidth(graphic) == 0 && compareHeight(graphic) == 0)
                return 0;
            if(compareWidth(graphic) < 0 || compareHeight(graphic) < 0)
                return -1;

            return 1;
        }

        private int compareWidth(GraphicEntry graphic){

            return (p11.getX() - p00.getX()) - graphic.width;
        }

        private int compareHeight(GraphicEntry graphic){
            return (p11.getY() - p00.getY()) - graphic.height;
        }

        @Override
        public int compareTo(TextureRegion o) {
            return size - o.size;
        }

        private TextureRegion addReference(){
            if(occupied)
                referenceCount++;

            return this;
        }

        public void removeReference() {
            if(--referenceCount <= 0) {
                occupied = false;
                regionMap.remove(key);
            }
        }

    }

    private static class Node {
        private Node left;
        private Node right;
        private TextureRegion region;

        private Node(){}

        private Node(TextureRegion reg){
            region = reg;
        }

        public boolean isLeaf(){
            return region != null;
        }
    }

    private static class Page {
        private Node root = new Node();
        private Surface pageSurface;

        public Page(){
            pageSurface = new Surface(TEXTURE_PAGE_SIZE, TEXTURE_PAGE_SIZE);
            pageSurface.clear(Color.C_RED, 0);
            root.region = new TextureRegion(0, 0, TEXTURE_PAGE_SIZE, TEXTURE_PAGE_SIZE, pageSurface);

        }

        private TextureRegion insert(GraphicEntry graphic){
            Node node = insert(root, graphic);
            if(node != null){
                node.region.drawGraphicToRegion(graphic);
                return node.region;
            }

            return null;
        }

        private Node insert(Node node, GraphicEntry graphic){
            if(node.isLeaf()){
                TextureRegion region = node.region;

                if(region.occupied)
                    return null;

                if(region.compareGraphic(graphic) < 0)
                    return null;

                if(region.compareGraphic(graphic) > 0) {

                    if (region.compareWidth(graphic) > region.compareHeight(graphic)) {
                        TextureRegion otherRegion = region.divide(true, graphic.width);
                        node.region = null;
                        node.left = new Node(region);
                        node.right = new Node(otherRegion);

                        return insert(node.left, graphic);
                    }

                    TextureRegion otherRegion = region.divide(false, graphic.height);
                    node.region = null;
                    node.left = new Node(region);
                    node.right = new Node(otherRegion);
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

    private static class GraphicEntry{
        private int texID;
        private int width;
        private int height;
        private GraphicsUtil.IOGraphic ioGraphic;


        private GraphicEntry(GraphicsUtil.IOGraphic io){
            texID = glGenTextures();
            width = io.getGraphicInfo().getWidth();
            height = io.getGraphicInfo().getHeight();
            ioGraphic = io;
        }

        private void dispose(){
            glDeleteTextures(texID);
        }

    }

}
