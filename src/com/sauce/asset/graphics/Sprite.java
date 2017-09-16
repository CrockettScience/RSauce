package com.sauce.asset.graphics;

import com.util.structures.nonsaveable.ArrayGrid;
import com.util.structures.nonsaveable.ArrayList;
import com.util.structures.nonsaveable.Map;
import com.util.Vector2D;

/**
 * Created by John Crockett.
 */
public class Sprite extends DrawableAsset{

    // Sprite Properties
    private Image source;
    private int cellsInRow;
    private int cellsInColumn;
    private ArrayGrid<String> animIds;
    private Map<String, ArrayList<Vector2D>> idMap;
    private boolean loop;
    private int fpms;

    //Sprite State Variables;
    private Vector2D cellCoords = new Vector2D(0,0);
    private ArrayList<Vector2D> animationState = new ArrayList<>();
    private int animationStateIndex = 0;
    private String animStateID;

    public Sprite(String fileSource, int horizontalCount, int verticalCount,  ArrayGrid<String> idMatrix, boolean looping, int fps){
        source = new Image(fileSource);
        cellsInRow = horizontalCount;
        cellsInColumn = verticalCount;
        animIds = idMatrix;
        loop = looping;

        // Setup IdMap
        animStateID = animIds.get(0, 0);
        idMap = new Map<>();
        idMap.put(animStateID, animationState);
        animationState.add(new Vector2D(0, 0));

        int i = 1, j = 0;
        while(j < animIds.height()){
            while(i < animIds.width()){
                if(idMap.containsKey(animIds.get(i, j)))
                    idMap.get(animIds.get(i, j)).add(new Vector2D(i, j));

                else if(animIds.get(i, j) != null) {
                    idMap.put(animIds.get(i, j), new ArrayList<>());
                    idMap.get(animIds.get(i, j)).add(new Vector2D(i, j));
                }
                i++;
            }
            j++;
            i = 0;
        }

        fpms = 1000 / fps;

        resize(source.width() / cellsInRow, source.height() / cellsInColumn, source.width(), source.height());
    }

    private int timeSinceLastUpdate;
    public void update(int delta){
        if(timeSinceLastUpdate >= fpms) {
            if (animationStateIndex >= animationState.size()) {
                if (loop)
                    animationStateIndex = 0;
                else
                    animationStateIndex--;

            }

            cellCoords = animationState.get(animationStateIndex++);


            timeSinceLastUpdate -= fpms;
        }
        else{
            timeSinceLastUpdate += delta;
        }

    }

    @Override
    public void dispose() {
        source.dispose();
    }

    public void setAnimationState(String state){
        if(!state.equals(animStateID)){
            animationState = idMap.get(state);
            animationStateIndex = 0;
            cellCoords = animationState.get(animationStateIndex++);
            animStateID = state;
        }

    }

    public String currentAnimationStateIdentifier(){
        return animStateID;
    }

    @Override
    protected float[] regionCoordinates() {
        float w = 1.0f / cellsInRow;
        float h = 1.0f / cellsInColumn;
        float x = w * cellCoords.getX();
        float y = h * cellCoords.getY();

        float[] arr = { x, y, x + w, y, x + w, y + h, x, y + h};
        return arr;
    }

    @Override
    protected int components() {
        return source.components();
    }

    @Override
    protected int textureID() {
        return source.textureID();
    }
}
