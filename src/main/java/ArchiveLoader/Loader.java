package ArchiveLoader;


import ArchiveLoader.Archive.Archive;
import Utils.Utils;
import javafx.fxml.FXML;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static Main.Launcher.config;
import static Main.Launcher.log;
import static Utils.Utils.getString;

public class Loader {

    private Map<String, Archive> archiveMap = new HashMap<>();
    private Map<String, Archive> newArchiveMap = new HashMap<>();
    private Map<String, Archive> editingArchiveMap = new HashMap<>();

    private Boolean paused;

    public Loader(){

    }

    public void load(){
        createFolders();
        checkForFiles();
    }

    private void createFolders(){
        if (config.getGlobal().getRootFolder().mkdir())     log.println("Creating " + config.getGlobal().getRootFolderName());
        if (config.getGlobal().getArchiveFolder().mkdirs()) log.println("Creating " + config.getGlobal().getArchiveFolderName());
        if (config.getGlobal().getVersionFolder().mkdirs()) log.println("Creating " + config.getGlobal().getVersionFolderName());
    }

    public void checkForFiles(){
        try{
            if (config.getDataFiles().getDataFilesList().size() > 0){
                for (JSONObject d: config.getDataFiles().getDataFilesList()){
                    if (archiveMap.get(getString(d, Utils.KEY.NAME)) == null){
                        FileReader fr = new FileReader(new File(getString(d, Utils.KEY.PATH)));
                        JSONTokener t = new JSONTokener(fr);
                        Archive data = new Archive(new JSONObject(t));
                        fr.close();
                        archiveMap.put(getString(d, Utils.KEY.NAME),data);
                    }
                }
            }

        } catch (IOException f){
            f.printStackTrace();
        }
    }

    public Map<String, Archive> getArchiveMap() {
        return archiveMap;
    }


}
