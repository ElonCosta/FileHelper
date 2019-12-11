package ArchiveLoader;

import Utils.ConfigInterface;
import Utils.Constants.*;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static Main.Launcher.LOG;
import static Main.Launcher.config;
import static Utils.Constants.put;

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

    FilesArchive(String dataPathFile) throws FileNotFoundException{
        this(new JSONObject(new JSONTokener(new FileReader( new File(dataPathFile)))));
    }

    private FilesArchive(JSONObject JSONData){
        this.JSONData = JSONData;
        load();
        File latestFolder = new File(config.getGlobal().getVersionFolderName() + "/" + name);
        if (latestFolder.mkdir()){
            LOG.println("Creating root folder for file(s): " + name);
        }
        try{
            checker();
        }catch (IOException | ParseException ex){
            System.err.println(ex);
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

    void checker() throws IOException, ParseException{
        if (!pathsAllDisabled()){
            LOG.println(name+":");
            for (Paths path : pathsList) {
                if (!path.isDisabled()){
                    if ((!path.getDest().exists()) || (FileUtils.isFileNewer(path.getFile(), path.getDest()))) {
                        if (archiveFiles && config.getGlobal().getArchiveFiles()) archiveFile(path.getDest());
                        createFile(path.getFile(), path.getDest());
                        save();
                    } else {
                        LOG.println("    \"" + config.getGlobal().getShorthandPath(path.getDest()) + "\" is up to date");
                    }
                }
            }
        }
    }

    private void archiveFile(File destFile) throws ParseException, IOException {
        if (!destFile.exists()) return;
        if (destFile.isDirectory()) {
                File arch = new File(config.getGlobal().getArchiveFolderName() + (archSDF.format(lastModSDF.parse(lastMod))) + "/" + name + "/" + destFile.getName());
                if (arch.mkdirs()) {
                    LOG.println("    Archiving \"" + config.getGlobal().getShorthandPath(destFile) + "\\" + destFile.getName() + "\" on " + arch.getParentFile().getParent());
                    FileUtils.copyDirectory(destFile, arch, true);
                }
            }else{
                File arch = new File(config.getGlobal().getArchiveFolderName() + (archSDF.format(lastModSDF.parse(lastMod))) + "/" + name + "/" + destFile.getName());
                if (arch.mkdirs()) {
                    LOG.println("    Archiving \"" + config.getGlobal().getShorthandPath(destFile) + "\" on " + arch.getParentFile().getParent());
                    FileUtils.copyFileToDirectory(destFile, arch, true);
                }
            }
    }

    private void createFile(File file, File destFile) throws IOException{
        try{
            LOG.println(destFile.isDirectory() + " " + destFile.getAbsolutePath());
            if (destFile.delete() || !destFile.exists()){
                if (file.isDirectory()){
                    if (destFile.mkdir()){
                        LOG.println("    Archiving latest version of \"" + config.getGlobal().getShorthandPath(file) + "\" on: \"" + destFile.toString() + "\"");
                        FileUtils.copyDirectory(file,destFile,true);
                    }
                }else{
                    destFile = new File(destFile.getParent());
                    LOG.println("    Archiving latest version of \"" + config.getGlobal().getShorthandPath(file) + "\" on: \"" + destFile.toString() + "\"");
                    FileUtils.copyFileToDirectory(file, destFile, true);
                }
            }
        }catch (SecurityException e){
            LOG.println(e);
        }
    }

    private Boolean pathsAllDisabled(){
        int i = 0;

        for (Paths p: pathsList) {
            if (p.isDisabled()){
                i++;
            }
        }

        return i == pathsList.size();
    }
    /*
     * Getters
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

    /*
     * Methods inherited from ConfigInterface
     */

    public void load() {
        JSONPaths = JSONData.getJSONArray(KEY.PATHS.getVar());
        name = JSONData.getString(KEY.NAME.getVar());
        archiveFiles = JSONData.getBoolean(KEY.ARCHIVE_FILE.getVar());

        for (Object o: JSONPaths){
            JSONPathsList.add((JSONObject) o);
            pathsList.add(new Paths((JSONObject) o));
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

    public Object getAsObject() {
        return null;
    }

    /*
     * Commands
     */

    public void disablePath(Integer pos, Boolean disable){
        Paths paths = pathsList.get(pos);
        if (paths.isDisabled() && disable){
            LOG.println("Path already is disabled");
        }else if (!paths.isDisabled() && !disable){
            LOG.println("Path already is enabled");
        }else {
            if (disable){
                paths.disablePath(true);
                LOG.println("Disabling \"" + name + "\" path #"+ (pos + 1));
            }else {
                paths.disablePath(false);
                LOG.println("Enabling \"" + name + "\" path #"+ (pos + 1));
            }
        }
        save();
    }

    public class Paths implements ConfigInterface{

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

        void disablePath(Boolean disabled){
            this.disabled = disabled;
        }

        Boolean isDisabled(){
            return disabled;
        }

        void updateLatestFolder(){
            dest = new File(config.getGlobal().getVersionFolder().getAbsolutePath() + "\\" + name + "\\" + file.getName());
            save();
        }

        /*
         * Getters
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
            if (!dest.getAbsolutePath().equals(this.dest.getAbsolutePath())){
                LOG.println("Changing \""+ FilesArchive.this.getName() +"\" file destination \"" + config.getGlobal().getShorthandPath(this.dest) + "\" to \"" + config.getGlobal().getShorthandPath(dest)   + "\"");
                this.dest = dest;
            }
            FilesArchive.this.save();
        }

        public void setFile(File file) {
            if (!file.getAbsolutePath().equals(this.file.getAbsolutePath())){
                LOG.println("Changing \""+ FilesArchive.this.getName() +"\" file path \"" + config.getGlobal().getShorthandPath(this.file) + "\" to \"" + config.getGlobal().getShorthandPath(file)   + "\"");
                this.file = file;
            }
            FilesArchive.this.save();
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

        public Object getAsObject() {
            return JSONPaths;
        }
    }
}
