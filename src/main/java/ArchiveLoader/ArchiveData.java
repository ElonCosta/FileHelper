package ArchiveLoader;

import org.apache.commons.io.*;
import org.json.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import ArchiveLoader.GlobalCfg.DataPath;

import static ArchiveLoader.Loader.glbCfg;
import static Main.Launcher.LOG;

@SuppressWarnings("Duplicates")
public class ArchiveData  {

    private List<FilePath> paths = new ArrayList<>();
    private DataPath fileData;
    private String lastMod;
    private String name;
    private Boolean archiveFile;
    private SimpleDateFormat lastModSDF = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    private SimpleDateFormat archSDF = new SimpleDateFormat("/yyyy/MM/dd/HH_mm/");

    private File dataFile;

    public String getName() {
        return name;
    }

    ArchiveData(DataPath fileData) throws IOException, ParseException{
        this.fileData = fileData;
        loadData();
        checker();
    }
    ArchiveData(JSONObject archive) throws IOException, ParseException{

        name = archive.getString("name");
        dataFile = new File(glbCfg.getRootFolderName() + "/" + name + "_Data.json");
        archiveFile = false;
        JSONArray archArr = archive.getJSONArray("Paths");
        for (int i = 0; i < archArr.length(); i++){
            paths.add(new FilePath(archArr.getJSONObject(i), name));
        }
        lastMod = lastModSDF.format(new Date());
        checker();
        archiveFile = archive.getBoolean("archiveFile");
        generateData();
    }

    private void generateData() throws IOException{
        JSONWriter w = new JSONStringer();
        w.object();
        w.key("Data");
        w.object();
        w.key("archiveFile").value(archiveFile);
        w.key("name").value(name);
        w.key("Paths").array();
        for (FilePath p : paths){
            w.object();
            w.key("path").value(p.getFilePath());
            w.key("dest").value(p.getDestFilePath());
            w.key("disabled").value(p.isDisabled());
            w.endObject();
        }
        w.endArray();
        w.key("lastMod").value(lastMod);
        w.endObject();
        w.endObject();

        JSONObject data = new JSONObject(w.toString());
        Files.write(Paths.get(dataFile.toURI()), data.toString(4).getBytes());

        if (updateGlbCfg()){
            JSONWriter W = new JSONStringer();
            W.object();
            W.key("name").value(name);
            W.key("data").value(dataFile.toString());
            W.endObject();

            JSONObject o = new JSONObject(W.toString());
            fileData = new DataPath(o);
            glbCfg.getDataPathsArray().put(o);
        }

    }

    public void loadData() {
        JSONObject archive = fileData.getData();

        name = archive.getString("name");
        archiveFile = archive.getBoolean("archiveFile");

        dataFile = new File(glbCfg.getRootFolderName() + "/" + name + "_Data.json");

        JSONArray archArr = archive.getJSONArray("Paths");

        for (int i = 0; i < archArr.length(); i++){
            paths.add(new FilePath(archArr.getJSONObject(i), name));
        }

        for (FilePath fp:
             paths) {
            System.out.println(fp.destFilePath);
        }

        lastMod = lastModSDF.format(new Date());
    }

