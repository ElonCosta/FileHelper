package ArchiveLoader;

import Utils.Log.Command;
import org.apache.commons.io.FileUtils;
import org.json.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static Main.Launcher.LOG;

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
    public File configPath = new File("./configuration.json");
    public JSONTokener tok;
    private Integer routineTime;
    private JSONObject config;
    private Date lastDateUpdate;
    public static JSONObject glbCfg;

    public Loader() throws IOException{
        if (!configPath.exists()){
            generateConfigFile();
            System.exit(0);
        }
    }

    public void load() throws IOException, ParseException{
        lastDateUpdate = new Date();
        loadGlb();
        routineTime = glbCfg.getInt("routineTime");
        FoldersMap = getFoldersMap();
        createFolders();
        LOG.println("Initial Check:");
        if (glbCfg.getJSONArray("dataPaths").length() == 0){
            JSONArray array = config.getJSONArray("Files");
            for (int i = 0; i < array.length(); i++){
                JSONObject File = array.getJSONObject(i).getJSONObject("File");
                ArchiveData data = new ArchiveData(File);
                archives.add(data);
            }
        }else {
            JSONArray array = glbCfg.getJSONArray("dataPaths");
            for (int i = 0; i < array.length(); i++){
                JSONTokener t = new JSONTokener(new BufferedReader(new FileReader(new File(array.getJSONObject(i).get("data").toString()))));
                JSONObject File = (new JSONObject(t)).getJSONObject("Data");
                ArchiveData data = new ArchiveData(File);
                archives.add(data);
            }
        }
        Files.write(Paths.get(configPath.toURI()),config.toString(4).getBytes());
        LOG.println("Next routine execution at: \"" + (new SimpleDateFormat("HH:mm").format(getNextRoutine(routineTime))) + "\"");
        routine();
    }
    private void createFolders(){
        if (FoldersMap.get("root").mkdir())     LOG.println("Creating " + FoldersMap.get("root").toString());
        if (FoldersMap.get("archive").mkdirs()) LOG.println("Creating " + FoldersMap.get("archive").toString());
        if (FoldersMap.get("version").mkdir())  LOG.println("Creating " + FoldersMap.get("version").toString());
    }
    private void generateConfigFile() throws IOException{
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
                w.endObject();
            w.endObject().endArray();
        w.endObject();

        JSONObject object = new JSONObject(w.toString());
        Files.write(Paths.get(configPath.toURI()),object.toString(4).getBytes());
    }
    private void routine() throws IOException, ParseException {
        Date actual = new Date();
        Date next = getNextRoutine(routineTime, actual);
        while (true){
            close();
            if (forceCheck()){
                next = getNextRoutine(routineTime, actual);
                LOG.println("Next routine execution at: \"" + (new SimpleDateFormat("HH:mm").format(next)) + "\"");
            }
            if (!actual.before(next)){
                for (ArchiveData data: archives){
                    data.checker();
                }
                next = getNextRoutine(routineTime, actual);
                LOG.println("Next routine execution at: \"" + (new SimpleDateFormat("HH:mm").format(next)) + "\"");
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

    private void reloadGlb() throws FileNotFoundException {
        if(FileUtils.isFileNewer(configPath, lastDateUpdate)){
            tok = new JSONTokener(new FileReader(configPath));
            try{
                config = new JSONObject(tok);
                glbCfg = config.getJSONObject("Global");
            }catch (JSONException e){
            }
        }
    }

    private void loadGlb() throws  FileNotFoundException{
        tok = new JSONTokener(new FileReader(configPath));
        try{
            config = new JSONObject(tok);
            glbCfg = config.getJSONObject("Global");
        }catch (JSONException e){
        }
        LOG.newCommand(new Command("close") {
            @Override
            public void run() {
                try{
                    close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void close() throws IOException, ParseException{
        if (LOG.readLog("close")){
            LOG.println("Checking files before closing");
            for (ArchiveData data: archives){
                data.checker();
            }
            LOG.println("Closing FileHelper");
            System.exit(0);
        }
    }

    private boolean forceCheck() throws IOException, ParseException{
        if (LOG.readLog("forceCheck")){
            for (ArchiveData data: archives){
                data.checker();
            }
            glbCfg.put("forceCheck", false);
            Files.write(Paths.get(configPath.toURI()),config.toString(4).getBytes());
            return true;
        }
        return false;
    }
}
