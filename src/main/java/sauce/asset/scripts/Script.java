package sauce.asset.scripts;


import util.RSauceLogger;

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
            RSauceLogger.printErrorln("An exception was thrown in Script '" + this + "':");
            e.printStackTrace();
        }

        return null;
    }

    public final String toString(){
        return getClass().getSimpleName();
    }

}
