package br.com.claw.utils;

import br.com.claw.enums.FXML_FILES;

public class FXMLLoader extends javafx.fxml.FXMLLoader {

    public FXMLLoader(FXML_FILES fxmlFiles){
        super(fxmlFiles.getPath());
    }


}
