package sauce.core;

import sauce.util.AssetDisposedException;
import sauce.util.Disposable;
import sauce.util.RSauceLogger;
import sauce.util.structures.Vector2D;
import sauce.util.structures.nonsaveable.ArrayGrid;
import sauce.util.structures.nonsaveable.ArrayList;
import sauce.util.structures.nonsaveable.Map;

import java.io.IOException;

import static sauce.core.GraphicsUtil.getGraphicInfo;
import static sauce.core.GraphicsUtil.ioResourceToImage;
import static sauce.core.ResourceUtil.loadResource;

/**
 * Created by John Crockett.
 */
public class Sprite implements Disposable {

    private Vector2D center;
    private TextureAtlas.TextureRegion region;

    // Sprite Properties
    private int cellsInRow;
    private int cellsInColumn;
    private Map<String, ArrayList<Vector2D>> idMap;
    private boolean loop;
    private double frameLimit;
    private int width;
    private int height;
    private int components;

    private Vector2D origin = Vector2D.create(0, 0);
    private float angle = 0.0f;
    private float xScale = 1.0f;
    private float yScale = 1.0f;

    private Vector2D cellCoords = Vector2D.create(0,0);
    private ArrayList<Vector2D> animationState = new ArrayList<>();
    private int animationStateIndex = 0;
    private String animStateID;

    private String sourceString;
    private boolean disposed = false;

