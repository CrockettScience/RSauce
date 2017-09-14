package com.sauce.input;

import com.util.structures.special.RecyclePool;

/**
 * Created by John Crockett.
 */
public class InputEvent implements RecyclePool.Poolable {

    private int key = -1;
    private int action = -1;
    private int mods = -1;

    public int key(){
        return key;
    }

    public int action(){
        return action;
    }

    public int mods(){
        return mods;
    }

    InputEvent setState(int key, int action, int mods){
        this.key = key;
        this.action = action;
        this.mods = mods;
        return this;
    }

    @Override
    public void clean() {

        key = -1;
        action = -1;
        mods = -1;
    }
}
