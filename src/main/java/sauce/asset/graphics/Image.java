package sauce.asset.graphics;

import java.io.IOException;

import static sauce.asset.graphics.GraphicsUtil.*;
import static sauce.core.coreutil.io.ResourceUtil.IOResource;
import static sauce.core.coreutil.io.ResourceUtil.loadResource;

/**
 * Created by John Crockett.
 */
public class Image extends Graphic {

    // Properties
    private IOGraphic image;
    private int components;

    public Image(String imagePath){
        IOResource resource;

        try{
            resource = loadResource(imagePath);
        } catch(IOException e){
            throw new RuntimeException();
        }

        GraphicInfo info = getGraphicInfo(resource);

        image = ioResourceToImage(resource, info);
        components = info.getComponents();
        resize(info.getWidth(), info.getHeight(), TextureAtlas.register(imagePath, image, false));

    }

    @Override
    protected float[] regionCoordinates() {
        checkDisposed();
        return GraphicsUtil.getRegionCoordinatesAdjustedForTextureRegion(region);
    }

    @Override
    protected int components() {
        checkDisposed();
        return components;
    }

    @Override
    protected int textureID() {
        checkDisposed();
        return region.page.textureID();
    }

    @Override
    public IOGraphic getIOImage() {
        checkDisposed();
        return image;
    }

    @Override
    public void update(double delta) {
        checkDisposed();
    }

}
