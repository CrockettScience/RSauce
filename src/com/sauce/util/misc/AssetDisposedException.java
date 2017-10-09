package com.sauce.util.misc;

public class AssetDisposedException extends RuntimeException {
    private String className;

    public AssetDisposedException(Disposable o){
        className = o.getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return className + " has been previously disposed.";
    }
}
