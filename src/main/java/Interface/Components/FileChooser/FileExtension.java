package Interface.Components.FileChooser;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileExtension {

    @Getter
    private String title;
    @Getter
    private String[] extensions;

    private FileExtension(String title, String... extensions){
        this.title = title;
        this.extensions = extensions;
    }

    public static List<FileExtension> createExtensionList(String... extensions){
        List<FileExtension> extensionList = new ArrayList<>();
        extensionList.add(new FileExtension("All files", extensions));
        if (extensions == null) return extensionList;
        for (String s: extensions){
            String title = s.substring(0,1).toUpperCase() + s.substring(1) + " files";
            extensionList.add(new FileExtension(title, s));
        }
        return extensionList;
    }

    public boolean matches(String ext){
        if (extensions.length == 0) return true;
        return Arrays.asList(extensions).contains(ext);
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
