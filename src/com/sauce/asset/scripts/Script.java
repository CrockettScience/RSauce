package com.sauce.asset.scripts;


/**
 *
 * @author Jonathan Crockett
 */
public abstract class Script<ArgumentType extends Argument, ReturnType extends Return>{


    protected String scriptLog = "";

    protected abstract ReturnType scriptMain(ArgumentType args);

    public final ReturnType execute(ArgumentType args){
        try{
            return scriptMain(args);
        }
        catch(Throwable e){
            System.out.println("Could not execute script '" + this + "'");
        }

        return null;
    }

    public abstract String toString();

}
