package ArchiveLoader;

import Utils.ConfigInterface;
//import com.Hasher.Hasher;
import lombok.Getter;
import lombok.Setter;
import org.json.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static Main.Launcher.*;
import static Utils.Utils.*;
import static Utils.Utils.KEY.*;

public class Configurations implements ConfigInterface {

    private JSONObject config;

    @Getter
    private Float ConfigVersion;
    @Getter
    private Global global;
    @Getter
    private DataFiles dataFiles;


    private static File configPath = new File("./configuration.json");

    public Configurations(){
        load();
    }

    private void generateConfigFile(){

        JSONObject config = new JSONObject();

        put(config, CFG_VERSION, ConfigVersion);
        put(config, GLOBAL, global.getAsObject());
        put(config, DATA_FILES, dataFiles.getAsObject());

        try{
            FileWriter fw = new FileWriter(configPath);
            fw.write(config.toString(4));
            fw.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void loadHasher(){
//        if (global.getHashKey().trim().equals("")){
//            hasher = new Hasher();
//            global.setHashKey(hasher.getHashKey());
//        }else {
//            hasher = new Hasher(global.getHashKey());
//        }
        save();
    }

    /* Methods inherited from ConfigInterface */

    @Override
    public void load(){
        if (!configPath.exists()){
            ConfigVersion = Float.parseFloat(CFG_VER.getVar());
            global = new Global();
            dataFiles = new DataFiles();

            generateConfigFile();
            System.exit(0);
        }else {
            try{
                JSONTokener tok = new JSONTokener(new FileReader(configPath));
                config = new JSONObject(tok);
                createFieldsIfEmpty();

                ConfigVersion = getFloat(config, CFG_VERSION);
                global = new Global(getJSONObject(config, GLOBAL));
                dataFiles = new DataFiles(getJSONArray(config, DATA_FILES));
            }catch (IOException iO){
                iO.printStackTrace();
            }
        }
    }

    @Override
    public void save(){
        global.save();
        dataFiles.save();

        put(config, GLOBAL, global.getAsObject());
        put(config, DATA_FILES, dataFiles.getAsObject());

        try{
            FileWriter fw = new FileWriter(configPath,false);
            fw.write(config.toString(4));
            fw.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void createFieldsIfEmpty() {
        for (KEY k: configKeys){
            Object value;
            switch (k){
                case CFG_VERSION:
                    value = Float.parseFloat(CFG_VER.getVar());
                    break;
                case GLOBAL:
                    value = (new Global()).getAsObject();
                    break;
                case DATA_FILES:
                    value = (new DataFiles()).getAsObject();
                    break;
                default:
                    value = null;
                    break;
            };
            if (isNull(config, k)) put(config, k, value);
        }
    }

    @Override
    public Object getAsObject() {
        return config;
    }

    public class Global implements ConfigInterface{

        @Getter
        private JSONObject JSONGlobal;

        @Getter @Setter
        private String versionFolderName;
        @Getter @Setter
        private String rootFolderName;
        @Getter @Setter
        private String archiveFolderName;

        @Getter @Setter
        private File versionFolder;
        @Getter @Setter
        private File rootFolder;
        @Getter @Setter
        private File archiveFolder;

        @Getter @Setter
        private Integer routineTime;

        @Getter @Setter
        private String hashKey;

        private Global(JSONObject JSONGlobal){
            this.JSONGlobal = JSONGlobal;
            load();
        }
        private Global(){
            JSONGlobal = getEmptyGlobal();
        }

        JSONObject getEmptyGlobal(){
            JSONObject JSONGlobal = new JSONObject();
            put(JSONGlobal, ROOT_FOLDER, "Source");
            put(JSONGlobal, ARCHIVE_FOLDER, "Source\\Archive");
            put(JSONGlobal, VERSION_FOLDER, "Source\\Latest");
            put(JSONGlobal, ROUTINE_TIME, 5);
            put(JSONGlobal, HASH_KEY, "");

            return JSONGlobal;
        }

        /* Methods inherited from ConfigInterface */

        @Override
        public void load() {
            createFieldsIfEmpty();

            rootFolderName = getString(JSONGlobal, ROOT_FOLDER);
            rootFolder = new File(rootFolderName);

            versionFolderName = getString(JSONGlobal, VERSION_FOLDER);
            versionFolder = new File(versionFolderName);

            archiveFolderName = getString(JSONGlobal, ARCHIVE_FOLDER);
            archiveFolder = new File(archiveFolderName);

            routineTime = getInteger(JSONGlobal, ROUTINE_TIME);

            hashKey = getString(JSONGlobal, HASH_KEY);
        }

        @Override
        public void save() {
            put(JSONGlobal,ARCHIVE_FOLDER,archiveFolder.getAbsolutePath());
            put(JSONGlobal,VERSION_FOLDER,versionFolder.getAbsolutePath());
            put(JSONGlobal,ROOT_FOLDER,rootFolder.getAbsolutePath());
            put(JSONGlobal,ROUTINE_TIME,routineTime);
            put(JSONGlobal,HASH_KEY,hashKey);
        }

        @Override
        public Object getAsObject() {
            return JSONGlobal;
        }

        @Override
        public void createFieldsIfEmpty(){
            for (KEY k: globalKeys){
                if (!isNull(JSONGlobal, k)) continue;
                Object value;
                switch(k) {
                    case ROOT_FOLDER:
                        value = "Source";
                        break;
                    case ARCHIVE_FOLDER:
                        value = global.getRootFolderName()+"\\Archive";
                        break;
                    case VERSION_FOLDER:
                        value = global.getRootFolderName()+"\\Latest";
                        break;
                    case ROUTINE_TIME:
                        value = 5;
                        break;
                    case HASH_KEY:
                        value = "";
                        break;
                    default:
                        value = null;
                        break;
                };
                put(JSONGlobal, k, value);
            }
        }
    }
    public class DataFiles implements ConfigInterface{

        @Getter
        private JSONArray JSONDataFiles;

        @Getter
        private List<JSONObject> dataFilesList = new ArrayList<>();

        private DataFiles(JSONArray JSONDataFiles){
            this.JSONDataFiles = JSONDataFiles;
            load();
        }
        private DataFiles(){
            JSONDataFiles = getEmptyDataFiles();
        }

        JSONArray getEmptyDataFiles(){
            return new JSONArray();
        }

        public void newDataFile(String name){
            if (dataFilesList.stream().anyMatch(jo -> getString(jo, NAME).equals(name))) return;
            JSONObject dataFile = new JSONObject();
            put(dataFile, NAME, name);
            put(dataFile, PATH, global.rootFolderName +"\\"+name + "_Data.json");

            dataFilesList.add(dataFile);
        }

        public void deleteDataFile(String name){
            File dataFile = null;
            int pos = 0;
            for (JSONObject jo: dataFilesList){
                if (getString(jo,NAME).equals(name)){
                    dataFile = new File(getString(jo,PATH));
                    pos = dataFilesList.indexOf(jo);
                }
            }
            if (dataFile != null){
                dataFile.delete();
                dataFilesList.remove(pos);
            }
            Configurations.this.save();
        }

        /* Methods inherited from ConfigInterface */

        @Override
        public void load() {
            JSONDataFiles.forEach(o -> dataFilesList.add((JSONObject) o));
        }

        @Override
        public void save() {
            JSONDataFiles = new JSONArray(dataFilesList);
        }

        @Override
        public void createFieldsIfEmpty() {

        }

        @Override
        public Object getAsObject() {
            return JSONDataFiles;
        }
    }

}