    public void saveData(){
        try{
            JSONWriter w = new JSONStringer();
            w.object();
            w.key("Data");
            w.object();
            w.endObject();
            w.endObject();
            JSONObject data = new JSONObject(w.toString());

            JSONWriter W = new JSONStringer();
            W.array();
            for (FilePath fp : paths){
                String filePath = fp.getFilePath();
                String destFilePath = fp.getDestFilePath();
                Boolean disabled = fp.isDisabled();

                W.object();
                W.key("path").value(filePath);
                W.key("dest").value(destFilePath);
                W.key("disabled").value(disabled);
                W.endObject();
            }
            W.endArray();
            Boolean archiveFile = this.archiveFile;
            String name = this.name;
            String lastMod = this.lastMod;
            JSONArray paths = new JSONArray(W.toString());

            fileData.getData().put("archiveFile",archiveFile);
            fileData.getData().put("name",name);
            fileData.getData().put("Paths",paths);
            fileData.getData().put("lastMod", lastMod);

            data.put("Data",fileData.getData());

            Files.write(Paths.get(dataFile.toURI()), data.toString(4).getBytes());
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    void checker() throws IOException, ParseException{
        LOG.println(name+":");
        for (FilePath path : paths) {
            if (!path.disabled){
                File file = path.getFile();
                File destFile = path.getDestFile();
                if ((!destFile.exists()) || (FileUtils.isFileNewer(file, destFile))) {
                    if (archiveFile) archiveFile(file, destFile);
                    createFile(file, destFile);
                    saveData();
                } else {
                    LOG.println("    \"" + destFile.getName() + "\" is up to date");
                }
            }
        }
    }
    private void createFile(File file, File inLatest) throws IOException, ParseException {
        if (inLatest.delete()){
            if (file.isDirectory()){
                if (inLatest.mkdir()){
                    LOG.println("    Archiving latest version of \"" + file.getParentFile().getName() + "\\" + file.getName() + "\" on: \"" + inLatest.toString() + "\"");
                    FileUtils.copyDirectory(file,inLatest,true);
                }
            }else{
                inLatest = new File(inLatest.getParent());
                if (inLatest.mkdir()) {
                    LOG.println("    Archiving latest version of \"" + file.getParentFile().getName() + "\\" + file.getName() + "\" on: \"" + inLatest.toString() + "\"");
                    FileUtils.copyFileToDirectory(file, inLatest, true);
                }
            }
        }
    }
    private void archiveFile(File file, File inLatest) throws IOException, ParseException {
        if (archiveFile){
            if (inLatest.isDirectory()) {
                File arch = new File(glbCfg.getArchiveFolderName() + (archSDF.format(lastModSDF.parse(lastMod))) + "/" + name + "/" + inLatest.getName());
                if (arch.mkdirs()) {
                    LOG.println("    Archiving \"" + inLatest.getParentFile().getName() + "\\" + inLatest.getName() + "\" on " + arch.getParentFile().getParent());
                    FileUtils.copyDirectory(inLatest, arch, true);
                }
            }else{
                File arch = new File(glbCfg.getArchiveFolderName() + (archSDF.format(lastModSDF.parse(lastMod))) + "/" + name + "/" + inLatest.getName());
                if (arch.mkdirs()) {
                    LOG.println("    Archiving \"" + inLatest.getParentFile().getName() + "\\" + inLatest.getName() + "\" on " + arch.getParentFile().getParent());
                    FileUtils.copyFileToDirectory(inLatest, arch, true);
                }
            }
        }

    }

    private boolean updateGlbCfg(){
        for (DataPath data: glbCfg.getDataPaths()){
            if (data.getDataName().equals(name)){
                return false;
            }
        }
        return true;
    }

    List<FilePath> getPaths() {
        return paths;
    }

    public static class FilePath {

        private JSONObject obj;

        private File file;
        private File destFile;

        private String filePath;
        private String destFilePath;

        private boolean disabled;

        FilePath(JSONObject o, String name){
            filePath = o.getString("path");
            destFilePath = o.getString("dest");

            obj = o;

            file = new File(filePath);
            destFile = new File(destFilePath);

            disabled = false;

            if (destFile.getName().equals(glbCfg.getVersionFolderName())){
                destFile = new File(glbCfg.getVersionFolderName() + "\\" + name + "\\" + file.getName());
            }else if (!file.getName().equals(destFile.getName())){
                destFile = new File(destFilePath + "\\" + file.getName());
            }
        }

        public File getFile() {
            return file;
        }

        File getDestFile() {
            return destFile;
        }

        boolean isDisabled() {
            return disabled;
        }

        void disable(boolean disabled){
            obj.put("disabled",disabled);
        }

        public String getFilePath() {
            return filePath;
        }

        public String getDestFilePath() {
            return destFilePath;
        }
    }

    public static class DataFile {

    }
}
