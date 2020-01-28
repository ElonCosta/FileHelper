package Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static Utils.Utils.KEY.*;

public class Utils {

    public static final URL AppUI      = Utils.class.getResource("/UI/FXML/AppUI.fxml");
    public static final URL LogUI      = Utils.class.getResource("/UI/FXML/LogUI.fxml");
    public static final URL ConfigUI   = Utils.class.getResource("/UI/FXML/ConfigurationsUI.fxml");
    public static final URL FilesUI    = Utils.class.getResource("/UI/FXML/File/FileUI.fxml");
    public static final URL FilesTabUI = Utils.class.getResource("/UI/FXML/File/FileTab.fxml");
    public static final URL PathsTabUI = Utils.class.getResource("/UI/FXML/File/PathsTab.fxml");
    public static final URL NewFileUI  = Utils.class.getResource("/UI/FXML/NewFile/NewFileUI.fxml");

    public static KEY[] globalKeys = new KEY[]{DISPLAY_TIME,ARCHIVE_FILES, VERSION_FOLDER, ROOT_FOLDER, ROUTINE_TIME, ARCHIVE_FOLDER, HASH_KEY};
    public static KEY[] configKeys = new KEY[]{CFG_VERSION, GLOBAL, DATA_FILES};
    public enum KEY {

        //Configurations Master Keys
        CFG_VER("1.0"),
        CFG_VERSION("cfgVersion"),
        GLOBAL("Global"),
        DATA_FILES("DataFiles"),

        //Global keys
        DISPLAY_TIME("displayTime"),
        ARCHIVE_FILES("archiveFiles"),
        VERSION_FOLDER("versionFolder"),
        ROOT_FOLDER("rootFolder"),
        ROUTINE_TIME("routineTime"),
        ARCHIVE_FOLDER("archiveFolder"),
        HASH_KEY("key"),

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
        VERSION("1.0.1"),
        TITLE("File Helper v" + VERSION.var),
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

    public static void put(JSONObject jo, KEY key, Object value){
        jo.put(key.getVar(),value);
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

    public static Float getFloat(JSONObject jo, KEY key){
        return jo.getFloat(key.getVar());
    }

    public static JSONObject getJSONObject(JSONObject jo, KEY key){
        return jo.getJSONObject(key.getVar());
    }

    public static JSONArray getJSONArray(JSONObject jo, KEY key){
        return jo.getJSONArray(key.getVar());
    }

    public static boolean isNull(JSONObject jo, KEY key){
        return jo.isNull(key.getVar());
    }

    public static Integer getInteger(JSONObject jo, KEY key){
        return jo.getInt(key.getVar());
    }

    public static String getShorthandPath(File file){
        return getShorthandPath(file, true);
    }

    public static String getShorthandPath(File file, boolean x){
        File parent = new File(file.getParent());
        if(x){
            return "...\\" + parent.getName() + "\\" + file.getName();
        }else{
            return "\\"+parent.getName() + "\\" + file.getName();
        }
    }

}
