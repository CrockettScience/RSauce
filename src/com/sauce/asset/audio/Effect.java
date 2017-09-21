package com.sauce.asset.audio;

public class Effect extends Audio {

    public Effect(String fileSource) {
        super(fileSource);
    }

    @Override
    protected boolean update() {
        return false;
    }
}
