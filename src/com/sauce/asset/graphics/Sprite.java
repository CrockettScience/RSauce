package com.sauce.asset.graphics;

import com.structures.nonsaveable.ArrayGrid;
import com.structures.nonsaveable.ArrayList;
import com.structures.nonsaveable.Map;
import com.util.Coordinates;

import java.nio.ByteBuffer;

/**
 * Created by John Crockett.
 */
public class Sprite extends DrawableAsset{

    // Sprite Properties
    private Image source;
    private int cellsInRow;
    private int cellsInColumn;
    private ArrayGrid<String> animIds;
    private Map<String, ArrayList<Coordinates>> idMap;
    private boolean loop;

    //Sprite State Variables;
    private Coordinates cellCoords = new Coordinates(0,0);
    private ArrayList<Coordinates> animationState = new ArrayList<>();
    private int animationStateIndex = 0;

    public Sprite(String fileSource, int horizontalCount, int verticalCount,  ArrayGrid<String> idMatrix, boolean looping){
        source = new Image(fileSource);
        cellsInRow = horizontalCount;
        cellsInColumn = verticalCount;
        animIds = idMatrix;
        loop = looping;

        // Setup IdMap
        idMap = new Map<>();
        idMap.put(animIds.get(0, 0), animationState);
        animationState.add(new Coordinates(0, 0));

        int i = 1, j = 0;
        while(j < animIds.height()){
            while(i < animIds.width()){
                if(idMap.containsKey(animIds.get(i, j)))
                    idMap.get(animIds.get(i, j)).add(new Coordinates(i, j));

                else if(animIds.get(i, j) != null) {
                    idMap.put(animIds.get(i, j), new ArrayList<>());
                    idMap.get(animIds.get(i, j)).add(new Coordinates(i, j));
                }
                i++;
            }
            j++;
            i = 0;
        }

        super.lateConstructor(source.width() / cellsInRow, source.height() / cellsInColumn, source.width(), source.height());
    }

    public void update(){
        if(animationStateIndex >= animationState.size()){
            if(loop)
                animationStateIndex = 0;
            else
                animationStateIndex--;
        }

        cellCoords = animationState.get(animationStateIndex++);

    }

    public void setAnimationState(String state){
        animationState = idMap.get(state);
        animationStateIndex = 0;
        cellCoords = animationState.get(animationStateIndex++);

    }

    @Override
    protected float[] regionCoordinates() {
        float w = 1.0f / cellsInRow;
        float h = 1.0f / cellsInColumn;
        float x = w * cellCoords.x();
        float y = h * cellCoords.y();

        float[] arr = { x, y, x + w, y, x + w, y + h, x, y + h};
        return arr;
    }

    @Override
    protected int components() {
        return source.components();
    }

    @Override
    protected ByteBuffer imageData() {
        return source.imageData();
    }
}
