package br.com.claw.archiveLoader;

import br.com.Hasher.*;
import lombok.Getter;
import lombok.Setter;
import org.json.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import br.com.claw.enums.KEY;

import static br.com.claw.enums.KEY.*;
import static br.com.claw.utils.JSONUtils.*;
import static br.com.claw.Launcher.*;

public class Configurations implements ConfigInterface {

    private JSONObject config;

    @Getter
    private Float ConfigVersion;
    @Getter
    private Global global;
    @Getter
    private DataFiles dataFiles;


    private static final File configPath = new File("./configuration.json");

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
        if (global.getHashKey().trim().equals("")){
            hasher = new Hasher();
            global.setHashKey(hasher.getHashKey());
        }else {
            hasher = new Hasher(global.getHashKey());
        }
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
            var value = switch (k){
                case CFG_VERSION -> Float.parseFloat(CFG_VER.getVar());
                case GLOBAL -> (new Global()).getAsObject();
                case DATA_FILES -> (new DataFiles()).getAsObject();
                default -> null;
            };
            if (isNull(config, k)) put(config, k, value);
        }
    }

    @Override
    public Object getAsObject() {
        return config;
    }

    public static class Global implements ConfigInterface{

        @Getter
        private final JSONObject JSONGlobal;

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
                var value = switch(k) {
                    case ROOT_FOLDER -> "Source";
                    case ARCHIVE_FOLDER -> "Source\\Archive";
                    case VERSION_FOLDER -> "Source\\Latest";
                    case ROUTINE_TIME -> 5;
                    case HASH_KEY -> "";
                    default -> null;
                };
                if(isNull(JSONGlobal, k)) put(JSONGlobal, k, value);
            }
        }
    }
    public class DataFiles implements ConfigInterface{

        @Getter
        private JSONArray JSONDataFiles;

        @Getter
        private final List<JSONObject> dataFilesList = new ArrayList<>();

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
            put(dataFile, PATH, global.rootFolderName +"\\"+(name.replaceAll("\\s","_"))+ "_Data.json");

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
            if (dataFile != null && dataFile.exists()){
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
