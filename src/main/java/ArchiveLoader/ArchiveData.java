package ArchiveLoader;

import Utils.FilePath;
import org.apache.commons.io.*;
import org.json.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static ArchiveLoader.Loader.FoldersMap;
import static ArchiveLoader.Loader.glbCfg;
import static Main.Launcher.LOG;

@SuppressWarnings("Duplicates")
public class ArchiveData  {

    private List<FilePath> paths = new ArrayList<>();
    private File dataFile;
    private JSONObject data;
    private String lastMod;
    private String name;
    private Map<String, File> map = FoldersMap;
    private SimpleDateFormat lastModSDF = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    private SimpleDateFormat archSDF = new SimpleDateFormat("/yyyy/MM/dd/HH_mm/");


    ArchiveData(JSONObject archive) throws IOException, ParseException{
        name = archive.getString("name");
        JSONArray archArr = archive.getJSONArray("Paths");
        for (int i = 0; i < archArr.length(); i++){
            JSONObject obj = archArr.getJSONObject(i);
            String orig = obj.getString("path");
            String dest = obj.getString("destination");
            paths.add(new FilePath(orig,dest, name));
        }
        lastMod = lastModSDF.format(new Date());
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
        for (FilePath p : paths){
            w.object();
            w.key("path").value(p.getFile().toString());
            w.key("destination").value(p.getDestFile().toString());
            w.endObject();
        }
        w.endArray();
        w.key("lastMod").value(lastMod);
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
        Files.write(Paths.get(dataFile.toURI()),data.toString(4).getBytes());
    }
    public void checker() throws IOException, ParseException{
        LOG.println(name+":");
        for (int i = 0; i < paths.size(); i++) {
            File file = paths.get(i).getFile();
            File destFile = paths.get(i).getDestFile();
            if((!destFile.exists())||(FileUtils.isFileNewer(file,destFile))){
                createFile(file, destFile);
                generateData();
            }else {
                LOG.println("    \"" + destFile.getName() + "\" is up to date");
            }
        }
    }
    private void createFile(File file, File inLatest) throws IOException, ParseException {
        if (!inLatest.exists()){
            if (file.isDirectory()){
                inLatest.mkdir();
                LOG.println("    Archiving latest version of \"" + file.getParentFile().getName() + "\\" + file.getName() + "\" on: \"" + inLatest.toString() + "\"");
                FileUtils.copyDirectory(file,inLatest,true);
            }else{
                inLatest = new File(inLatest.getParent());
                inLatest.mkdir();
                LOG.println("    Archiving latest version of \"" + file.getParentFile().getName() + "\\" + file.getName() + "\" on: \"" + inLatest.toString() + "\"");
                FileUtils.copyFileToDirectory(file, inLatest,true);
            }
        }else{
            if (inLatest.isDirectory()) {
                File arch = new File(map.get("archive").toString() + (archSDF.format(lastModSDF.parse(lastMod))) + "/" + name + "/" + inLatest.getName());
                LOG.println("    Archiving \"" + inLatest.getParentFile().getName() + "\\" + inLatest.getName() + "\" on " + arch.getParentFile().getParent());
                arch.mkdirs();
                FileUtils.copyDirectory(inLatest, arch, true);
                FileUtils.deleteDirectory(inLatest);
            }else{
                File arch = new File(map.get("archive").toString() + (archSDF.format(lastModSDF.parse(lastMod))) + "/" + name + "/" + inLatest.getName());
                LOG.println("    Archiving \"" + inLatest.getParentFile().getName() + "\\" + inLatest.getName() + "\" on " + arch.getParentFile().getParent());
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
}
