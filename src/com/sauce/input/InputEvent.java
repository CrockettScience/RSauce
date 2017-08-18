package com.sauce.input;

/**
 * Created by John Crockett.
 */
public class InputEvent {

    private int key;
    private int action;

    public InputEvent(int key, int action){
        this.key = key;
        this.action = action;
    }

    public int key(){
        return key;
    }

    public int action(){
        return action;
    }
}
