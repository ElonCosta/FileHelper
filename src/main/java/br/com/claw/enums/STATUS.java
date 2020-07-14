package br.com.claw.enums;

import static br.com.claw.Launcher.*;

public enum STATUS {
    ARCHIVING,
    EDITING,
    NEW,
    READY,
    CHECKING;

    public static STATUS setStatus(STATUS status){
        app.updateFileList();
        return status;
    }
}
