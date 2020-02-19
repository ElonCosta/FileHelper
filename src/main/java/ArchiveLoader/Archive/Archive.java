package ArchiveLoader.Archive;

import ArchiveLoader.FilesArchive;
import Utils.ConfigInterface;
import Utils.Utils;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import Utils.Utils.STATUS;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static Main.Launcher.config;
import static Main.Launcher.log;
import static Utils.Utils.getShorthandPath;

public class Archive implements ConfigInterface {

    private JSONObject JSONData;

    private STATUS status;

    private File dataPath;

    private String name;
    private Boolean archiveFiles;

    private volatile Thread thread;

    private JSONArray JSONPaths;
    private List<JSONObject> JSONPathsList;
    private List<Paths> pathsList;

    private boolean checkDisabled = false;

    private String lastMod;

    private SimpleDateFormat lastModSDF = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    private SimpleDateFormat archSDF = new SimpleDateFormat("/yyyy/MM/dd/HH_mm/");

    public Archive(JSONObject JSONData){
        this.JSONData = JSONData;
        load();
        File latestFolder = new File(config.getGlobal().getVersionFolderName() + "/" + name);
        if (latestFolder.mkdir()){
            log.println("Creating root folder for file(s): " + name);
        }
    }

    public Archive() {
        this.JSONData = new JSONObject();
        this.archiveFiles = true;
        this.lastMod = lastModSDF.format(new Date());
        this.status = STATUS.NEW;
        this.JSONPathsList = new ArrayList<>();
        this.pathsList = new ArrayList<>();
    }

    private List<JSONObject> getPathsAsJSON(){
        List<JSONObject> tmp = new ArrayList<>();
        for (Paths p: pathsList) {
            p.save();
            tmp.add((JSONObject) p.getAsObject());
        }
        return tmp;
    }

    private void archiveFile(File destFile) throws ParseException, IOException {
        if (!destFile.exists()) return;
        if (destFile.isDirectory()) {
            File arch = new File(config.getGlobal().getArchiveFolderName() + (archSDF.format(lastModSDF.parse(lastMod))) + "/" + name + "/" + destFile.getName());
            if (arch.mkdirs()) {
                log.println("\tArchiving \"" + getShorthandPath(destFile) + "\\" + destFile.getName() + "\" on " + arch.getParentFile().getParent());
                FileUtils.copyDirectory(destFile, arch, true);
            }
        }else{
            File arch = new File(config.getGlobal().getArchiveFolderName() + (archSDF.format(lastModSDF.parse(lastMod))) + "/" + name + "/" + destFile.getName());
            if (arch.mkdirs()) {
                log.println("\tArchiving \"" + getShorthandPath(destFile) + "\" on " + arch.getParentFile().getParent());
                FileUtils.copyFileToDirectory(destFile, arch, true);
            }
        }
    }

    void createFile(File file, File destFile) throws IOException{
        if (deleteDest(destFile) || !destFile.exists()){
            if (file.isDirectory()){
                if (destFile.mkdir()){
                    log.println("\tArchiving latest version of \"" + getShorthandPath(file) + "\" on: \"" + destFile.toString() + "\"");
                    FileUtils.copyDirectory(file,destFile,true);
                }
            }else{
                destFile = new File(destFile.getParent());
                log.println("\tArchiving latest version of \"" + getShorthandPath(file) + "\" on: \"" + destFile.toString() + "\"");
                FileUtils.copyFileToDirectory(file, destFile, true);
            }
        }
        lastMod = lastModSDF.format(new Date());
    }

    private Boolean allPathsDisabled(){
        return pathsList.stream().filter(Paths::isDisabled).count() == pathsList.size();
    }

    private Boolean allPathsEnabled(){
        return pathsList.stream().filter(p -> !p.isDisabled()).count() == pathsList.size();
    }

    public Paths createNewPath(){
        Paths path = new Paths();
        path.setParent(this);
        pathsList.add(path);
        return path;
    }
    /*
     * Getters && Setters
     */

    public String getName() {
        return name;
    }

    public Boolean getArchiveFiles() {
        return archiveFiles;
    }

    public String getLastMod() {
        return lastMod;
    }

