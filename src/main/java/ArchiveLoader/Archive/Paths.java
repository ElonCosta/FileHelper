package ArchiveLoader.Archive;

import Utils.ConfigInterface;
import Utils.Utils;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import Utils.Utils.STATUS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static Main.Launcher.*;
import static Utils.Utils.getShorthandPath;
import static Utils.Utils.put;

public class Paths implements ConfigInterface {

    private Archive parent;

    private STATUS status;

    private File file;
    private File dest;

    private Boolean disabled;

    private String filePath;
    private String destPath;

    private Date lastMod;
    private SimpleDateFormat archSDF = new SimpleDateFormat("/yyyy/MM/dd/HH_mm/");

    private JSONObject JSONPaths;

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
//                    if (parent.getArchiveFiles() && config.getGlobal().getArchiveFiles()) archiveFile(dest);
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

    private void archiveFile(File destFile) throws IOException {
        if (!destFile.exists()) return;
        if (destFile.isDirectory()) {
            File arch = new File(config.getGlobal().getArchiveFolderName() + (archSDF.format(lastMod)) + "/" + parent.getName() + "/" + destFile.getName());
            if (arch.mkdirs()) {
                FileUtils.copyDirectory(destFile, arch, true);
            }
        }else{
            File arch = new File(config.getGlobal().getArchiveFolderName() + (archSDF.format(lastMod)) + "/" + parent.getName() + "/" + destFile.getName());
            if (arch.mkdirs()) {
                FileUtils.copyFileToDirectory(destFile, arch, true);
            }
        }
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

    /*
     * Getters && Setters
     */

    public File getFile() {
        return file;
    }

    public File getDest() {
        return dest;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDest(File dest) {
        this.dest = dest;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setDest(String destPath) {
        File dest = new File(destPath);
        if (!dest.getAbsolutePath().equals(this.dest.getAbsolutePath())) {
            onLatest = dest.getAbsolutePath().equals(getDefaultLatestFolder());
            log.println("Changing \"" + parent.getName() + "\" file destination \"" + getShorthandPath(this.dest) + "\" to \"" + getShorthandPath(dest) + "\"");
            this.dest = dest;
        }
    }

    public void setFile(String filePath) {
        File file = new File(filePath);
        if (!file.getAbsolutePath().equals(this.file.getAbsolutePath())) {
            log.println("Changing \"" + parent.getName() + "\" file path \"" + getShorthandPath(this.file) + "\" to \"" + getShorthandPath(file) + "\"");
            this.file = file;
        }
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public Archive getParent() {
        return parent;
    }

    public void setParent(Archive parent) {
        this.parent = parent;
    }

    public Boolean onLatest() {
        return onLatest;
    }

    public void setOnLatest(boolean onLatest) {
        this.onLatest = onLatest;
        if (onLatest) {
            this.dest = config.getGlobal().getVersionFolder();
            if (parent.getName() != null) {
                this.dest = new File(config.getGlobal().getVersionFolder() + "\\" + parent.getName());
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
        filePath = JSONPaths.getString(Utils.KEY.PATH.getVar());
        destPath = JSONPaths.getString(Utils.KEY.DEST.getVar());

        try {
            disabled = JSONPaths.getBoolean(Utils.KEY.DISABLED.getVar());
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

        put(JSONPaths, Utils.KEY.PATH, filePath);
        put(JSONPaths, Utils.KEY.DEST, destPath);
        put(JSONPaths, Utils.KEY.DISABLED, disabled);
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
