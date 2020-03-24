package Interface.Components.FileChooser;

import lombok.Getter;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileDetails{

    final long B = 1024;

    @Getter private File file;
    @Getter private String name;
    @Getter private String type;
    @Getter private Date date;
    @Getter private String size;
    @Getter private Long fileSize;

    public FileDetails(File file){
        this.file = file;
        type = file.isDirectory() ? "File folder" : FilenameUtils.getExtension(file.getName());
        name = FilenameUtils.getName(file.getName());
        date = new Date(file.lastModified()){
            @Override
            public String toString() {
                return (new SimpleDateFormat("dd/MM/yyyy HH:mm")).format(this);
            }
        };
        fileSize = file.length();
        size = file.isDirectory() ? "" : formatSize(file.length());
    }

    private String formatSize(long size){
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);
        double fs = size;
        String s = df.format(fs) + " B";
        if (fs > B){
            fs = (fs/B);
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

    public boolean isDirectory(){
        return file.isDirectory();
    }

    public boolean isFile(){
        return file.isFile();
    }

    public static void sortByName(List<FileDetails> list, boolean invert){
        sortBy(list, invert, Comparator.comparing(FileDetails::getName));
    }

    public static void sortBySize(List<FileDetails> list, boolean invert){
        sortBy(list, invert, Comparator.comparing(FileDetails::getFileSize));
    }

    private static void sortBy(List<FileDetails> list, boolean invert, Comparator<FileDetails> comparing) {
        List<FileDetails> tmp = new ArrayList<>();
        list.sort(comparing);
        if (invert) Collections.reverse(list);
        for (FileDetails f: list) {
            if (f.isDirectory()) tmp.add(0,f);
            else tmp.add(f);
        }
        list.clear();
        list.addAll(tmp);
    }
}
