package Utils;

import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Constants {

    public enum KEY {

        //Configurations Master Keys
        GLOBAL("Global"),
        FILES("Files"),
        DATA_FILES("DataFiles"),

        //Global keys
        DISPLAY_TIME("displayTime"),
        ARCHIVE_FILES("archiveFiles"),
        VERSION_FOLDER("versionFolder"),
        ROOT_FOLDER("rootFolder"),
        ROUTINE_TIME("routineTime"),
        ARCHIVE_FOLDER("archiveFolder"),

        //DataFile Keys
        ARCHIVE_FILE("archiveFile"),
        LAST_MOD("lastMod"),

        //Paths Keys
        PATHS("Paths"),

        DEST("dest"),
        DISABLED("disabled"),

        NAME("name"),
        PATH("path");


        private String var;

        KEY(String var){
            this.var = var;
        }

        public String getVar(){
            return var;
        }

    }

    public enum UIVE {
        TITLE("File Helper"),
        LOG_BUTTON_NAME("Log"),
        CONFIG_BUTTON_NAME("Configurations"),
        FILES_BUTTON_NAME("Files"),
        NEW_FILE_BUTTON_NAME("New file");

        String var;

        UIVE(String var){
            this.var = var;
        }

        public  String getVar() {
            return var;
        }
    }

    public static void put(JSONObject jo, KEY key, Object ob){
        jo.put(key.getVar(),ob);
    }

    public static Object get(JSONObject jo, KEY key){
        return jo.get(key.getVar());
    }

    public static String getString(JSONObject jo, KEY key){
        return jo.getString(key.getVar());
    }

    public static Boolean getBoolean(JSONObject jo, KEY key){
        return jo.getBoolean(key.getVar());
    }

    public static Integer getInteger(JSONObject jo, KEY key){
        return jo.getInt(key.getVar());
    }

    public static Component cloneSwingComponent(Component c) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(c);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (Component) ois.readObject();
        } catch (IOException |ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
