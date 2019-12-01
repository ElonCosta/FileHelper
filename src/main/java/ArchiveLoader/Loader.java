package ArchiveLoader;

import Utils.Constants.*;
import Utils.Log.Command;

import org.json.*;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.Timer;

import static ArchiveLoader.Configurations.*;
import static Main.Launcher.*;

import static Utils.Constants.*;

@SuppressWarnings("Duplicates")
public class Loader {

    public static Map<String, FilesArchive> archiveMap = new HashMap<>();
    private Timer routine;

    private Date next;

    private boolean isPaused;

    public Loader(){
        loadCommands();
    }

    public void load(){
        createFolders();
        LOG.println("Initial Check:");
        checkForFiles();
        mainUI.createTabs(archiveMap);
        config.save();
        LOG.println(!archiveMap.isEmpty() ? "Next routine execution at: \"" + (new SimpleDateFormat("HH:mm").format(getNextRoutine(config.getGlobal().getRoutineTime()))) + "\"" : "" );
        routine();
    }
    private void createFolders(){
        if (config.getGlobal().getRootFolder().mkdir())     LOG.println("Creating " + config.getGlobal().getRootFolderName());
        if (config.getGlobal().getArchiveFolder().mkdirs()) LOG.println("Creating " + config.getGlobal().getArchiveFolderName());
        if (config.getGlobal().getVersionFolder().mkdirs()) LOG.println("Creating " + config.getGlobal().getVersionFolderName());
    }

    private void routine(){
        routine = new Timer(getTime(), e -> {
            try{
                for (FilesArchive data: archiveMap.values()){
                    data.checker();
                }
                next = getNextRoutine(config.getGlobal().getRoutineTime());
                if(!isPaused){
                    LOG.println("Next routine execution at: \"" + (new SimpleDateFormat("HH:mm").format(next)) + "\"");
                }
            }catch (IOException | ParseException ex){
                ex.printStackTrace();
            }
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

    private void checkForFiles(){
        try{
            if (config.getDataFiles().getDataFilesList().size() > 0){
                for (JSONObject d: config.getDataFiles().getDataFilesList()){
                    if (archiveMap.get(getString(d,KEY.NAME)) == null){
                        FilesArchive data = new FilesArchive(getString(d,KEY.PATH));
                        archiveMap.put(getString(d, KEY.NAME),data);
                    }
                }
            }
        }catch (FileNotFoundException f){
            f.printStackTrace();
        }
    }

    private Integer getTime(){
        return config.getGlobal().getRoutineTime() * 60000;
    }

    private void newArchive(){

    }

     /*
     * Commands
     */
    private void close() throws IOException, ParseException{
            LOG.println("Checking files before closing");
            for (FilesArchive data: archiveMap.values()){
                data.checker();
            }
            LOG.println("Closing FileHelper");
            System.exit(0);
    }

    private void forceCheck() throws IOException, ParseException {
        for (FilesArchive data: archiveMap.values()){
            data.checker();
        }
        next = getNextRoutine(config.getGlobal().getRoutineTime());
        if(!isPaused){
            LOG.println("Next routine execution at: \"" + (new SimpleDateFormat("HH:mm").format(next)) + "\"");
        }
    }

    private void setPaused(boolean paused){
        if (isPaused && paused){
            LOG.println("Routine is already paused");
        }else if (!isPaused && !paused){
            LOG.println("Routine is already running");
        }else {
            if (paused){
                isPaused = true;
                routine.stop();
                LOG.println("Pausing routine");
            }else {
                isPaused = false;
                routine.start();
                LOG.println("Resuming routine");
            }
        }

    }

    private void loadCommands(){
        LOG.newCommand(
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
                        try{
                            forceCheck();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Command(true,"pause","v") {
                    @Override
                    public void run() {
                        if (this.argsLoad){
                            Boolean value = getArg("v").getAsBoolean();
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
                        FilesArchive data = archiveMap.get(file);
                        if(data != null){
                            data.disablePath(pos,val);
                        }else {
                            LOG.printErr(6, file);
                        }
                    }
                });
    }
}
