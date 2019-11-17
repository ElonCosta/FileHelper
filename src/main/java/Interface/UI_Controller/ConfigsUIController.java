package Interface.UI_Controller;

import Interface.UI.ConfigsUI;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;

import static Main.Launcher.*;

public class ConfigsUIController extends ConfigsUI {

    public ConfigsUIController(){
        getFieldRootFolder().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                getFieldRootFolder().setText(config.getGlobal().getRootFolder().getAbsolutePath());
            }

            @Override
            public void focusLost(FocusEvent e) {
                config.getGlobal().setRootFolder(new File(getFieldRootFolder().getText()));
                getFieldRootFolder().setText(config.getGlobal().getShorthandPath(config.getGlobal().getRootFolder()));
            }
        });
        getFieldArchiveFolder().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                getFieldArchiveFolder().setText(config.getGlobal().getArchiveFolder().getAbsolutePath());
            }

            @Override
            public void focusLost(FocusEvent e) {
                config.getGlobal().setArchiveFolder(new File(getFieldArchiveFolder().getText()));
                getFieldArchiveFolder().setText(config.getGlobal().getShorthandPath(config.getGlobal().getArchiveFolder()));
            }
        });
        getFieldVersionFolder().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                getFieldVersionFolder().setText(config.getGlobal().getVersionFolder().getAbsolutePath());
            }

            @Override
            public void focusLost(FocusEvent e) {
                config.getGlobal().setVersionFolder(new File(getFieldVersionFolder().getText()));
                getFieldVersionFolder().setText(config.getGlobal().getShorthandPath(config.getGlobal().getVersionFolder()));
            }
        });

        getSpinnerRoutineTime().addChangeListener( e -> {
            if(config.getGlobal().getRoutineTime() != getSpinnerRoutineTime().getValue()){
                config.getGlobal().setRoutineTime((Integer) getSpinnerRoutineTime().getValue());
            }
        });

        getCheckBoxArchiveFiles().addActionListener(e -> config.getGlobal().setArchiveFiles(getCheckBoxArchiveFiles().isSelected()));
        getCheckBoxDisplayTime().addActionListener(e -> config.getGlobal().setDisplayTime(getCheckBoxDisplayTime().isSelected()));
    }

    public void updateInterface(){
        getSpinnerRoutineTime().setValue(config.getGlobal().getRoutineTime());

        getCheckBoxArchiveFiles().setSelected(config.getGlobal().getArchiveFiles());
        getCheckBoxDisplayTime().setSelected(config.getGlobal().getDisplayTime());
    }
}
