package ArchiveLoader;

import Utils.ConfigInterface;
import Utils.Utils.*;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static Main.Launcher.log;
import static Main.Launcher.config;
import static Utils.Utils.getShorthandPath;
import static Utils.Utils.put;

@SuppressWarnings("Duplicates")
public class FilesArchive implements ConfigInterface {

    private JSONObject JSONData;

    private File dataPath;

    private String name;
    private Boolean archiveFiles;

    private JSONArray JSONPaths;
    private List<JSONObject> JSONPathsList = new ArrayList<>();
    private List<Paths> pathsList = new ArrayList<>();

    private String lastMod;

    private SimpleDateFormat lastModSDF = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    private SimpleDateFormat archSDF = new SimpleDateFormat("/yyyy/MM/dd/HH_mm/");

    FilesArchive(JSONObject JSONData){
        this.JSONData = JSONData;
        load();
        File latestFolder = new File(config.getGlobal().getVersionFolderName() + "/" + name);
        if (latestFolder.mkdir()){
            log.println("Creating root folder for file(s): " + name);
        }
    }

    private List<JSONObject> getPathsAsJSON(){
        List<JSONObject> tmp = new ArrayList<>();
        for (Paths p: pathsList) {
            p.save();
            tmp.add((JSONObject) p.getAsObject());
        }
        return tmp;
    }

    void checker(){
        if (!pathsAllDisabled()){
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

    private void createFile(File file, File destFile) throws IOException{
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

    private Boolean pathsAllDisabled(){
        return pathsList.stream().filter(Paths::isDisabled).count() == pathsList.size();
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

    /*
     * Methods inherited from ConfigInterface
     */

    public void load() {
        JSONPaths = JSONData.getJSONArray(KEY.PATHS.getVar());
        name = JSONData.getString(KEY.NAME.getVar());
        archiveFiles = JSONData.getBoolean(KEY.ARCHIVE_FILE.getVar());

        for (Object o: JSONPaths){
            JSONPathsList.add((JSONObject) o);
            Paths p = new Paths((JSONObject) o);
            p.setParent(this);
            pathsList.add(p);
        }

        try{
            lastMod = JSONData.getString(KEY.LAST_MOD.getVar());
        }catch (JSONException e){
            lastMod = lastModSDF.format(new Date());
        }

        if (dataPath == null){
            dataPath = new File(config.getGlobal().getRootFolderName()+"/"+name + "_Data.json");
            config.getDataFiles().newDataFile(name);
            config.save();
            save();
        }
    }

    public void save() {
        JSONData.put(KEY.NAME.getVar(),name);
        JSONData.put(KEY.ARCHIVE_FILE.getVar(), archiveFiles);
        JSONPathsList = getPathsAsJSON();
        JSONPaths = new JSONArray(JSONPathsList);
        JSONData.put(KEY.PATHS.getVar(),JSONPaths);
        JSONData.put(KEY.LAST_MOD.getVar(),lastMod);

        try{
            FileWriter fw = new FileWriter(dataPath);
            fw.write(JSONData.toString(4));
            fw.close();
        }catch (IOException io){
            io.printStackTrace();
        }
    }

    @Override
    public void createFieldsIfEmpty() {

    }

    public Object getAsObject() {
        return null;
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

    public class Paths implements ConfigInterface{

        private FilesArchive parent;

        private File file;
        private File dest;

        private Boolean disabled;

        private String filePath;
        private String destPath;

        private JSONObject JSONPaths;

        Boolean onLatest = false;

        Paths(JSONObject JSONPaths){
            this.JSONPaths = JSONPaths;
            load();
        }

        public void disablePath(Boolean disabled){
            this.disabled = disabled;
        }

        Boolean isDisabled(){
            return disabled;
        }

        String getDefaultLatestFolder(){
            return config.getGlobal().getVersionFolder().getAbsolutePath() + File.separator + parent.getName() + File.separator + file.getName();
        }

        /*
         * Getters && Setters
         */

        public File getFile() {
            return file;
        }

        public File getDest() {
            return dest;
        }

        public Boolean getDisabled() {
            return disabled;
        }

        public void setDest(File dest) {
            System.out.println(getDefaultLatestFolder());
            if (!dest.getAbsolutePath().equals(this.dest.getAbsolutePath())){
                onLatest = dest.getAbsolutePath().equals(getDefaultLatestFolder());
                log.println("Changing \""+ FilesArchive.this.getName() +"\" file destination \"" + getShorthandPath(this.dest) + "\" to \"" + getShorthandPath(dest)   + "\"");
                this.dest = dest;
            }
        }

        public void setFile(File file) {
            if (!file.getAbsolutePath().equals(this.file.getAbsolutePath())){
                log.println("Changing \""+ FilesArchive.this.getName() +"\" file path \"" + getShorthandPath(this.file) + "\" to \"" + getShorthandPath(file)   + "\"");
                this.file = file;
            }
        }

        public FilesArchive getParent(){
            return parent;
        }

        public void setParent(FilesArchive parent) {
            this.parent = parent;
        }

        /*
         * Methods inherited from ConfigInterface
         */

        public void load(){
            filePath = JSONPaths.getString(KEY.PATH.getVar());
            destPath = JSONPaths.getString(KEY.DEST.getVar());

            try{
                disabled = JSONPaths.getBoolean(KEY.DISABLED.getVar());
            }catch (JSONException n){
                n.printStackTrace();
                disabled = false;
            }

            file = new File(filePath);
            if(destPath.equals(config.getGlobal().getVersionFolder().getName())){
                dest = new File(config.getGlobal().getVersionFolder().getAbsolutePath() + "\\" + name + "\\" + file.getName());
                onLatest = true;
            }else{
                dest = new File(destPath);
            }

            if (!file.getName().equals(dest.getName())){
                dest = new File(destPath + "\\" + file.getName());
            }

        }

        public void save(){
            filePath = file.getAbsolutePath();
            destPath = onLatest ? config.getGlobal().getVersionFolder().getName() : dest.getAbsolutePath();

            put(JSONPaths, KEY.PATH, filePath);
            put(JSONPaths, KEY.DEST, destPath);
            put(JSONPaths, KEY.DISABLED, disabled);
        }

        @Override
        public void createFieldsIfEmpty() {

        }

        public Object getAsObject() {
            return JSONPaths;
        }
    }
}
