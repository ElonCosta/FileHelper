package ArchiveLoader;

import Utils.ConfigInterface;
import Utils.Constants.*;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import static Main.Launcher.LOG;
import static Utils.Constants.dataVars;
import static Utils.Constants.glbVars;

@SuppressWarnings("Duplicates")
public class FilesArchive extends ConfigInterface {

    private String name;
    private Boolean archiveFiles;

    private List<JSONObject> JSONPaths;
    private List<Paths> pathsList;

    private String LastMod;

    FilesArchive(String dataPath){

    }

    public void load() {

    }


    public void save() {

    }


    public void setValue(String param, Object value) {

    }

    private static class Paths extends ConfigInterface{

        private File file;
        private File dest;

        private Boolean disabled;

        private String filePath;
        private String destPath;

        private JSONObject JSONPaths;

        public Paths(JSONObject JSONPaths){
            this.JSONPaths = JSONPaths;
            load();
        }

        public void load(){
            filePath = JSONPaths.getString(PVE.PATH.getVar());
            destPath = JSONPaths.getString(PVE.DEST.getVar());

            try{
                disabled = JSONPaths.getBoolean(PVE.DISABLED.getVar());
            }catch (NullPointerException n){
                disabled = false;
            }

            file = new File(filePath);
            dest = new File(destPath);
        }

        public void save(){
            filePath = JSONPaths.getString(PVE.PATH.getVar());
            destPath = JSONPaths.getString(PVE.DEST.getVar());
            try{
                disabled = JSONPaths.getBoolean(PVE.DISABLED.getVar());
            }catch (NullPointerException n){
                disabled = false;
            }

            file = new File(filePath);
            dest = new File(destPath);

            JSONPaths.put(PVE.PATH.getVar(),filePath);
            JSONPaths.put(PVE.DEST.getVar(),destPath);
            JSONPaths.put(PVE.DISABLED.getVar(),disabled);
        }

        public void setValue(String param, Object value) {
            if (!dataVars.contains(param)){
                LOG.println("Invalid parameter ["+ param +"]");
                return;
            }
            JSONPaths.put(param,value);
            save();
        }
    }
}
