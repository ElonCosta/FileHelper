package ArchiveLoader;

import org.apache.commons.io.FileUtils;
import org.json.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static ArchiveLoader.Loader.FoldersMap;
import static ArchiveLoader.Loader.glbCfg;
import static ArchiveLoader.Loader.configPath;

@SuppressWarnings("Duplicates")
public class ArchiveData  {

    private List<File> files = new ArrayList<>();
    private List<File> destFiles = new ArrayList<>();
    private File dataFile;
    private JSONObject data;
    private String lastMod;
    private String name;
    private Map<String, File> map = FoldersMap;
    private Integer routineTime;
    private SimpleDateFormat lastModSDF = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    private SimpleDateFormat archSDF = new SimpleDateFormat("/yyyy/MM/dd/HH_mm/");


    ArchiveData(){

    }
    public void loadArchive(JSONObject archive) throws Exception{
            name = archive.getString("name");
            JSONArray archArr = archive.getJSONArray("Paths");
            for (int i = 0; i < archArr.length(); i++){
                JSONObject obj = archArr.getJSONObject(i);
                File file = new File(obj.getString("path"));
                files.add(file);
                String dest = obj.getString("destination");
                if (dest.equals(map.get("version").getName())){
                    destFiles.add(new File(map.get("version").toString() + "/" + name + "/" + file.getName()));
                }else{
                    File destFile = new File(dest);
                    if (destFile.getName().equals(file.getName())){
                        destFiles.add(destFile);
                    }else{
                        destFiles.add(new File(dest+"/"+file.getName()));
                    }
                }
            }
            lastMod = lastModSDF.format(new Date());
            routineTime = archive.getInt("routineTime");
            if (routineTime < 0){
                routineTime = 0;
            }
            checker();
            generateData();
    }
    public void generateData() throws IOException{
        dataFile = new File(map.get("root").toString() + "/" + name + "_Data.json");
        JSONWriter w = new JSONStringer();
        w.object();
        w.key("Data");
        w.object();
        w.key("name").value(name);
        w.key("Paths").array();
        for (File f: files){
            w.object();
            w.key("path").value(f.toString());
            w.key("destination").value(destFiles.get(files.indexOf(f)).toString());
            w.endObject();
        }
        w.endArray();
        w.key("lastMod").value(lastMod);
        w.key("routineTime").value(routineTime);
        w.endObject();
        w.endObject();

        if (updateGlbCfg()){
            JSONWriter W = new JSONStringer();
            W.object();
            W.key("name").value(name);
            W.key("data").value(dataFile.toString());
            W.endObject();

            JSONObject o = new JSONObject(W.toString());
            glbCfg.getJSONArray("dataPaths").put(o);
        }
        data = new JSONObject(w.toString());
        Files.write(Paths.get(dataFile.toURI()),data.toString().getBytes());
    }
    public void checker() throws IOException, ParseException{
        System.out.println(name+":");
        for (int i = 0; i < files.size(); i++) {
            if((!destFiles.get(i).exists())||(FileUtils.isFileNewer(files.get(i),destFiles.get(i)))){
                createFile(files.get(i), destFiles.get(i));
                generateData();
            }else {
                System.out.println("    \"" + files.get(i).getName() + "\" is up to date");
            }
        }
    }
    public void specialChecker() throws IOException, ParseException{
        for (int i = 0; i < files.size(); i++) {
            if((!destFiles.get(i).exists())||(FileUtils.isFileNewer(files.get(i),destFiles.get(i)))){
                createFile(files.get(i), destFiles.get(i));
                generateData();
            }
        }
    }
    private void createFile(File file, File inLatest) throws IOException, ParseException {
        if (!inLatest.exists()){
            if (file.isDirectory()){
                inLatest.mkdir();
                System.out.println("    Archiving latest version of \"" + name + "/" + file.getName() + "\" on: \"" + inLatest.toString() + "\"");
                FileUtils.copyDirectory(file,inLatest,true);
            }else{
                inLatest = new File(inLatest.getParent());
                inLatest.mkdir();
                System.out.println("    Archiving latest version of \"" + name + "/" + file.getName() + "\" on: \"" + inLatest.toString() + "\"");
                FileUtils.copyFileToDirectory(file, inLatest,true);
            }
        }else{
            if (inLatest.isDirectory()) {
                File arch = new File(map.get("archive").toString() + (archSDF.format(lastModSDF.parse(lastMod))) + "/" + name + "/" + inLatest.getName());
                System.out.println("    Archiving \"" + name + "/" + file.getName() + "\" on " + arch.getParentFile().getParent());
                arch.mkdirs();
                FileUtils.copyDirectory(inLatest, arch, true);
                FileUtils.deleteDirectory(inLatest);
            }else{
                File arch = new File(map.get("archive").toString() + (archSDF.format(lastModSDF.parse(lastMod))) + "/" + name + "/" + inLatest.getName());
                System.out.println("    Archiving \"" + name + "/" + file.getName() + "\" on " + arch.getParentFile().getParent());
                arch.mkdirs();
                FileUtils.copyFileToDirectory(inLatest, arch, true);
                inLatest.delete();
            }
            createFile(file, inLatest);
        }
    }

    public boolean updateGlbCfg(){
        JSONArray a = glbCfg.getJSONArray("dataPaths");
        for (int i = 0; i < a.length(); i++){
            if (a.getJSONObject(i).get("name").equals(name)){
                return false;
            }
        }
        return true;
    }

    public Integer getRoutineTime() {

        return routineTime;

    }
}
