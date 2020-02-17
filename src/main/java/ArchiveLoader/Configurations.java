package ArchiveLoader;

import Utils.ConfigInterface;
import Log.Command;
import com.Hasher.Hasher;
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

    private Float ConfigVersion;
    private Global global;
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

    public void loadCommands(){
        global.loadCommands();
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

    /* Getters */

    public Global getGlobal() {
        return global;
    }

    public DataFiles getDataFiles() {
        return dataFiles;
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

        private JSONObject JSONGlobal;

        private String versionFolderName;
        private String rootFolderName;
        private String archiveFolderName;

        private File versionFolder;
        private File rootFolder;
        private File archiveFolder;

        private Boolean displayTime;
        private Boolean archiveFiles;

        private Integer routineTime;

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
            put(JSONGlobal, DISPLAY_TIME, true);
            put(JSONGlobal, ARCHIVE_FILES, true);
            put(JSONGlobal, ROOT_FOLDER, "Source");
            put(JSONGlobal, ARCHIVE_FOLDER, "Source\\Archive");
            put(JSONGlobal, VERSION_FOLDER, "Source\\Latest");
            put(JSONGlobal, ROUTINE_TIME, 5);
            put(JSONGlobal, HASH_KEY, "");

            return JSONGlobal;
        }

        /* Getters || Setters */

        String getVersionFolderName() {
            return versionFolderName;
        }

        String getRootFolderName() {
            return rootFolderName;
        }

        String getArchiveFolderName() {
            return archiveFolderName;
        }

        public File getVersionFolder() {
            return versionFolder;
        }

        public File getRootFolder() {
            return rootFolder;
        }

        public File getArchiveFolder() {
            return archiveFolder;
        }

        public Boolean getDisplayTime() {
            return displayTime;
        }

        public Boolean getArchiveFiles() {
            return archiveFiles;
        }

        public Integer getRoutineTime() {
            return routineTime;
        }

        public String getHashKey(){
            return hashKey;
        }

        public void setRootFolder(File rootFolder){
            if (!rootFolder.getAbsolutePath().equals(this.rootFolder.getAbsolutePath())){
                log.println("Changing \"" + getShorthandPath(this.rootFolder, false) + "\" to \"" + getShorthandPath(rootFolder, false)   + "\"");
                this.rootFolder = rootFolder;
            }
            Configurations.this.save();
        }

        public void setArchiveFolder(File archiveFolder) {
            if (!archiveFolder.getAbsolutePath().equals(this.archiveFolder.getAbsolutePath())){
                log.println("Changing \"" + getShorthandPath(this.archiveFolder, false) + "\" to \"" + getShorthandPath(archiveFolder, false)  + "\"");
                this.archiveFolder = archiveFolder;
            }
            Configurations.this.save();
        }

        public void setVersionFolder(File versionFolder) {
            if (!versionFolder.getAbsolutePath().equals(this.versionFolder.getAbsolutePath())){
                log.println("Changing \"" + getShorthandPath(this.versionFolder, false) + "\" to \"" + getShorthandPath(versionFolder, false)  + "\"");
                this.versionFolder = versionFolder;
            }
            Configurations.this.save();
        }

        public void setDisplayTime(Boolean displayTime){
            if (this.displayTime && displayTime){
                log.println("Time display is already enabled");
            }else if (!this.displayTime && !displayTime){
                log.println("Time display is already disabled");
            }else {
                if (displayTime){
                    log.println("Enabling time display");
                    this.displayTime = true;
                }else {
                    log.println("Disabling time display");
                    this.displayTime = false;
                }
            }
            Configurations.this.save();
        }
        public void setArchiveFiles(Boolean archiveFiles){
            if (this.archiveFiles && archiveFiles){
                log.println("File archiving is already enabled");
            }else if (!this.archiveFiles && !archiveFiles){
                log.println("File archiving is already disabled");
            }else {
                if (archiveFiles){
                    this.archiveFiles = true;
                    log.println("Enabling file archiving");
                }else {
                    this.archiveFiles = false;
                    log.println("Disabling file archiving");
                }
            }
            Configurations.this.save();
        }
        public void setRoutineTime(Integer routineTime){
            if (this.routineTime.equals(routineTime)){
                log.println("The routine delay already is: " + routineTime);
            }else{
                this.routineTime = routineTime;
            }
            Configurations.this.save();
        }

        public void setHashKey(String hashKey){
            this.hashKey = hashKey;
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

            displayTime = getBoolean(JSONGlobal, DISPLAY_TIME);
            archiveFiles = getBoolean(JSONGlobal, ARCHIVE_FILES);

            routineTime = getInteger(JSONGlobal, ROUTINE_TIME);

            hashKey = getString(JSONGlobal, HASH_KEY);
        }

        @Override
        public void save() {
            put(JSONGlobal,ARCHIVE_FOLDER,archiveFolder.getAbsolutePath());
            put(JSONGlobal,VERSION_FOLDER,versionFolder.getAbsolutePath());
            put(JSONGlobal,ROOT_FOLDER,rootFolder.getAbsolutePath());
            put(JSONGlobal,ROUTINE_TIME,routineTime);
            put(JSONGlobal,DISPLAY_TIME,displayTime);
            put(JSONGlobal,ARCHIVE_FILES,archiveFiles);
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
                    case DISPLAY_TIME:
                    case ARCHIVE_FILES:
                        value = true;
                        break;
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

        /* Commands */

        private void loadCommands(){
            log.newCommand(new Command("displayTime", "v") {
                @Override
                public void run() {
                    if(this.argsLoad){
                        Boolean setTo = getArg("v").getAsBoolean();
                        if (setTo == null){
                            log.println("Invalid value \""+getArg("v")+"\"");
                            return;
                        }
                        setDisplayTime(setTo);
                    }else {
                        setDisplayTime(!displayTime);
                    }
                }}, new Command("archiveFiles", "v") {
                @Override
                public void run() {
                    if(this.argsLoad){
                        Boolean setTo = getArg("v").getAsBoolean();
                        if (setTo == null){
                            log.println("Invalid value \""+getArg("v")+"\"");
                            return;
                        }
                        setArchiveFiles(setTo);
                    }else {
                        setArchiveFiles(!archiveFiles);
                    }
                }}, new Command(false,"setRoutineTime","v") {
                @Override
                public void run() {
                    Integer setTo = getArg("v").getAsInteger();
                    setRoutineTime(setTo);
                    log.println("Setting routine delay to: " + routineTime);
                }}
            );
        }
    }
    public class DataFiles implements ConfigInterface{

        private JSONArray JSONDataFiles;

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

        /* Getters */

        List<JSONObject> getDataFilesList(){
            return dataFilesList;
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
