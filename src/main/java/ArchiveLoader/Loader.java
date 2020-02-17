package ArchiveLoader;

import Log.PrintBuffer;
import Utils.Utils.*;
import Log.Command;

import org.json.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.Timer;

import static Main.Launcher.*;

import static Utils.Utils.*;


public class Loader {

    private Map<String, FilesArchive> archiveMap = new HashMap<>();
    private Timer routine;

    private Date next;

    private boolean isPaused;

    public Map<String, PrintBuffer> printBuffers = new HashMap<>();

    public Loader(){
        loadCommands();
    }

    public void load(){
        createFolders();
        checkForFiles();
        log.println("Initial Check:");
//        check(false);
        config.save();
        log.spitCommands();
        routine();
    }
    private void createFolders(){
        if (config.getGlobal().getRootFolder().mkdir())     log.println("Creating " + config.getGlobal().getRootFolderName());
        if (config.getGlobal().getArchiveFolder().mkdirs()) log.println("Creating " + config.getGlobal().getArchiveFolderName());
        if (config.getGlobal().getVersionFolder().mkdirs()) log.println("Creating " + config.getGlobal().getVersionFolderName());
    }

    private void check(boolean f){
        if (routine != null && !f){
            routine.stop();
            routine.setInitialDelay(getTime());
            routine.restart();
        }
        archiveMap.values().forEach(FilesArchive::check);
        next = getNextRoutine(config.getGlobal().getRoutineTime());
        if(!isPaused || !f) log.println("Next routine execution at: \"" + (new SimpleDateFormat("HH:mm").format(next)) + "\"");
    }

    private void check(boolean f, String s){
        if (s == null){ check(f); return;}
        archiveMap.get(s).check();
    }

    private void routine(){
        routine = new Timer(getTime(), e -> {
                archiveMap.values().forEach(FilesArchive::check);
                next = getNextRoutine(config.getGlobal().getRoutineTime());
                if(!isPaused) log.println("Next routine execution at: \"" + (new SimpleDateFormat("HH:mm").format(next)) + "\"");
        });
        routine.setRepeats(true);
        routine.start();
    }

    private Date getNextRoutine(int Time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, Time);
        return calendar.getTime();
    }

    public void checkForFiles(){
        try{
            if (config.getDataFiles().getDataFilesList().size() > 0){
                for (JSONObject d: config.getDataFiles().getDataFilesList()){
                    if (archiveMap.get(getString(d,KEY.NAME)) == null){
                        FileReader fr = new FileReader(new File(getString(d, KEY.PATH)));
                        JSONTokener t = new JSONTokener(fr);
                        FilesArchive data = new FilesArchive(new JSONObject(t));
                        fr.close();
                        archiveMap.put(getString(d, KEY.NAME),data);
                    }
                }
            }

        } catch (IOException f){
            f.printStackTrace();
        }
    }

    private Integer getTime(){
        return config.getGlobal().getRoutineTime() * 60000;
    }

    public void removeFile(String fileName){
        archiveMap.remove(fileName);
        config.getDataFiles().deleteDataFile(fileName);
    }

    public Map<String, FilesArchive> getArchiveMap() {
        return archiveMap;
    }

    public void setArchiveMap(Map<String, FilesArchive> archiveMap) {
        this.archiveMap = archiveMap;
    }

    /*
     * Commands
     */
    private void close(){
//            log.println("Checking files before closing");
//            archiveMap.values().forEach(FilesArchive::check);
            log.println("Closing FileHelper");
            System.exit(0);
    }

    private void setPaused(boolean paused){
        if (isPaused && paused){
            log.println("Routine is already paused");
        }else if (!isPaused && !paused){
            log.println("Routine is already running");
        }else {
            if (paused){
                isPaused = true;
                routine.stop();
                log.println("Pausing routine");
            }else {
                isPaused = false;
                routine.start();
                log.println("Resuming routine");
            }
        }

    }

    private void loadCommands(){
        log.newCommand(
                new Command("close") {
                    @Override
                    public void run() {
                        close();
                    }
                },
                new Command("forceCheck", "d", "f") {
                    @Override
                    public void run() {
                        Boolean d = getArg("d").getAsBoolean();
                        String f = getArg("f").getAsString();
                        check((d != null ? d : false), (f));
                    }
                },
                new Command("pause","v") {
                    @Override
                    public void run() {
                        if (this.argsLoad){
                            Boolean value = getArg("v").getAsBoolean();
                            if (value == null){
                                log.println("Invalid value \""+getArg("v")+"\"");
                                return;
                            }
                            setPaused(value);
                        }else {
                            setPaused(!isPaused);
                        }
                    }
                });
    }
}
