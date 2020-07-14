package br.com.claw.iterface.components.fileChooser;

import lombok.Getter;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileDetails{

    final long B = 1024;

    @Getter private final File file;
    @Getter private final String name;
    @Getter private final String type;
    @Getter private final Date date;
    @Getter private final String size;
    @Getter private final Long fileSize;

    public FileDetails(File file){
        this.file = file;
        type = file.isDirectory() ? "File folder" : FilenameUtils.getExtension(file.getName());
        name = file.isDirectory() ? file.getName() : file.getName().substring(0,file.getName().lastIndexOf("."));
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

    public static void sortByDate(List<FileDetails> list, boolean invert){
        sortBy(list,invert, Comparator.comparing(FileDetails::getDate));
    }

    public static void sortByType(List<FileDetails> list, boolean invert){
        sortBy(list,invert, Comparator.comparing(FileDetails::getType));
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
