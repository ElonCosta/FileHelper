package br.com.claw.utils;

import lombok.Getter;

import java.io.File;

public class Utils {

    /*IMAGES FILES*/
    public static final File CheckThisFile = new File("./Images/checkThis.svg");
    public static final File Check         = new File("./Images/check.svg");
    public static final File Pause         = new File("./Images/paused.svg");
    public static final File remove        = new File("./Images/remove.svg");

    public enum UIVE {
        VERSION("1.2b"),
        TITLE("File Helper v" + VERSION.var);

        @Getter
        String var;

        UIVE(String var){
            this.var = var;
        }
    }


    public static String getShorthandPath(File file){
        return getShorthandPath(file, true);
    }

    public static String getShorthandPath(File file, boolean x){
        File parent;
        if(file.getParent() != null){
            parent = new File(file.getParent());
        }else{
            return file.getName();
        }
        if(x){
            return "...\\" + parent.getName() + "\\" + file.getName();
        }else{
            return "\\"+parent.getName() + "\\" + file.getName();
        }
    }
}
