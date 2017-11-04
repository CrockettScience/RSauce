package sauce.asset.graphics;

import sauce.util.io.ResourceUtil;

import java.io.IOException;

import static sauce.asset.graphics.GraphicsUtil.getGraphicInfo;
import static sauce.asset.graphics.GraphicsUtil.ioResourceToImage;
import static sauce.util.io.ResourceUtil.loadResource;

/**
 * Created by John Crockett.
 */
public class TiledTexture extends Graphic {
    private GraphicsUtil.IOGraphic ioGraphic;

    public TiledTexture(String fileSource, int width, int height){
        ResourceUtil.IOResource resource;

        try{
            resource = loadResource(fileSource);
        } catch(IOException e){
            throw new RuntimeException();
        }

        GraphicsUtil.GraphicInfo info = getGraphicInfo(resource);

        ioGraphic = ioResourceToImage(resource, info);
        resize(info.getWidth(), info.getHeight(), TextureAtlas.register(fileSource, ioGraphic, true));
    }

    @Override
    protected float[] regionCoordinates() {
        checkDisposed();
        float w = (float)width() / (float)getIOImage().getGraphicInfo().getWidth();
        float h = (float)height() / (float)getIOImage().getGraphicInfo().getHeight();

        return new float[]{ 0, 0, w, 0, w, -h, 0, -h};
    }

    public void resize(int width, int height){
        checkDisposed();

        resize(width, height, region);
    }

    @Override
    protected int components() {
        checkDisposed();
        return ioGraphic.getGraphicInfo().getComponents();
    }

    @Override
    protected int textureID() {
        checkDisposed();
        return region.page.textureID();
    }

    @Override
    public GraphicsUtil.IOGraphic getIOImage() {
        checkDisposed();
        return ioGraphic;
    }

    @Override
    public void update(double delta) {
        checkDisposed();
    }
}
