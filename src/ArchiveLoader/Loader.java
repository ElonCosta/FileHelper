package ArchiveLoader;

import org.json.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("Duplicates")
public class Loader {

    public static Map<String, File> FoldersMap;
    private Map<String, File> getFoldersMap(){
        Map<String, File> map = new HashMap<>();

        map.put("root",new File("./"+glbCfg.getString("rootFolder")));
        map.put("version", new File(map.get("root").toString()+"/"+glbCfg.getString("versionFolder")));
        map.put("archive", new File(map.get("root").toString()+"/"+glbCfg.getString("archiveFolder")));

        return map;
    }
    private List<ArchiveData> archives = new ArrayList<>();
    private List<ArchiveData> specialCaseArchives = new ArrayList<>();
    public static File configPath = new File("./configuration.json");
    private Integer routineTime;
    private JSONObject config;
    public static JSONObject glbCfg;

    public Loader() throws Exception{
        if (!configPath.exists()){
            generateConfigFile();
            System.exit(0);
        }
    }

    public void load() throws Exception{
        JSONTokener jsonTokener = new JSONTokener(new BufferedReader(new FileReader(configPath)));
        config = new JSONObject(jsonTokener);
        glbCfg = config.getJSONObject("Global");
        routineTime = glbCfg.getInt("routineTime");
        FoldersMap = getFoldersMap();
        createFolders();
        if (glbCfg.getJSONArray("dataPaths").length() == 0){
            JSONArray array = config.getJSONArray("Files");
            System.out.println("Initial Check:");
            for (int i = 0; i < array.length(); i++){
                JSONObject File = array.getJSONObject(i).getJSONObject("File");
                ArchiveData data = new ArchiveData();
                data.loadArchive(File);
                if (data.getRoutineTime() == routineTime){
                    archives.add(data);
                }else{
                    specialCaseArchives.add(data);
                }
            }
        }else {
            JSONArray array = glbCfg.getJSONArray("dataPaths");
            System.out.println("Initial Check:");
            for (int i = 0; i < array.length(); i++){
                JSONTokener t = new JSONTokener(new BufferedReader(new FileReader(new File(array.getJSONObject(i).get("data").toString()))));
                JSONObject File = (new JSONObject(t)).getJSONObject("Data");
                ArchiveData data = new ArchiveData();
                data.loadArchive(File);
                if (data.getRoutineTime() == routineTime){
                    archives.add(data);
                }else{
                    specialCaseArchives.add(data);
                }
            }
        }
        Files.write(Paths.get(configPath.toURI()),config.toString().getBytes());
        System.out.println("Next routine execution at: \"" + (new SimpleDateFormat("HH:mm").format(getNextRoutine(routineTime))) + "\"");
        routine();
    }
    private void createFolders(){
        if (FoldersMap.get("root").mkdir()){
            System.out.println("Creating " + FoldersMap.get("root").toString());
        }
        if (FoldersMap.get("archive").mkdirs()){
            System.out.println("Creating " + FoldersMap.get("archive").toString());
        }
        if (FoldersMap.get("version").mkdir()){
            System.out.println("Creating " + FoldersMap.get("version").toString());
        }
    }
    private void generateConfigFile() throws Exception{
        JSONWriter w = new JSONStringer();
        w.object();
        w.key("Global");
        w.object();
        w.key("rootFolder").value("Source");
        w.key("versionFolder").value("Latest");
        w.key("archiveFolder").value("Archive");
        w.key("dataPaths").array().endArray();
        w.endObject();
        w.key("Files");
        w.array().object();
        w.key("File");
        w.object();
        w.key("name").value("");
        w.key("Paths").array();
        w.object();
        w.key("path").value("");
        w.key("destination").value("Latest");
        w.endObject();
        w.endArray();
        w.key("routineTime").value("");
        w.endObject();
        w.endObject().endArray();
        w.endObject();

        JSONObject object = new JSONObject(w.toString());
        Files.write(Paths.get(configPath.toURI()),object.toString().getBytes());
    }
    private void routine() throws Exception{
        Date actual = new Date();
        Date next = getNextRoutine(routineTime, actual);
        while (true){
            if (!actual.before(next)){
                for (ArchiveData data: archives){
                    data.checker();
                }
                next = getNextRoutine(routineTime, actual);
                System.out.println("Next routine execution at: " + (new SimpleDateFormat("HH:mm").format(next)));
            }
            for (ArchiveData data: specialCaseArchives){
                if (data.getRoutineTime() == 0){
                    data.specialChecker();
                }else{
                    Date spclNext = getNextRoutine(data.getRoutineTime(), actual);
                    if (!actual.before(spclNext)){
                        data.checker();
                        next = getNextRoutine(routineTime, actual);
                        System.out.println("Next routine execution at: " + (new SimpleDateFormat("HH:mm").format(spclNext)));
                    }
                }
            }
            actual = new Date();
        }
    }

    private Date getNextRoutine(int Time, Date Date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Date);
        calendar.add(Calendar.MINUTE, Time);
        return calendar.getTime();
    }

    private Date getNextRoutine(int Time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, Time);
        return calendar.getTime();
    }
}
