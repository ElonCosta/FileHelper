package Utils;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;

import static Utils.Utils.KEY.*;

public class Utils {

    /*FXML FILES*/
    public static final URL AppUI         = Utils.class.getResource("/UI/FXML/AppUI.fxml");
    public static final URL ConfigUI      = Utils.class.getResource("/UI/FXML/ConfigurationsUI.fxml");
    public static final URL MonitoringUI  = Utils.class.getResource("/UI/FXML/MonitoringUI.fxml");
    public static final URL FileChooserUI = Utils.class.getResource("/UI/FXML/FileChooser.fxml");

    /*IMAGES FILES*/
    public static final File CheckThisFile = new File("./Images/checkThis.svg");
    public static final File Check         = new File("./Images/check.svg");
    public static final File Pause         = new File("./Images/paused.svg");
    public static final File remove        = new File("./Images/remove.svg");
    public static final File add           = new File("./Images/new.svg");


    public static KEY[] globalKeys = new KEY[]{VERSION_FOLDER, ROOT_FOLDER, ROUTINE_TIME, ARCHIVE_FOLDER, HASH_KEY};
    public static KEY[] configKeys = new KEY[]{CFG_VERSION, GLOBAL, DATA_FILES};
    public enum KEY {

        //Configurations Master Keys
        CFG_VER("1.0"),
        CFG_VERSION("cfgVersion"),
        GLOBAL("Global"),
        DATA_FILES("DataFiles"),

        //Global keys
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

        ID("id"),
        NAME("name"),
        PATH("path");

        @Getter
        private String var;

        KEY(String var){
            this.var = var;
        }

    }
    public enum UIVE {
        VERSION("1.2b"),
        TITLE("File Helper v" + VERSION.var);

        @Getter
        String var;

        UIVE(String var){
            this.var = var;
        }
    }
    public enum STATUS {
        ARCHIVING,
        EDITING,
        NEW,
        READY,
        CHECKING
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

    public static boolean isWritable(KeyEvent e){
        return e.getCode().isDigitKey() || e.getCode().isLetterKey() || e.getCode().equals(KeyCode.BACK_SPACE) || e.getCode().equals(KeyCode.SPACE);
    }

    /* JSON Utils */
    public static void put(JSONObject jo, KEY key, Object value){
        jo.put(key.getVar(),value);
    }

    public static boolean isNull(JSONObject jo, KEY key){
        return jo.isNull(key.getVar());
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
    public static Integer getInteger(JSONObject jo, KEY key){
        return jo.getInt(key.getVar());
    }

}
