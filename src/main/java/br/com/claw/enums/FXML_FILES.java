package br.com.claw.enums;

import lombok.Getter;

import java.net.URL;

public enum FXML_FILES{

    APP_UI("/UI/FXML/AppUI.fxml"),
    CONFIG_UI("/UI/FXML/ConfigurationsUI.fxml"),
    MONITORING_UI("/UI/FXML/MonitoringUI.fxml"),
    MONITORING_FILE_LIST_UI("/UI/FXML/MonitoringUI/FileList.fxml"),
    MONITORING_FILE_DISPLAY_UI("/UI/FXML/MonitoringUI/FileDisplay.fxml"),
    FILE_CHOOSER_UI("/UI/FXML/FileChooser.fxml");

    @Getter
    private final URL path;

    FXML_FILES(String url){
        path = FXML_FILES.class.getResource(url);
    }

}
