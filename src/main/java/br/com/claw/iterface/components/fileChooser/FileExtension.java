package br.com.claw.iterface.components.fileChooser;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileExtension {

    @Getter
    private String title;
    @Getter
    private String[] extensions;

    public FileExtension(String title, String... extensions){
        this.title = title;
        this.extensions = extensions;
    }

    public static List<FileExtension> createExtensionList(String... extensions){
        List<FileExtension> extensionList = new ArrayList<>();
        if (extensions.length != 1) {
            extensionList.add(new FileExtension("All files", getAllExtensions(extensions)));
        }
        if (extensions.length == 0) return extensionList;
        for (String s: extensions){
            if (s.startsWith(">")){
                String[] extS = s.split("\n");
                String title = extS[0].substring(1);
                extensionList.add(new FileExtension(title, getAllExtensions(s)));
            }else{
                String title = s.substring(0,1).toUpperCase() + s.substring(1) + " files";
                extensionList.add(new FileExtension(title, s));
            }
        }
        return extensionList;
    }

    public boolean matches(String ext){
        if (extensions.length == 0) return true;
        return Arrays.asList(extensions).contains(ext);
    }

    private static String[] getAllExtensions(String... extensions){
        List<String> exts = new ArrayList<>();
        for (String s: extensions) {
            if (s.startsWith(">")){
                for (String ss: s.split("\n")) {
                    if (ss.startsWith(">")) continue;
                    exts.add(ss);
                }
            }else {
                exts.add(s);
            }
        }
        String[] allExts = new String[exts.size()];
        return exts.toArray(allExts);
    }

    @Override
    public String toString() {
        StringBuilder allExts = new StringBuilder(" (");
        if (extensions.length == 0) allExts.append("*.*");
        for (int i = 0; i < extensions.length; i++){
            allExts.append("*.").append(extensions[i]);
            if (i < extensions.length-1){
                allExts.append(", ");
            }
        }
        allExts.append(")");
        return title + allExts;
    }
}
