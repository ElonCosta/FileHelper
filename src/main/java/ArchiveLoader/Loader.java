package ArchiveLoader;


import ArchiveLoader.Archive.Archive;
import Utils.Utils;
import lombok.Getter;
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
import static Utils.Utils.getString;

public class Loader {

    @Getter
    private List<Archive> archives = new ArrayList<>();
    @Getter
    private List<Archive> newArchives = new ArrayList<>();

    private Boolean paused;

    private ThreadPoolExecutor updateExecutor;

    public Loader(){
        updateExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(6);
    }

    public void load(){
        createFolders();
        checkForFiles();
    }

    private void createFolders(){
        config.getGlobal().getRootFolder().mkdir();
        config.getGlobal().getArchiveFolder().mkdirs();
        config.getGlobal().getVersionFolder().mkdirs();
    }

    public void check(){
        archives.stream().filter(a -> a.getStatus() == Utils.STATUS.READY).forEach(a -> updateExecutor.submit(a::check));
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
}
