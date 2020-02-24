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
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static Main.Launcher.config;
import static Main.Launcher.log;
import static Utils.Utils.getString;

public class Loader {

    private List<Archive> archives = new ArrayList<>();
    private List<Archive> newArchives = new ArrayList<>();

    private Boolean paused;

    private ThreadPoolExecutor executor;

    public Loader(){
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(6);
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

    public void check(){
        archives.stream().filter(a -> a.getStatus() == Utils.STATUS.READY).forEach(a -> executor.submit(a::check));
    }

    public void checkForFiles(){
        try{
            if (config.getDataFiles().getDataFilesList().size() > 0){
                for (JSONObject d: config.getDataFiles().getDataFilesList()){
                        FileReader fr = new FileReader(new File(getString(d, Utils.KEY.PATH)));
                        JSONTokener t = new JSONTokener(fr);
                        Archive data = new Archive(new JSONObject(t));
                        fr.close();
                    if (archives.stream().map(Archive::getId).collect(Collectors.toList()).contains(data.getId())) continue;
                        archives.add(data);
                }
            }

        } catch (IOException f){
            f.printStackTrace();
        }
    }
    public List<Archive> getArchives() {
        return archives;
    }

    public List<Archive> getNewArchives() {
        return newArchives;
    }
}
