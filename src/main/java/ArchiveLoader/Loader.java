package ArchiveLoader;

import Utils.Utils.*;
import Log.Command;

import org.json.*;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.Timer;

import static Main.Launcher.*;

import static Utils.Utils.*;

@SuppressWarnings("Duplicates")
public class Loader {

    private Map<String, FilesArchive> archiveMap = new HashMap<>();
    private Timer routine;

    private Date next;

    private boolean isPaused;

    public Loader(){
        loadCommands();
    }

    public void load(){
        createFolders();
        checkForFiles();
        log.println("Initial Check:");
        initialCheck();
        config.save();
        log.spitCommands();
        routine();
    }
    private void createFolders(){
        if (config.getGlobal().getRootFolder().mkdir())     log.println("Creating " + config.getGlobal().getRootFolderName());
        if (config.getGlobal().getArchiveFolder().mkdirs()) log.println("Creating " + config.getGlobal().getArchiveFolderName());
        if (config.getGlobal().getVersionFolder().mkdirs()) log.println("Creating " + config.getGlobal().getVersionFolderName());
    }

    private void initialCheck(){
        archiveMap.values().forEach(FilesArchive::checker);
        next = getNextRoutine(config.getGlobal().getRoutineTime());
        if(!isPaused) log.println("Next routine execution at: \"" + (new SimpleDateFormat("HH:mm").format(next)) + "\"");
    }

    private void routine(){
        routine = new Timer(getTime(), e -> {
                archiveMap.values().forEach(FilesArchive::checker);
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
    private void close() throws IOException, ParseException{
            log.println("Checking files before closing");
            for (FilesArchive data: archiveMap.values()){
                data.checker();
                data.save();
            }
            log.println("Closing FileHelper");
            System.exit(0);
    }

    private void forceCheck(){
        archiveMap.values().forEach(FilesArchive::checker);
        next = getNextRoutine(config.getGlobal().getRoutineTime());
        if(!isPaused) log.println("Next routine execution at: \"" + (new SimpleDateFormat("HH:mm").format(next)) + "\"");
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
                        try{
                            close();
                        }catch (IOException | ParseException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Command("forceCheck") {
                    @Override
                    public void run() {
                        forceCheck();
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
                },
                new Command(false,"disablePath","f","p","v") {
                    @Override
                    public void run() {
                        String file = getArg("f").getAsString();
                        Integer pos = getArg("p").getAsInteger() - 1;
                        Boolean val = getArg("v").getAsBoolean();
                        if (val == null){
                            log.println("Invalid value \""+getArg("v")+"\"");
                            return;
                        }
                        FilesArchive data = archiveMap.get(file);
                        if(data != null){
                            data.disablePath(pos,val);
                        }else {
                            log.println("Unknown archive \""+file+"\"");
                        }
                    }
                });
    }
}
