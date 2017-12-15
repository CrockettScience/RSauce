package sauce.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jonathan Crockett
 */
public class Ini {
    private final Path INIPATH;
    private ArrayList<String> ini = new ArrayList();

    public Ini(Path path, String name){
        //create a new ini from scratch
        INIPATH = Paths.get(path.toString(), name + ".ini");
        openIni();
        if(ini.isEmpty()){
            ini.add("");
        }
    }

    public Ini(Path filePath){
        //open an already created ini
        INIPATH = filePath;
        openIni();
        if(ini.isEmpty()){
            ini.add("");
        }
    }

    private void openIni(){

        if(Files.notExists(INIPATH)){
            File f = new File(INIPATH.toUri());
            f.getParentFile().mkdirs();
            try{
                f.createNewFile();
            }catch (IOException ex) {
                Logger.getLogger(Ini.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try{
            ini = (ArrayList<String>) Files.readAllLines(INIPATH);
        }catch (IOException ex) {
            Logger.getLogger(Ini.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    public void save(){

        if(Files.notExists(INIPATH)){
            File f = new File(INIPATH.toUri());
            f.getParentFile().mkdirs();
            try{
                f.createNewFile();
            }catch (IOException ex) {
                Logger.getLogger(Ini.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            Files.write(INIPATH, ini);
        } catch (IOException ex) {
            Logger.getLogger(Ini.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void write(String section, String key, String value){
        //strip all input strings of sub-syntactic characters prior to proceeding to avoid errors
        section = section.replace("=", "");
        section = section.replace("-", "");
        section = section.trim();

        key = key.replace("=", "");
        key = key.replace("-", "");
        key = key.trim();

        value = value.replace("=", "");
        value = value.replace("-", "");
        value = value.trim();

        int writeIndex;
        int sectionIndex = ini.indexOf('-' + section + '-');

        if(sectionIndex == -1){
            writeIndex = ini.size() - 1;
            ini.add(writeIndex, "-" + section + '-');
            writeIndex++;
        }

        else{
            writeIndex = sectionIndex + 1;

            while((writeIndex < ini.size() - 1) && ((ini.get(writeIndex) + " ").charAt(0) != '-') && (!ini.get(writeIndex).split("=")[0].trim().equals(key))){
                writeIndex++;
            }
        }

        if(ini.get(writeIndex).split("=")[0].trim().equals(key)){
            ini.set(writeIndex, key + "=" + value);
        }
        else{
            ini.add(writeIndex, key + "=" + value);
        }


    }

    public boolean isEmpty(){
        return ini.size() <= 1;
    }

    public Path getPath(){
        return INIPATH;
    }

    public String read(String section, String key, String defValue){
        int readIndex;
        int sectionIndex = ini.indexOf('-' + section + '-');

        if(sectionIndex != -1){
            readIndex = sectionIndex;

            while(!ini.get(readIndex).split("=")[0].trim().equals(key)){
                readIndex++;
                if(((readIndex + 1) == ini.size()) || ((ini.get(readIndex) + " ").charAt(0) == '-')){
                    return defValue;
                }
            }

            return ini.get(readIndex).substring(ini.get(readIndex).indexOf('=') + 1).trim();
        }

        else{
            return defValue;
        }
    }

    public boolean readBool(String section, String key, boolean defValue){
        String read = read(section, key, String.valueOf(defValue));

        if("true".equals(read)){
            return true;
        }else if ("false".equals(read)){
            return false;
        }else{
            RSauceLogger.printWarningln("Ini value could not parse to boolean; overwriting.");
            write(section, key, String.valueOf(defValue));
            save();
            return false;
        }
    }

    public int readInt(String section, String key, int defValue){
        int val;

        try{
            val = Integer.parseInt(read(section, key, String.valueOf(defValue)));
        }
        catch(NumberFormatException e){
            RSauceLogger.printWarningln("Ini value could not parse to int; overwriting.");
            write(section, key, String.valueOf(defValue));
            save();
            return defValue;
        }

        return val;
    }

}
