package Utils;

import java.io.File;
import java.util.Map;

import static ArchiveLoader.Loader.FoldersMap;

public class FilePath {

    private Map<String, File> map = FoldersMap;

    private File file;
    private File destFile;

    public FilePath(String filePath, String destFilePath, String name){
        file = new File(filePath);
        destFile = new File(destFilePath);

        if (destFile.getName().equals(map.get("version").getName())){
            destFile = new File(map.get("version").toString() + "\\" + name + "\\" + file.getName());
        }else if (!file.getName().equals(destFile.getName())){
            destFile = new File(destFilePath + "\\" + file.getName());
        }

    }

    public File getFile() {
        return file;
    }

    public File getDestFile() {
        return destFile;
    }

}
