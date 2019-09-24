package ArchiveLoader;

import Utils.ConfigInterface;
import Utils.Log.Command;
import org.json.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static Main.Launcher.LOG;
import static Utils.Constants.*;

public class GlobalCfg extends ConfigInterface {

    static Map<String, File> FoldersMap;
    private Map<String, File> getFoldersMap(){
        Map<String, File> map = new HashMap<>();

        map.put("root", rootFolder);
        map.put("version", versionFolder);
        map.put("archive", archiveFolder);

        return map;
    }

    private JSONObject config;
    private JSONObject glbCfg;

    private JSONArray filesArray;
    private JSONArray dataPathsArray;

    private String rootFolderName;
    private String versionFolderName;

    private String archiveFolderName;

    private File rootFolder;
    private File versionFolder;
    private File archiveFolder;

    private Integer routineTime;

    private Boolean displayTime;
    private Boolean archiveFiles;

    private List<DataPath> dataPaths = new ArrayList<>();

    static File configPath = new File("./configuration.json");

    GlobalCfg(){
        load();
        FoldersMap = getFoldersMap();
        LOG.newCommand(new Command("updateConfigs","-p[S]","-v[S]") {
            @Override
            public void run() {
                String param = getArg("-p").getAsString();
                String value = getArg("-v").getAsString();
                setValue(param, value);
                LOG.println("Parameter updated");
            }
        });
    }

    public void save(){
        rootFolderName = glbCfg.getString("rootFolder");
        versionFolderName = glbCfg.getString("versionFolder");
        archiveFolderName = glbCfg.getString("archiveFolder");
        routineTime = glbCfg.getInt("routineTime");
        displayTime = glbCfg.getBoolean("displayTime");
        archiveFiles = glbCfg.getBoolean("archiveFiles");

        glbCfg.put("rootFolder",rootFolderName);
        glbCfg.put("versionFolder",versionFolderName);
        glbCfg.put("archiveFolder",archiveFolderName);
        glbCfg.put("routineTime",routineTime);
        glbCfg.put("displayTime",displayTime);

        config.put("Global",glbCfg);

        try{
            Files.write(configPath.toPath(),config.toString(4).getBytes());
        }catch (IOException iO){
            iO.printStackTrace();
        }
    }

    public void load(){
        try{
            JSONTokener tok = new JSONTokener(new FileReader(configPath));
            config = new JSONObject(tok);
            filesArray = config.getJSONArray("Files");
            glbCfg = config.getJSONObject("Global");

            rootFolderName = glbCfg.getString("rootFolder");
            versionFolderName = glbCfg.getString("versionFolder");
            archiveFolderName = glbCfg.getString("archiveFolder");

            routineTime = glbCfg.getInt("routineTime");

            displayTime = glbCfg.getBoolean("displayTime");

            dataPathsArray = glbCfg.getJSONArray("dataPaths");
            
            for (int i = 0; i < dataPathsArray.length(); i++){
                dataPaths.add(new DataPath(dataPathsArray.getJSONObject(i)));
            }
            
            rootFolder = new File(rootFolderName);
            versionFolder = new File(rootFolderName+"/"+versionFolderName);
            archiveFolder = new File(rootFolderName+"/"+archiveFolderName);
        }catch (IOException iO){
            iO.printStackTrace();
        }
    }

    public void setValue(String param, Object value){
        if (!glbVars.contains(param)){
            LOG.println("Invalid parameter ["+ param +"]");
            return;
        }
        glbCfg.put(param,value);
        save();
    }

    void generateConfigFile() throws IOException{
        LOG.println("Generating config file");
        JSONWriter w = new JSONStringer();
        w.object();
        w.key("Global");
        w.object();
        w.key("routineTime").value(5);
        w.key("rootFolder").value("Source");
        w.key("versionFolder").value("Latest");
        w.key("archiveFolder").value("Archive");
        w.key("dataPaths").array().endArray();
        w.key("displayTime").value(true);
        w.endObject();
        w.key("Files");
        w.array().object();
        w.key("File");
        w.object();
        w.key("name").value("");
        w.key("Paths").array();
        w.object();
        w.key("path").value("");
        w.key("dest").value("Latest");
        w.endObject();
        w.endArray();
        w.endObject();
        w.endObject().endArray();
        w.endObject();

        JSONObject object = new JSONObject(w.toString());
        Files.write(Paths.get(configPath.toURI()),object.toString(4).getBytes());
    }

    /*
        GETTERS/SETTERS
     */

    String getRootFolderName() {
        return rootFolderName;
    }

    String getVersionFolderName() {
        return versionFolderName;
    }

    String getArchiveFolderName() {
        return archiveFolderName;
    }

    public File getRootFolder() {
        return rootFolder;
    }

    public File getVersionFolder() {
        return versionFolder;
    }

    public File getArchiveFolder() {
        return archiveFolder;
    }

    Integer getRoutineTime() {
        return routineTime;
    }

    public Boolean getDisplayTime() {
        return displayTime;
    }

    JSONObject getConfig() {
        return config;
    }

    public JSONObject getGlbCfg() {
        return glbCfg;
    }

    JSONArray getFilesArray() {
        return filesArray;
    }

    JSONArray getDataPathsArray() {
        return dataPathsArray;
    }

    List<DataPath> getDataPaths() {
        return dataPaths;
    }

    public static class DataPath {
        private String dataPath;
        private File dataFile;
        private String dataName;

        private JSONObject data;

        DataPath(JSONObject jsonObject) throws IOException{
            dataPath = jsonObject.getString("data");
            dataName = jsonObject.getString("name");

            dataFile = new File(dataPath);
            
            JSONTokener t = new JSONTokener(new BufferedReader(new FileReader(dataFile)));
            data = new JSONObject(t).getJSONObject("Data");
        }

        public String getDataPath() {
            return dataPath;
        }

        public File getDataFile() {
            return dataFile;
        }

        String getDataName() {
            return dataName;
        }

        public JSONObject getData() {
            return data;
        }
    }


}
