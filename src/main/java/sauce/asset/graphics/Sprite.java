package sauce.asset.graphics;

import sauce.util.io.ResourceUtil;
import util.structures.nonsaveable.ArrayGrid;
import util.structures.nonsaveable.ArrayList;
import util.structures.nonsaveable.Map;
import util.Vector2D;

import java.io.IOException;

import static sauce.asset.graphics.GraphicsUtil.getGraphicInfo;
import static sauce.asset.graphics.GraphicsUtil.ioResourceToImage;
import static sauce.util.io.ResourceUtil.loadResource;

/**
 * Created by John Crockett.
 */
public class Sprite extends Graphic {

    // Sprite Properties
    private int cellsInRow;
    private int cellsInColumn;
    private Map<String, ArrayList<Vector2D>> idMap;
    private boolean loop;
    private double frameLimit;
    private GraphicsUtil.IOGraphic image;
    private int components;

    //Sprite State Variables;
    private Vector2D cellCoords = new Vector2D(0,0);
    private ArrayList<Vector2D> animationState = new ArrayList<>();
    private int animationStateIndex = 0;
    private String animStateID;

    public Sprite(String fileSource, int horizontalCount, int verticalCount,  ArrayGrid<String> idMatrix, boolean looping, int fps){
        cellsInRow = horizontalCount;
        cellsInColumn = verticalCount;
        loop = looping;

        // Setup IdMap
        animStateID = idMatrix.get(0, 0);
        idMap = new Map<>();
        idMap.put(animStateID, animationState);
        animationState.add(new Vector2D(0, 0));

        int i = 1, j = 0;
        while(j < idMatrix.height()){
            while(i < idMatrix.width()){
                if(idMap.containsKey(idMatrix.get(i, j)))
                    idMap.get(idMatrix.get(i, j)).add(new Vector2D(i, j));

                else if(idMatrix.get(i, j) != null) {
                    idMap.put(idMatrix.get(i, j), new ArrayList<>());
                    idMap.get(idMatrix.get(i, j)).add(new Vector2D(i, j));
                }
                i++;
            }
            j++;
            i = 0;
        }

        frameLimit = 1.0 / fps;


        ResourceUtil.IOResource resource;

        try{
            resource = loadResource(fileSource);
        } catch(IOException e){
            throw new RuntimeException();
        }

        GraphicsUtil.GraphicInfo info = getGraphicInfo(resource);

        image = ioResourceToImage(resource, info);
        components = info.getComponents();
        resize(info.getWidth() / cellsInRow, info.getHeight() / cellsInColumn, TextureAtlas.register(fileSource, image, false));

    }

    private double timeSinceLastUpdate;
    public void update(double delta){
        checkDisposed();
        timeSinceLastUpdate += delta;
        if(timeSinceLastUpdate >= frameLimit) {
            if (animationStateIndex >= animationState.size()) {
                if (loop)
                    animationStateIndex = 0;
                else
                    animationStateIndex--;

            }

            cellCoords = animationState.get(animationStateIndex++);


            timeSinceLastUpdate = 0;
        }

    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public void setAnimationState(String state){
        checkDisposed();
        if(!state.equals(animStateID)){
            animationState = idMap.get(state);
            animationStateIndex = 0;
            cellCoords = animationState.get(animationStateIndex++);
            animStateID = state;
        }

    }

    public String currentAnimationStateIdentifier(){
        checkDisposed();
        return animStateID;
    }

    @Override
    protected float[] regionCoordinates() {
        checkDisposed();
        float[] coords = GraphicsUtil.getRegionCoordinatesAdjustedForTextureRegion(region);
        float texelWidth = coords[2] - coords[0];
        float texelHeight = coords[5] - coords[1];
        float w = texelWidth / cellsInRow;
        float h = texelHeight / cellsInColumn;
        float x = w * cellCoords.getX();
        float y = h * cellCoords.getY();

        return new float[]{x + coords[0], y + coords[1], x + w + coords[0], y + coords[1], x + w + coords[0], y + h + coords[1], x + coords[0], y + h + coords[1]};
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
    public GraphicsUtil.IOGraphic getIOImage() {
        checkDisposed();
        return image;
    }
}
