package Interface.UI_Controller;

import ArchiveLoader.FilesArchive;
import Interface.UI.AppUI;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

import Utils.Constants.*;

@SuppressWarnings("Duplicates")
public class AppUIController{

    private static AppUI appUI;

    private Boolean logDisabled = false;
    private Boolean cfgDisabled = false;
    private Boolean fleDisabled = false;
    private Boolean newFleDisabled = false;

    static {
        appUI = new AppUI();
        appUI.setVisible(true);
    }

    public AppUIController(){
        initEvents();

        navigation(UIVE.LOG_BUTTON_NAME);
    }

    private void initEvents(){
        appUI.getBtnConfigs().addActionListener(e -> navigation(UIVE.CONFIG_BUTTON_NAME));
        appUI.getBtnDataFiles().addActionListener(e -> navigation(UIVE.FILES_BUTTON_NAME));
        appUI.getBtnLog().addActionListener(e -> navigation(UIVE.LOG_BUTTON_NAME));
        appUI.getBtnNewFile().addActionListener(e -> navigation(UIVE.NEW_FILE_BUTTON_NAME));
    }

    private void navigation(UIVE key){

        JPanel container = appUI.getContainer();

        CardLayout card = (CardLayout) container.getLayout();
        card.show(container, key.getVar());

        appUI.getBtnConfigs().setEnabled(!cfgDisabled);
        appUI.getBtnDataFiles().setEnabled(!fleDisabled);
        appUI.getBtnLog().setEnabled(!logDisabled);
        appUI.getBtnNewFile().setEnabled(!newFleDisabled);

        if (key.equals(UIVE.LOG_BUTTON_NAME)){
            appUI.setBounds(appUI.getX(), appUI.getY(), 530, 409);
            appUI.getBtnLog().setEnabled(false);
            LogUIController t = (LogUIController) appUI.getContainer().getComponent(0);
            t.getCmdField().requestFocus();
        }else if (key.equals(UIVE.CONFIG_BUTTON_NAME)){
            appUI.setBounds(appUI.getX(), appUI.getY(), 530, 320);
            appUI.getBtnConfigs().setEnabled(false);
        }else if (key.equals(UIVE.FILES_BUTTON_NAME)){
            appUI.setBounds(appUI.getX(), appUI.getY(), 530, 335);
            appUI.getBtnDataFiles().setEnabled(false);
        }else if (key.equals(UIVE.NEW_FILE_BUTTON_NAME)){
            appUI.setBounds(appUI.getX(), appUI.getY(), 530, 320);
            appUI.getBtnNewFile().setEnabled(false);
        }

    }

    public String readCmd(){
        return ((LogUIController) appUI.getContainer().getComponent(0)).readCmd();
    }

    public void appendLog(String s){
        ((LogUIController) appUI.getContainer().getComponent(0)).appendLog(s);
    }

    public void clearLog(){((LogUIController) appUI.getContainer().getComponent(0)).clearLog();}

    public void updateInterface(){
        ((ConfigsUIController) appUI.getContainer().getComponent(1)).updateInterface();
    }

    public void createTabs(Map<String, FilesArchive> m){
        ((DataUIController) appUI.getContainer().getComponent(2)).createTabs(m);
    }

    public void disableButton(UIVE key){
        if (key.equals(UIVE.LOG_BUTTON_NAME)){
            logDisabled = true;
            appUI.getBtnLog().setEnabled(false);
        }else if (key.equals(UIVE.CONFIG_BUTTON_NAME)){
            cfgDisabled = true;
            appUI.getBtnConfigs().setEnabled(false);
        }else if (key.equals(UIVE.FILES_BUTTON_NAME)){
            fleDisabled = true;
            appUI.getBtnDataFiles().setEnabled(false);
        }else if (key.equals(UIVE.NEW_FILE_BUTTON_NAME)){
            newFleDisabled = true;
            appUI.getBtnDataFiles().setEnabled(false);
        }
    }

    public void enableButton(UIVE key){
        if (key.equals(UIVE.LOG_BUTTON_NAME)){
            logDisabled = false;
            appUI.getBtnLog().setEnabled(true);
        }else if (key.equals(UIVE.CONFIG_BUTTON_NAME)){
            cfgDisabled = false;
            appUI.getBtnConfigs().setEnabled(true);
        }else if (key.equals(UIVE.FILES_BUTTON_NAME)){
            fleDisabled = false;
            appUI.getBtnDataFiles().setEnabled(true);
        }else if (key.equals(UIVE.NEW_FILE_BUTTON_NAME)){
            newFleDisabled = false;
            appUI.getBtnDataFiles().setEnabled(true);
        }
    }

}
