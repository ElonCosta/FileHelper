import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;
import org.json.JSONWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ArchiveData {

    private String[] toAvoid;
    private File file;
    private File dataFile;
    private JSONObject data;
    private Long size;
    private String lastMod;
    private String name;

    ArchiveData(JSONObject archive){
        try{
            dataFile = new File(archive.getString("dataPath"));
            if (!dataFile.exists()){
                file = new File(archive.getString("path"));
                name = archive.getString("name");
                toAvoid = new String[archive.getJSONArray("extensionsToAvoid").length()];
                for (int i = 0; i < toAvoid.length;i++){
                    toAvoid[i] = archive.getJSONArray("extensionsToAvoid").getJSONObject(i).getString("Extension");
                }
                size = file.getTotalSpace();
                lastMod = new SimpleDateFormat("yyyy/MM/dd hh:mm").format(new Date());
                System.out.println(lastMod);
                generateData();
            }else{
                loadData();
                System.out.println(lastMod);
            }
        }catch (Exception e){
            System.err.println(e);
        }
    }

    public void generateData(){
        JSONWriter w = new JSONStringer();
        w.object();
        w.key("File");
        w.object();
        w.key("name").value(name);
        w.key("size").value(size);
        w.key("path").value(file.toString());
        w.key("lastMod").value(lastMod);
        w.key("extensionsToAvoid").array();
        for (String ext: toAvoid){
            w.object();
            w.key("Extension").value(ext);
            w.endObject();
        }
        w.endArray();
        w.endObject();
        w.endObject();

        data = new JSONObject(w.toString());
        try{
            Files.write(Paths.get(dataFile.toURI()),data.toString().getBytes());
        }catch (Exception e){
            System.err.println(e);
        }
    }

    public void loadData(){
        try{
            JSONTokener t = new JSONTokener(new BufferedReader(new FileReader(dataFile)));
            data = new JSONObject(t).getJSONObject("File");
            file = new File(data.getString("path"));
            name = data.getString("name");
            toAvoid = new String[data.getJSONArray("extensionsToAvoid").length()];
            for (int i = 0; i < toAvoid.length;i++){
                toAvoid[i] = data.getJSONArray("extensionsToAvoid").getJSONObject(i).getString("Extension");
            }
            size = data.getLong("size");
            lastMod = data.getString("lastMod");
        }catch (Exception e){
            System.err.println(e);
        }
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public String[] getToAvoid() {
        return toAvoid;
    }
}
