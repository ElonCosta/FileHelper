package Interface.Components.FileChooser;

import lombok.Getter;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class FileDetails{

    final long B = 1024;

    @Getter
    private File file;
    @Getter
    private String name;
    @Getter
    private String type;
    @Getter
    private String date;
    @Getter
    private String size;

    public FileDetails(File file){
        this.file = file;
        type = file.isDirectory() ? "File folder" : FilenameUtils.getExtension(file.getName());
        name = FilenameUtils.getName(file.getName());
        date = (new SimpleDateFormat("dd/MM/yyyy HH:mm")).format(file.lastModified());
        size = file.isDirectory() ? "" : formatSize(file.length());
    }

    private String formatSize(long size){
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);
        double fs = size;
        String s = df.format(fs) + " B";
        if (fs > B){
            fs = (fs/ B);
            s = df.format(fs) + " KB";
        }
        if (fs > B){
            fs = (fs/B);
            s = df.format(fs) + " MB";
        }
        if (fs > B){
            fs = (fs/B);
            s = df.format(fs) + " GB";
        }
        return s;
    }
}
