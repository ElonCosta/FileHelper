package br.com.claw.utils;

import br.com.claw.enums.IMAGES;
import br.com.claw.enums.STATUS;
import br.com.claw.iterface.controllers.GenericController;
import br.com.claw.iterface.controllers.monitoring.FileDisplayController;
import br.com.claw.iterface.controllers.monitoring.FileListController;
import br.com.claw.iterface.controllers.monitoring.MonitoringController;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ControllersUtils {

    public static void elementDisplay(GenericController controller, String element){

        if(controller instanceof MonitoringController){
            MonitoringController con = (MonitoringController) controller;

            FileListController listCon = con.getFileListController();
            FileDisplayController displayCon = con.getFileDisplayController();

            if (element.equalsIgnoreCase("updateFileList")){
                if (listCon.getFiles().getChildren().size() == 0){
                    displayCon.editing.setVisible(false);
                    displayCon.ready.setVisible(false);
                    displayCon.pathBtns.setVisible(false);
                    displayCon.pathFile.setEditable(false);
                    displayCon.pathDest.setEditable(false);
                    displayCon.fileName.setEditable(false);
                    displayCon.archiveFile.setDisable(true);
                    displayCon.pathDisabled.setDisable(true);
                }else{
                    displayCon.pathBtns.setVisible(true);
                    displayCon.archiveFile.setDisable(false);
                    displayCon.pathDisabled.setDisable(false);
                }
            }else if (element.equalsIgnoreCase("updateFileDisplay")){
                if (displayCon.getSelectedArchive().getStatus().equals(STATUS.READY) || displayCon.getSelectedArchive().getStatus().equals(STATUS.CHECKING)){
                    displayCon.fileName.setEditable(false);
                    displayCon.editing.setVisible(false);
                    displayCon.ready.setVisible(true);
                    if (displayCon.getSelectedPaths().getStatus().equals(STATUS.READY) || displayCon.getSelectedPaths().getStatus().equals(STATUS.CHECKING)){
                        displayCon.pathDest.setEditable(false);
                        displayCon.pathFile.setEditable(false);
                        displayCon.pathDisabled.setDisable(false);
                    }else{
                        displayCon.pathDest.setEditable(true);
                        displayCon.pathFile.setEditable(true);
                        displayCon.editing.setVisible(true);
                        if(displayCon.getSelectedPaths().getStatus() == STATUS.NEW){
                            displayCon.pathDisabled.setDisable(true);
                        }
                    }
                }else{
                    if (displayCon.getSelectedArchive().getStatus().equals(STATUS.NEW) && displayCon.getSelectedArchive().getName() == null){
                        displayCon.fileName.setText("");
                    }

                    System.out.println("ay");
                    displayCon.pathFile.setEditable(true);
                    displayCon.pathDest.setEditable(true);
                    displayCon.fileName.setEditable(true);
                    displayCon.editing.setVisible(true);
                    displayCon.ready.setVisible(false);
                }
            }
        }

    }

    public static void loadImages(GenericController controller){

        if (controller instanceof MonitoringController){
            MonitoringController con = (MonitoringController) controller;
            FileListController listCon = con.getFileListController();
            FileDisplayController displayCon = con.getFileDisplayController();

            Image newIcon = new Image(IMAGES.ADD.getPath());
            ImageView createPathIcon = new ImageView(newIcon);
            createPathIcon.setScaleX(0.03);
            createPathIcon.setScaleY(0.03);
            displayCon.newPaths.setGraphic(createPathIcon);

            ImageView createArchiveIcon = new ImageView(newIcon);
            createArchiveIcon.setScaleX(0.04);
            createArchiveIcon.setScaleY(0.04);
            listCon.newArchive.setGraphic(createArchiveIcon);
        }
    }
}
