package sauce.asset.graphics;

import sauce.util.io.GraphicsUtil;

/**
 * Created by John Crockett.
 */
public class TiledTexture extends Graphic {
    private Image image;

    public TiledTexture(String fileSource, int width, int height){
        image = new Image(fileSource);

        resize(width, height, image.width, image.height);
    }

    @Override
    protected float[] regionCoordinates() {
        checkDisposed();
        float w = (float)width() / (float)actualWidth();
        float h = (float)height() / (float)actualHeight();

        float[] arr = { 0, 0, w, 0, w, -h, 0, -h};
        return arr;
    }

    public void resize(int width, int height){
        checkDisposed();

        resize(width, height, image.width, image.height);
    }

    @Override
    protected int components() {
        checkDisposed();
        return image.components();
    }

    @Override
    protected int textureID() {
        checkDisposed();
        return image.textureID();
    }

    @Override
    public GraphicsUtil.IOGraphic getIOImage() {
        checkDisposed();
        return image.getIOImage();
    }

    @Override
    public void update(double delta) {
        checkDisposed();
    }

    @Override
    public void dispose(){
        super.dispose();
        image.dispose();
    }
}
