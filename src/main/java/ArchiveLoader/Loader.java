package ArchiveLoader;

import Main.Launcher;
import Utils.Log.Command;
import org.json.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import ArchiveLoader.GlobalCfg.DataPath;

import javax.swing.Timer;

import static ArchiveLoader.GlobalCfg.*;
import static Main.Launcher.LOG;

@SuppressWarnings("Duplicates")
public class Loader {

    public static GlobalCfg glbCfg = new GlobalCfg();
    private List<ArchiveData> archives = new ArrayList<>();
    private Timer routine;

    private Date next;

    private boolean isPaused;

    public Loader() throws IOException{
        if (!configPath.exists()){
            glbCfg.generateConfigFile();
            System.exit(0);
        }

        LOG.newCommand(new Command("close") {
            @Override
            public void run() {
                try{
                    close();
                }catch (IOException | ParseException e){
                    e.printStackTrace();
                }
            }
        });
        LOG.newCommand(new Command("forceCheck") {
            @Override
            public void run() {
                try{
                    forceCheck();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        LOG.newCommand(new Command("pause","-v[B]") {
            @Override
            public void run() {
                Boolean value = getArg("-v").getAsBoolean();
                setPaused(value);
            }
        });
        LOG.newCommand(new Command("disablePath","-f[S]","-p[I]","-v[B]") {
            @Override
            public void run() {
                String file = getArg("-f").getAsString();
                Integer pos = getArg("-p").getAsInteger();
                Boolean val = getArg("-v").getAsBoolean();
                for (ArchiveData arch:
                     archives) {
                    if (file.equals(arch.getName())){
                        arch.getPaths().get(pos-1).disable(val);
                        arch.saveData();
                        if (val){
                            LOG.println("path disabled");
                        }else {
                            LOG.println("path enabled");
                        }
                    }
                }

            }
        });
    }

    public void load() throws IOException, ParseException{
        createFolders();
        LOG.println("Initial Check:");
        checkForFiles();
        Files.write(Paths.get(configPath.toURI()),glbCfg.getConfig().toString(4).getBytes());
        LOG.println("Next routine execution at: \"" + (new SimpleDateFormat("HH:mm").format(getNextRoutine(glbCfg.getRoutineTime()))) + "\"");
        routine();
    }
    private void createFolders(){
        if (FoldersMap.get("root").mkdir())     LOG.println("Creating " + glbCfg.getRootFolderName());
        if (FoldersMap.get("archive").mkdirs()) LOG.println("Creating " + glbCfg.getArchiveFolderName());
        if (FoldersMap.get("version").mkdirs()) LOG.println("Creating " + glbCfg.getVersionFolderName());
    }

    private void routine() throws IOException {
        routine = new Timer(getTime(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    for (ArchiveData data: archives){
                        data.checker();
                    }
                    next = getNextRoutine(glbCfg.getRoutineTime());
                    LOG.println("Next routine execution at: \"" + (new SimpleDateFormat("HH:mm").format(next)) + "\"");
                }catch (IOException | ParseException ex){
                    ex.printStackTrace();
                }
            }
        });
        routine.setRepeats(true);
        routine.start();
        while (true){
            LOG.readCommand();
        }
    }

    private Date getNextRoutine(int Time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, Time);
        return calendar.getTime();
    }

    private void checkForFiles() throws IOException, ParseException{
        if (glbCfg.getDataPaths().size() > 0){
            for (DataPath d: glbCfg.getDataPaths()){
                ArchiveData data = new ArchiveData(d);
                archives.add(data);
            }
        }
        outer:
        for (int i = 0; i < glbCfg.getFilesArray().length(); i++){
            JSONObject File = glbCfg.getFilesArray().getJSONObject(i).getJSONObject("File");
            for (ArchiveData d: archives){
                if (File.getString("name").equals(d.getName())){
                    continue outer;
                }
            }
            ArchiveData data = new ArchiveData(File);
            archives.add(data);
        }

        for (ArchiveData a:archives){
            System.out.println(a.getName());
        }
    }

    private Integer getTime(){
        return glbCfg.getRoutineTime() * 60000;
    }

     /*
     * Commands
     */
    private void close() throws IOException, ParseException{
            LOG.println("Checking files before closing");
            for (ArchiveData data: archives){
                data.checker();
            }
            LOG.println("Closing FileHelper");
            System.exit(0);
    }

    private void forceCheck() throws IOException, ParseException {
        for (ArchiveData data : archives) {
            data.checker();
        }
        next = getNextRoutine(glbCfg.getRoutineTime());
        LOG.println("Next routine execution at: \"" + (new SimpleDateFormat("HH:mm").format(next)) + "\"");
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

    private void updateFile(String fileName){

    }
}
