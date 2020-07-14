package br.com.claw.enums;

import lombok.Getter;

public enum KEY {
    //Configurations Master Keys
    CFG_VER("1.0"),
    CFG_VERSION("cfgVersion"),
    GLOBAL("Global"),
    DATA_FILES("DataFiles"),

    //Global keys
    ROOT_FOLDER("rootFolder"),
    ROUTINE_TIME("routineTime"),
    ARCHIVE_FOLDER("archiveFolder"),
    VERSION_FOLDER("versionFolder"),
    HASH_KEY("key"),

    //DataFile Keys
    ARCHIVE_FILE("archiveFile"),
    LAST_MOD("lastMod"),

    //Paths Keys
    PATHS("Paths"),

    DEST("dest"),
    DISABLED("disabled"),

    ID("id"),
    NAME("name"),
    PATH("path");

    @Getter
    private final String var;

    KEY(String var){
        this.var = var;
    }

    public static KEY[] globalKeys = new KEY[]{VERSION_FOLDER, ROOT_FOLDER, ROUTINE_TIME, ARCHIVE_FOLDER, HASH_KEY};
    public static KEY[] configKeys = new KEY[]{CFG_VERSION, GLOBAL, DATA_FILES};
}
