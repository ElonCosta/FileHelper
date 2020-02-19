package ArchiveLoader.Archive;

import Utils.ConfigInterface;
import Utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;
import Utils.Utils.STATUS;

import java.io.File;

import static Main.Launcher.config;
import static Main.Launcher.log;
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

    Boolean isDisabled() {
        return disabled;
    }

    String getDefaultLatestFolder() {
        return config.getGlobal().getVersionFolder().getAbsolutePath() + File.separator + parent.getName() + File.separator + file.getName();
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
        return getShorthandPath(dest);
    }
}
