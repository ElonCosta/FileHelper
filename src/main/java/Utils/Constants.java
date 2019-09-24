package Utils;

import java.util.ArrayList;
import java.util.List;

public class Constants {

    public enum GVE {
        DISPLAY_TIME("displayTime"),
        VERSION_FOLDER("versionFolder"),
        ROOT_FOLDER("rootFolder"),
        ROUTINE_TIME("routineTime"),
        ARCHIVE_FOLDER("archiveFolder");

        private String var;

        GVE(String var){
            this.var = var;
        }

        public String getVar(){
            return var;
        }
    }
    public enum PVE {
        PATH("path"),
        DEST("dest"),
        DISABLED("disabled");

        private String var;

        PVE(String var){
            this.var = var;
        }

        public String getVar() {
            return var;
        }
    }
    public enum DVE {
        ARCHIVE_FILE("archiveFile"),
        NAME("name"),
        PATHS("Paths"),
        LAST_MOD("lastMod");

        private String var;

        DVE(String var){
            this.var = var;
        }

        public String getVar() {
            return var;
        }
    }

    public static List<String> glbVars = new ArrayList<>();
    public static List<String> pathsVars = new ArrayList<>();
    public static List<String> dataVars = new ArrayList<>();

    static {
        for (GVE g: GVE.values()){
            glbVars.add(g.var);
        }
        for (PVE p: PVE.values()){
            pathsVars.add(p.var);
        }
        for (DVE p: DVE.values()){
            dataVars.add(p.var);
        }
    }
}