    public Sprite(String fileSource, int horizontalCount, int verticalCount,  ArrayGrid<String> idMatrix, boolean looping, int fps){


        sourceString = fileSource;
        cellsInRow = horizontalCount;
        cellsInColumn = verticalCount;
        loop = looping;

        // Setup IdMap
        animStateID = idMatrix.get(0, 0);
        idMap = new Map<>();
        idMap.put(animStateID, animationState);
        animationState.add(Vector2D.create(0, idMatrix.height() - 1));

        int i = 1, j = 0;
        while(j < idMatrix.height()){
            while(i < idMatrix.width()){
                if(idMap.containsKey(idMatrix.get(i, j)))
                    idMap.get(idMatrix.get(i, j)).add(Vector2D.create(i, idMatrix.height() - 1 - j));

                else if(idMatrix.get(i, j) != null) {
                    idMap.put(idMatrix.get(i, j), new ArrayList<>());
                    idMap.get(idMatrix.get(i, j)).add(Vector2D.create(i, idMatrix.height() - 1 - j));
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
        GraphicsUtil.IOGraphic image = ioResourceToImage(resource, info);

        width = info.getWidth() / cellsInRow;
        height = info.getHeight() / cellsInColumn;

        components = info.getComponents();
        region = TextureAtlas.register(this, info.getWidth(), info.getHeight(), info.getComponents(), image.getBuffer(), false);
        findCenter();
    }

    public Sprite(String fileSource, int horizontalCount, int verticalCount, boolean looping, int fps) {
        sourceString = fileSource;
        cellsInRow = horizontalCount;
        cellsInColumn = verticalCount;
        loop = looping;

        for(int i = 0; i < cellsInColumn; i++){
            for(int j = 0; j < cellsInRow; j++){
                animationState.add(Vector2D.create(i, j));
            }
        }

        frameLimit = 1.0 / fps;

        ResourceUtil.IOResource resource;

        try{
            resource = loadResource(fileSource);
        } catch(IOException e){
            throw new RuntimeException();
        }

        GraphicsUtil.GraphicInfo info = getGraphicInfo(resource);
        GraphicsUtil.IOGraphic image = ioResourceToImage(resource, info);

        width = info.getWidth() / cellsInRow;
        height = info.getHeight() / cellsInColumn;

        components = info.getComponents();
        region = TextureAtlas.register(this, info.getWidth(), info.getHeight(), info.getComponents(), image.getBuffer(), false);
        findCenter();
    }

    public Sprite(String fileSource){
        this(fileSource, 1, 1, false, 0);

    }

    public Sprite(Surface source, int horizontalCount, int verticalCount,  ArrayGrid<String> idMatrix, boolean looping, int fps){
        sourceString = source.toString();
        cellsInRow = horizontalCount;
        cellsInColumn = verticalCount;
        loop = looping;

        // Setup IdMap
        animStateID = idMatrix.get(0, 0);
        idMap = new Map<>();
        idMap.put(animStateID, animationState);
        animationState.add(Vector2D.create(0, 0));

        int i = 1, j = 0;
        while(j < idMatrix.height()){
            while(i < idMatrix.width()){
                if(idMap.containsKey(idMatrix.get(i, j)))
                    idMap.get(idMatrix.get(i, j)).add(Vector2D.create(i, j));

                else if(idMatrix.get(i, j) != null) {
                    idMap.put(idMatrix.get(i, j), new ArrayList<>());
                    idMap.get(idMatrix.get(i, j)).add(Vector2D.create(i, j));
                }
                i++;
            }
            j++;
            i = 0;
        }

        frameLimit = 1.0 / fps;

        width = source.getWidth() / cellsInRow;
        height = source.getHeight() / cellsInColumn;

        components = source.components();
        region = TextureAtlas.register(source, source.getWidth(), source.getHeight(), source.components(), null, false);
        findCenter();
    }

    public Sprite(Surface source, int horizontalCount, int verticalCount, boolean looping, int fps){
        sourceString = source.toString();
        cellsInRow = horizontalCount;
        cellsInColumn = verticalCount;
        loop = looping;

        for(int i = 0; i < cellsInColumn; i++){
            for(int j = 0; j < cellsInRow; j++){
                animationState.add(Vector2D.create(i, j));
            }
        }

        frameLimit = 1.0 / fps;

        width = source.getWidth() / cellsInRow;
        height = source.getHeight() / cellsInColumn;

        components = source.components();
        region = TextureAtlas.register(source, source.getWidth(), source.getHeight(), source.components(), null, false);
        findCenter();
    }

    public Sprite(Surface source){
        this(source, 1, 1, false, 0);
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
        checkDisposed();
        disposed = true;
        region.removeReference();
    }

    public void setAnimationState(String state){
        if(animStateID == null) {
            RSauceLogger.printWarningln("No IDMap given to construction of Sprite; cannot set animationState");
            return;
        }

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

    public void setAngle(float degrees){
        angle = degrees;
    }

    public float getAngle(){
        return angle;
    }

    public void setXScale(float factor){
        xScale = factor;
    }

    public float getXScale(){
        return xScale;
    }

    public void setYScale(float factor){
        yScale = factor;
    }

    public float getYScale(){
        return yScale;
    }

    public Vector2D getOrigin(){
        return origin;
    }

    public void setOrigin(Vector2D newOrigin){
        origin = newOrigin;
    }

    public void setOrigin(int x, int y){
        origin = Vector2D.create(x, y);
    }

    float[] regionCoordinates() {
        checkDisposed();
        float[] coords = GraphicsUtil.getRegionCoordinatesAdjustedForTextureRegion(getRegion());
        float texelWidth = coords[2] - coords[0];
        float texelHeight = coords[5] - coords[1];
        float w = texelWidth / cellsInRow;
        float h = texelHeight / cellsInColumn;
        float x = w * cellCoords.getX();
        float y = h * cellCoords.getY();

        return new float[]{x + coords[0], y + coords[1], x + w + coords[0], y + coords[1], x + w + coords[0], y + h + coords[1], x + coords[0], y + h + coords[1]};
    }

    int textureID() {
        checkDisposed();
        return getRegion().page.textureID();
    }

    @Override
    public String toString() {
        return sourceString;
    }

    private void findCenter(){
        center =  Vector2D.create(getWidth() / 2, getHeight() / 2);
    }

    public int width(){
        return getWidth();
    }

    public int height(){
        return getHeight();
    }

    protected void checkDisposed(){
        if(disposed)
            throw new AssetDisposedException(this);

    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    Vector2D getCenter() {
        return center;
    }

    TextureAtlas.TextureRegion getRegion() {
        return region;
    }
}
