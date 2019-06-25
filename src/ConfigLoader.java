import org.json.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigLoader {

    private ArchiveData[] archives;
    private Helper helper;
    private File configPath = new File("configuration.json");
    private JSONObject config;

    ConfigLoader(Helper helper) throws Exception{
        if (!configPath.exists()){
            generateConfigFile();
        }
        this.helper = helper;
    }

    public void load() throws Exception{
        JSONTokener jsonTokener = new JSONTokener(new BufferedReader(new FileReader(configPath)));
        config = new JSONObject(jsonTokener);
        JSONObject glbCfg = config.getJSONObject("Global");
        helper.setSource(glbCfg.getString("rootFolderName"));
        helper.setArchive(glbCfg.getString("archiveFolderName"));
        helper.setLatest(glbCfg.getString("versionFolderName"));
        helper.createFolders();
        JSONArray array = config.getJSONArray("Files");
        archives = new ArchiveData[array.length()];
        System.out.println("Initial Check: \n");
        for (int i = 0; i < array.length(); i++){
            JSONObject File = array.getJSONObject(i);
            ArchiveData data = new ArchiveData(File.getJSONObject("File"));
            helper.checker(data);
        }
    }

    private void generateConfigFile() throws Exception{
        JSONWriter w = new JSONStringer();
        w.object();
        w.key("Global");
        w.object();
        w.key("rootFolderName").value("Source");
        w.key("fileVersionFolderName").value("Latest");
        w.key("archiveFolderName").value("Archive");
        w.key("extensionToAvoid").array().endArray();
        w.key("dataPaths").array().endArray();
        w.endObject();
        w.key("Files");
        w.array().object();
        w.key("File");
        w.object();
        w.key("name").value("");
        w.key("path").value("");
        w.key("dataPath").value("");
        w.key("extensionToAvoid").array().object().key("extension").value("").endObject().endArray();
        w.endObject();
        w.endObject().endArray();
        w.endObject();

        JSONObject object = new JSONObject(w.toString());
        Files.write(Paths.get(configPath.toURI()),object.toString().getBytes());
    }
}
