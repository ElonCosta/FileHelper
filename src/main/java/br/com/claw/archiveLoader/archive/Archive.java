package br.com.claw.archiveLoader.archive;

import br.com.claw.archiveLoader.ConfigInterface;
import br.com.claw.enums.STATUS;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static br.com.claw.Launcher.*;
import static br.com.claw.utils.JSONUtils.*;
import static br.com.claw.enums.KEY.*;

public class Archive implements ConfigInterface {

    private final JSONObject JSONData;

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

    @Getter
    private Date lastMod;

    private final SimpleDateFormat lastModSDF = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    public Archive(JSONObject JSONData){
        this.JSONData = JSONData;
        load();
        File latestFolder = new File(config.getGlobal().getVersionFolderName() + "/" + name);
        latestFolder.mkdir();
    }

    public Archive() {
        this.JSONData = new JSONObject();
        this.archiveFiles = true;
        this.lastMod = new Date();
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

    public void check(){
        status = archiveFiles ? STATUS.setStatus(STATUS.ARCHIVING) : null;
        if (status != null){
            pathsList.forEach(Paths::archiveFile);
        }
        status = STATUS.setStatus(STATUS.CHECKING);
        pathsList.forEach(Paths::check);
        status = STATUS.setStatus(STATUS.READY);
    }

    public void checkThis(){
        status = archiveFiles ? STATUS.setStatus(STATUS.ARCHIVING) : null;
        if (status != null){
            pathsList.forEach(Paths::archiveFile);
        }
        status = STATUS.setStatus(STATUS.CHECKING);
        pathsList.forEach(Paths::checkThis);
        status = STATUS.setStatus(STATUS.READY);
    }

    public void checkThis(Paths p){
        if (!pathsList.contains(p)) return;

        status = archiveFiles ? STATUS.setStatus(STATUS.ARCHIVING) : null;
        if (status != null){
            p.archiveFile();
        }
        status = STATUS.setStatus(STATUS.CHECKING);
        p.checkThis();
        status = STATUS.setStatus(STATUS.READY);
    }

    private Boolean allPathsDisabled(){
        return pathsList.stream().filter(Paths::isDisabled).count() == pathsList.size();
    }

    private Boolean allPathsEnabled(){
        return pathsList.stream().filter(Paths::isEnabled).count() == pathsList.size();
    }

    public Boolean isReady(){
        return status == STATUS.READY;
    }

    public Paths createNewPath(){
        Paths path = new Paths();
        path.setParent(this);
        path.setStatus(STATUS.NEW);
        pathsList.add(path);
        return path;
    }

    public void generateId(){
        int i = this.hashCode();
        i = i << 8;
        id = (Integer.toString(i,16) + "["+Integer.toString(this.getClass().hashCode(),16)+"]").replaceAll("-","0");
    }

    public Map<Boolean, String> isValid(){
        Map<Boolean, String> response = new HashMap<>();
        response.put(name != null, (name != null ? "Sucess" : "Empty name"));
        return response;
    }

    /*
     * Methods inherited from ConfigInterface
     */

    public void load() {
        JSONPaths = getJSONArray(JSONData, PATHS);
        name = getString(JSONData,NAME);
        archiveFiles = getBoolean(JSONData,ARCHIVE_FILE);

        id = getString(JSONData,ID);

        if (id.trim().equals("")) generateId();

        JSONPathsList = new ArrayList<>();
        pathsList = new ArrayList<>();
        for (Object o: JSONPaths){
            JSONPathsList.add((JSONObject) o);
            Paths p = new Paths((JSONObject) o, this);
            pathsList.add(p);
        }

        try{
            lastMod = lastModSDF.parse(getString(JSONData, LAST_MOD));
        }catch (JSONException | ParseException e){
            lastMod = new Date();
        }

        if (dataPath == null){
            dataPath = new File(config.getGlobal().getRootFolderName()+"/"+(name.replaceAll("\\s","_"))+ "_Data.json");
            config.getDataFiles().newDataFile(name);
            config.save();
            save();
        }
        this.status = STATUS.READY;
    }

    public void save() {
        put(JSONData, NAME, name);
        put(JSONData, ARCHIVE_FILE, archiveFiles);
        JSONPathsList = getPathsAsJSON();
        JSONPaths = new JSONArray(JSONPathsList);
        put(JSONData, PATHS, JSONPaths);
        put(JSONData, LAST_MOD, lastMod);
        put(JSONData,ID,id);
        try{
            if (dataPath == null){
                dataPath = new File(config.getGlobal().getRootFolderName()+"/"+(name.replaceAll("\\s","_"))+ "_Data.json");
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