    public List<Paths> getPathsList(){
        return pathsList;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArchiveFiles(Boolean archiveFiles) {
        this.archiveFiles = archiveFiles;
    }

    public void setPathsList(List<Paths> pathsList) {
        this.pathsList = pathsList;
    }

    public void setLastMod(String lastMod) {
        this.lastMod = lastMod;
    }

    public boolean isCheckDisabled() {
        return checkDisabled;
    }

    public void setCheckDisabled(boolean checkDisabled) {
        this.checkDisabled = checkDisabled;
    }

    /*
     * Methods inherited from ConfigInterface
     */

    public void load() {
        JSONPaths = JSONData.getJSONArray(Utils.KEY.PATHS.getVar());
        name = JSONData.getString(Utils.KEY.NAME.getVar());
        archiveFiles = JSONData.getBoolean(Utils.KEY.ARCHIVE_FILE.getVar());

        JSONPathsList = new ArrayList<>();
        pathsList = new ArrayList<>();
        for (Object o: JSONPaths){
            JSONPathsList.add((JSONObject) o);
            Paths p = new Paths((JSONObject) o, this);
            pathsList.add(p);
        }

        try{
            lastMod = JSONData.getString(Utils.KEY.LAST_MOD.getVar());
        }catch (JSONException e){
            lastMod = lastModSDF.format(new Date());
        }

        if (dataPath == null){
            dataPath = new File(config.getGlobal().getRootFolderName()+"/"+name + "_Data.json");
            config.getDataFiles().newDataFile(name);
            config.save();
            save();
        }
        this.status = STATUS.READY;
    }

    public void save() {
        JSONData.put(Utils.KEY.NAME.getVar(),name);
        JSONData.put(Utils.KEY.ARCHIVE_FILE.getVar(), archiveFiles);
        JSONPathsList = getPathsAsJSON();
        JSONPaths = new JSONArray(JSONPathsList);
        JSONData.put(Utils.KEY.PATHS.getVar(),JSONPaths);
        JSONData.put(Utils.KEY.LAST_MOD.getVar(),lastMod);

        try{
            if (dataPath == null){
                dataPath = new File(config.getGlobal().getRootFolderName()+"/"+name + "_Data.json");
                config.getDataFiles().newDataFile(name);
            }
            FileWriter fw = new FileWriter(dataPath);
            fw.write(JSONData.toString(4));
            fw.close();
            this.status = STATUS.READY;
        }catch (IOException io){
            io.printStackTrace();
        }
    }

    private Thread createThread() throws Exception{
        Runnable r = () -> {
            if (checkDisabled){
                if (!allPathsEnabled()){
                    log.println(name+":");
                    pathsList.stream().filter(Paths::isDisabled).forEach(p -> {
                        try{
                            if ((!p.getDest().exists()) || (FileUtils.isFileNewer(p.getFile(), p.getDest()))) {
                                if (archiveFiles && config.getGlobal().getArchiveFiles()) archiveFile(p.getDest());
                                createFile(p.getFile(), p.getDest());
                                save();
                            } else {
                                log.println("\t\"" + getShorthandPath(p.getDest()) + "\" is up to date");
                            }
                        }catch (ParseException | IOException e){
                            log.println(e.getMessage());
                        }
                    });
                }
            }else{
                if (!allPathsDisabled()){
                    log.println(name+":");
                    pathsList.stream().filter(p -> !p.isDisabled()).forEach(p -> {
                        try{
                            if ((!p.getDest().exists()) || (FileUtils.isFileNewer(p.getFile(), p.getDest()))) {
                                if (archiveFiles && config.getGlobal().getArchiveFiles()) archiveFile(p.getDest());
                                createFile(p.getFile(), p.getDest());
                                save();
                            } else {
                                log.println("\t\"" + getShorthandPath(p.getDest()) + "\" is up to date");
                            }
                        }catch (ParseException | IOException e){
                            log.println(e.getMessage());
                        }
                    });
                }
            }
        };
        return new Thread(r);
    }

    @Override
    public void createFieldsIfEmpty() {

    }

    public Object getAsObject() {
        return this;
    }

    @Override
    public String toString() {
        return name;
    }

    /*
     * Commands
     */

    public void disablePath(Integer pos, Boolean disable){
        Paths paths = pathsList.get(pos);
        if (paths.isDisabled() && disable){
            log.println("Path already is disabled");
        }else if (!paths.isDisabled() && !disable){
            log.println("Path already is enabled");
        }else {
            if (disable){
                paths.disablePath(true);
                log.println("Disabling \"" + name + "\" path #"+ (pos + 1));
            }else {
                paths.disablePath(false);
                log.println("Enabling \"" + name + "\" path #"+ (pos + 1));
            }
        }
        save();
    }

    boolean deleteDest(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (!Files.isSymbolicLink(f.toPath())) {
                    deleteDest(f);
                }
            }
        }
        return file.delete();
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }
}
