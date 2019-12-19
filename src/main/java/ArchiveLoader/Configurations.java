package ArchiveLoader;

import Utils.ConfigInterface;
import Utils.Log.Command;
import org.json.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static Main.Launcher.*;
import static Utils.Constants.*;

public class Configurations implements ConfigInterface {

    private JSONObject config;

    private Global global;
    private DataFiles dataFiles;


    private static File configPath = new File("./configuration.json");

    public Configurations(){
        load();
    }

    private void generateConfigFile(){

        JSONObject config = new JSONObject();

        config.put("Global",global.getAsObject());
        config.put("DataFiles",dataFiles.getAsObject());

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
            global = new Global();
            dataFiles = new DataFiles();

            generateConfigFile();
            System.exit(0);
        }else {
            try{
                JSONTokener tok = new JSONTokener(new FileReader(configPath));
                config = new JSONObject(tok);
                global = new Global(config.getJSONObject(KEY.GLOBAL.getVar()));
                dataFiles = new DataFiles(config.getJSONArray(KEY.DATA_FILES.getVar()));
            }catch (IOException iO){
                iO.printStackTrace();
            }
        }
    }

    @Override
    public void save(){
        global.save();
        dataFiles.save();

        put(config, KEY.GLOBAL, global.getAsObject());
        put(config, KEY.DATA_FILES, dataFiles.getAsObject());

        try{
            FileWriter fw = new FileWriter(configPath,false);
            fw.write(config.toString(4));
            fw.close();
        }catch (IOException e){
            e.printStackTrace();
        }

        if(mainUI != null){
            mainUI.updateInterface();
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
            JSONObject global = new JSONObject();
            global.put(KEY.DISPLAY_TIME.getVar(),true);
            global.put(KEY.ARCHIVE_FILES.getVar(),true);
            global.put(KEY.ROOT_FOLDER.getVar(),"Source");
            global.put(KEY.ARCHIVE_FOLDER.getVar(),"Source\\Archive");
            global.put(KEY.VERSION_FOLDER.getVar(),"Source\\Latest");
            global.put(KEY.ROUTINE_TIME.getVar(),5);

            return global;
        }

        public String getShorthandPath(File file){
            return getShorthandPath(file, true);
        }

        public String getShorthandPath(File file, boolean x){
            if(x){
                File parent = new File(new File(file.getAbsolutePath()).getParent());
                return "...\\" + parent.getName() + "\\" + file.getName();
            }else{
                File parent = new File(new File(file.getAbsolutePath()).getParent());
                return "\\"+parent.getName() + "\\" + file.getName();
            }
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
                LOG.println("Changing \"" + getShorthandPath(this.rootFolder, false) + "\" to \"" + getShorthandPath(rootFolder, false)   + "\"");
                this.rootFolder = rootFolder;
            }
            Configurations.this.save();
        }

        public void setArchiveFolder(File archiveFolder) {
            if (!archiveFolder.getAbsolutePath().equals(this.archiveFolder.getAbsolutePath())){
                LOG.println("Changing \"" + getShorthandPath(this.archiveFolder, false) + "\" to \"" + getShorthandPath(archiveFolder, false)  + "\"");
                this.archiveFolder = archiveFolder;
            }
            Configurations.this.save();
        }

        public void setVersionFolder(File versionFolder) {
            if (!versionFolder.getAbsolutePath().equals(this.versionFolder.getAbsolutePath())){
                LOG.println("Changing \"" + getShorthandPath(this.versionFolder, false) + "\" to \"" + getShorthandPath(versionFolder, false)  + "\"");
                this.versionFolder = versionFolder;
            }
            Configurations.this.save();
        }

        public void setDisplayTime(Boolean displayTime){
            if (this.displayTime && displayTime){
                LOG.println("Time display is already enabled");
            }else if (!this.displayTime && !displayTime){
                LOG.println("Time display is already disabled");
            }else {
                if (displayTime){
                    LOG.println("Enabling time display");
                    this.displayTime = true;
                }else {
                    LOG.println("Disabling time display");
                    this.displayTime = false;
                }
            }
            Configurations.this.save();
        }
        public void setArchiveFiles(Boolean archiveFiles){
            if (this.archiveFiles && archiveFiles){
                LOG.println("File archiving is already enabled");
            }else if (!this.archiveFiles && !archiveFiles){
                LOG.println("File archiving is already disabled");
            }else {
                if (archiveFiles){
                    this.archiveFiles = true;
                    LOG.println("Enabling file archiving");
                }else {
                    this.archiveFiles = false;
                    LOG.println("Disabling file archiving");
                }
            }
            Configurations.this.save();
        }
        public void setRoutineTime(Integer routineTime){
            if (this.routineTime.equals(routineTime)){
                LOG.println("The routine delay already is: " + routineTime);
            }else{
                this.routineTime = routineTime;
                LOG.println("Setting routine delay to: " + routineTime);
            }
            Configurations.this.save();
        }

        public void setHashKey(String hashKey){
            this.hashKey = hashKey;
        }

        /* Methods inherited from ConfigInterface */

        @Override
        public void load() {
            rootFolderName = getString(JSONGlobal, KEY.ROOT_FOLDER);
            rootFolder = new File(rootFolderName);

            versionFolderName = getString(JSONGlobal, KEY.VERSION_FOLDER);
            versionFolder = new File(versionFolderName);

            archiveFolderName = getString(JSONGlobal, KEY.ARCHIVE_FOLDER);
            archiveFolder = new File(archiveFolderName);

            displayTime = getBoolean(JSONGlobal, KEY.DISPLAY_TIME);
            archiveFiles = getBoolean(JSONGlobal, KEY.ARCHIVE_FILES);

            routineTime = getInteger(JSONGlobal, KEY.ROUTINE_TIME);

            hashKey = getString(JSONGlobal, KEY.HASH_KEY);
        }

        @Override
        public void save() {
            JSONGlobal.put(KEY.ARCHIVE_FOLDER.getVar(),archiveFolder.getAbsolutePath());
            JSONGlobal.put(KEY.VERSION_FOLDER.getVar(),versionFolder.getAbsolutePath());
            JSONGlobal.put(KEY.ROOT_FOLDER.getVar(),rootFolder.getAbsolutePath());
            JSONGlobal.put(KEY.ROUTINE_TIME.getVar(),routineTime);
            JSONGlobal.put(KEY.DISPLAY_TIME.getVar(),displayTime);
            JSONGlobal.put(KEY.ARCHIVE_FILES.getVar(),archiveFiles);
            JSONGlobal.put(KEY.HASH_KEY.getVar(),hashKey);
        }

        @Override
        public Object getAsObject() {
            return JSONGlobal;
        }

        /* Commands */

        private void loadCommands(){
            LOG.newCommand(new Command("displayTime", "v") {
                @Override
                public void run() {
                    if(this.argsLoad){
                        Boolean setTo = getArg("v").getAsBoolean();
                        if (setTo == null){
                            LOG.println("Invalid value \""+getArg("v")+"\"");
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
                            LOG.println("Invalid value \""+getArg("v")+"\"");
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
            for (JSONObject jo: dataFilesList){
                if (jo.get(KEY.NAME.getVar()).equals(name)){
                    return;
                }
            }
            JSONObject dataFile = new JSONObject();
            put(dataFile, KEY.NAME, name);
            put(dataFile, KEY.PATH, global.rootFolderName +"\\"+name + "_Data.json");

            dataFilesList.add(dataFile);
        }

        /* Getters */

        List<JSONObject> getDataFilesList(){
            return dataFilesList;
        }

        /* Methods inherited from ConfigInterface */

        @Override
        public void load() {
            for (Object o: JSONDataFiles) {
                dataFilesList.add((JSONObject) o);
            }
        }

        @Override
        public void save() {
            JSONDataFiles = new JSONArray(dataFilesList);
        }

        @Override
        public Object getAsObject() {
            return JSONDataFiles;
        }
    }

}
