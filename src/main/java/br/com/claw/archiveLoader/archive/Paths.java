package br.com.claw.archiveLoader.archive;

import br.com.claw.archiveLoader.ConfigInterface;
import br.com.claw.enums.STATUS;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static br.com.claw.Launcher.*;
import static br.com.claw.utils.Utils.getShorthandPath;

import static br.com.claw.enums.KEY.*;
import static br.com.claw.utils.JSONUtils.*;

public class Paths implements ConfigInterface {

    @Getter @Setter
    private Archive parent;

    @Getter @Setter
    private STATUS status;

    @Getter @Setter
    private File file;
    @Getter @Setter
    private File dest;

    @Getter @Setter
    private Boolean disabled;

    private String filePath;
    private String destPath;

    private final SimpleDateFormat archSDF = new SimpleDateFormat("/yyyy/MM/dd/HH_mm/");
    private final SimpleDateFormat lastModSDF = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    private final JSONObject JSONPaths;

    Boolean onLatest = false;

    public Paths(JSONObject JSONPaths, Archive parent) {
        this.status = STATUS.READY;
        this.parent = parent;
        this.JSONPaths = JSONPaths;
        load();
    }

    public Paths() {
        this.status = STATUS.NEW;
        this.JSONPaths = new JSONObject();
        this.disabled = false;
    }

    public void disablePath(Boolean disabled) {
        this.disabled = disabled;
    }

    public Boolean isDisabled() {
        return disabled;
    }

    public Boolean isEnabled() {
        return !disabled;
    }

    public String getDefaultLatestFolder() {
        return config.getGlobal().getVersionFolder().getAbsolutePath() + File.separator + parent.getName() + File.separator + file.getName();
    }

    void check(){
        if (isEnabled()){
            status = STATUS.CHECKING;
            app.updateFileList();
            try{
                if ((!dest.exists()) || (FileUtils.isFileNewer(file, dest))) {
                    createFile(file, dest);
                    save();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
            status = STATUS.READY;
            app.updateFileList();
        }
    }

    void checkThis(){
        status = STATUS.setStatus(STATUS.CHECKING);
        try{
            if((!dest.exists() || (FileUtils.isFileNewer(file,dest)))){
                createFile(file,dest);
                save();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        status = STATUS.setStatus(STATUS.READY);
    }

    public void archiveFile(){
        status = STATUS.ARCHIVING;
        app.updateFileList();
        try{
            if (!dest.exists()) return;
            File arch = new File(config.getGlobal().getArchiveFolderName() + (archSDF.format(parent.getLastMod())) + "/" + parent.getName() + "/" + dest.getName());
            if (dest.isDirectory()) {
                if (arch.mkdirs()) {
                    FileUtils.copyDirectory(dest, arch, true);
                }
            }else{
                if (arch.mkdirs()) {
                    FileUtils.copyFileToDirectory(dest, arch, true);
                }
            }
        }catch (IOException i){
            i.printStackTrace();
        }
        status = STATUS.READY;
        app.updateFileList();
    }

    private void createFile(File file, File destFile) throws IOException{
        if (delete(destFile) || !destFile.exists()){
            if (file.isDirectory()){
                if (destFile.mkdir()){
                    FileUtils.copyDirectory(file,destFile,true);
                }
            }else{
                destFile = new File(destFile.getParent());
                FileUtils.copyFileToDirectory(file, destFile, true);
            }
        }
    }

    private boolean delete(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (!Files.isSymbolicLink(f.toPath())) {
                    delete(f);
                }
            }
        }
        return file.delete();
    }

    public Map<Boolean, String> isValid(){
        Map<Boolean, String> response = new HashMap<>();
        response.put(dest != null, (dest != null ? "Sucess" : "No destination selected"));
        response.put(file != null, (file != null ? "Sucess" : "No file selected"));
        return response;
    }

    /*
     * Getters && Setters
     */

    public Boolean onLatest() {
        return onLatest;
    }

    public void setOnLatest(boolean onLatest) {
        this.onLatest = onLatest;
        if (onLatest) {
            this.dest = new File(config.getGlobal().getVersionFolder() + "\\" + file.getName());
            if (parent.getName() != null) {
                this.dest = new File(config.getGlobal().getVersionFolder() + "\\" + parent.getName() + "\\" + file.getName());
            }
        }
    }

    public boolean isOnLatest() {
        File f = new File(dest.getAbsolutePath());
        while (true) {
            if (f.getAbsolutePath().equals(config.getGlobal().getVersionFolder().getAbsolutePath())) {
                return true;
            }
            if (f.getParent() == null) {
                return false;
            }
            f = new File(f.getParent());
        }
    }

    /*
     * Methods inherited from ConfigInterface
     */

    public void load() {
        filePath = getString(JSONPaths, PATH);
        destPath = getString(JSONPaths, DEST);

        try {
            disabled = getBoolean(JSONPaths, DISABLED);
        } catch (JSONException n) {
            n.printStackTrace();
            disabled = false;
        }

        file = new File(filePath);
        if (destPath.equals(config.getGlobal().getVersionFolder().getName())) {
            dest = new File(config.getGlobal().getVersionFolder().getAbsolutePath() + "\\" + parent.getName() + "\\" + file.getName());
            onLatest = true;
        } else {
            dest = new File(destPath);
        }

        if (!file.getName().equals(dest.getName())) {
            dest = new File(destPath + "\\" + file.getName());
        }

    }

    public void save() {
        filePath = file.getAbsolutePath();
        destPath = onLatest ? config.getGlobal().getVersionFolder().getName() : dest.getAbsolutePath();

        put(JSONPaths, PATH, filePath);
        put(JSONPaths, DEST, destPath);
        put(JSONPaths, DISABLED, disabled);

        status = STATUS.READY;
    }

    @Override
    public void createFieldsIfEmpty() {

    }

    public Object getAsObject() {
        return JSONPaths;
    }
    @Override
    public String toString() {
        return dest == null ? "New Path" : getShorthandPath(dest);
    }
}
