package ArchiveLoader.Archive;

import Utils.ConfigInterface;
import Utils.Utils;
import lombok.Getter;
import lombok.Setter;
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

import static Main.Launcher.*;
import static Utils.Utils.*;

public class Archive implements ConfigInterface {

    private JSONObject JSONData;

    @Getter @Setter
    private STATUS status;

    private File dataPath;

    @Getter @Setter
    private String name;
    @Getter
    private String id;
    @Getter @Setter
    private Boolean archiveFiles;

    private JSONArray JSONPaths;
    private List<JSONObject> JSONPathsList;
    @Getter @Setter
    private List<Paths> pathsList;
    @Getter @Setter
    private List<Paths> newPathsList;

    @Getter
    private String lastMod;

    private SimpleDateFormat lastModSDF = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    public Archive(JSONObject JSONData){
        this.JSONData = JSONData;
        load();
        File latestFolder = new File(config.getGlobal().getVersionFolderName() + "/" + name);
        if (latestFolder.mkdir());
    }

    public Archive() {
        this.JSONData = new JSONObject();
        this.archiveFiles = true;
        this.lastMod = lastModSDF.format(new Date());
        this.status = STATUS.NEW;
        this.JSONPathsList = new ArrayList<>();
        this.pathsList = new ArrayList<>();
        this.newPathsList = new ArrayList<>();
    }

    private List<JSONObject> getPathsAsJSON(){
        List<JSONObject> tmp = new ArrayList<>();
        for (Paths p: pathsList) {
            p.save();
            tmp.add((JSONObject) p.getAsObject());
        }
        for (Paths p: newPathsList){
            p.save();
            tmp.add((JSONObject) p.getAsObject());
        }
        return tmp;
    }

    public void archiveFiles(){
        status = STATUS.ARCHIVING;
        app.updateFileList();
        pathsList.forEach(Paths::archiveFile);
        status = STATUS.READY;
        app.updateFileList();
    }

    public void check(){
        status = STATUS.CHECKING;
        app.updateFileList();
        pathsList.forEach(Paths::check);
        status = STATUS.READY;
        app.updateFileList();
    }

    private Boolean allPathsDisabled(){
        return pathsList.stream().filter(Paths::isDisabled).count() == pathsList.size();
    }

    private Boolean allPathsEnabled(){
        return pathsList.stream().filter(Paths::isEnabled).count() == pathsList.size();
    }

    public Paths createNewPath(){
        Paths path = new Paths();
        path.setParent(this);
        newPathsList.add(path);
        return path;
    }

    public void generateId(){
        int i = this.hashCode();
        i = i << 8;
        id = Integer.toString(i,16) + "["+Integer.toString(this.getClass().hashCode(),16)+"]";
    }

    /*
     * Methods inherited from ConfigInterface
     */

    public void load() {
        JSONPaths = JSONData.getJSONArray(Utils.KEY.PATHS.getVar());
        name = JSONData.getString(Utils.KEY.NAME.getVar());
        archiveFiles = JSONData.getBoolean(Utils.KEY.ARCHIVE_FILE.getVar());

        id = getString(JSONData,Utils.KEY.ID);

        if (id.trim().equals("")) generateId();

        System.out.println(id + name);

        JSONPathsList = new ArrayList<>();
        pathsList = new ArrayList<>();
        newPathsList = new ArrayList<>();
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
        put(JSONData,KEY.ID,id);
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

    @Override
    public void createFieldsIfEmpty() {

    }

    public Object getAsObject() {
        return this;
    }

    @Override
    public String toString() {
        return name == null ? "New File" : name;
    }
}
