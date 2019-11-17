package Interface.UI;

import javax.swing.*;

import java.awt.*;

import static Main.Launcher.config;

public class ConfigsUI extends JPanel {

    private JTextField fieldVersionFolder;
    private JTextField fieldArchiveFolder;
    private JTextField fieldRootFolder;

    private JSpinner spinnerRoutineTime;

    private JCheckBox checkBoxDisplayTime;
    private JCheckBox checkBoxArchiveFiles;

    protected ConfigsUI(){
        this.setLayout(null);

        JPanel pnl = new JPanel();
        pnl.setLayout(null);
        pnl.setBorder(BorderFactory.createLineBorder(Color.gray));
        pnl.setBounds(5,0,515,241);
        int y = 0;

        JLabel rootLbl = new JLabel("\"Root\" Folder:");
        rootLbl.setBounds(5,y,300, 25);
        pnl.add(rootLbl);

        y += 25;

        fieldRootFolder = new JTextField();
        fieldRootFolder.setText(config.getGlobal().getShorthandPath(config.getGlobal().getRootFolder()));
        fieldRootFolder.setBounds(5,y,505, 25);
        pnl.add(fieldRootFolder);

        y += 30;

        JLabel archiveLbl = new JLabel("\"Archive\" Folder:");
        archiveLbl.setBounds(5,y,300, 25);
        pnl.add(archiveLbl);

        y += 25;

        fieldArchiveFolder = new JTextField();
        fieldArchiveFolder.setText(config.getGlobal().getShorthandPath(config.getGlobal().getArchiveFolder()));
        fieldArchiveFolder.setBounds(5,y,505, 25);
        pnl.add(fieldArchiveFolder);

        y += 30;

        JLabel versionLbl = new JLabel("\"Latest\" Folder:");
        versionLbl.setBounds(5,y,300, 25);
        pnl.add(versionLbl);

        y += 25;

        fieldVersionFolder = new JTextField();
        fieldVersionFolder.setText(config.getGlobal().getShorthandPath(config.getGlobal().getVersionFolder()));
        fieldVersionFolder.setBounds(5,y,505,25);
        pnl.add(fieldVersionFolder);
        y += 30;

        JSeparator separator = new JSeparator();
        separator.setBounds(5,y, 505, 5);
        pnl.add(separator);
        y += 10;

        JLabel routineTimeLbl = new JLabel("Routine time:");
        routineTimeLbl.setBounds(5,y,300,25);
        pnl.add(routineTimeLbl);

        SpinnerModel model = new SpinnerNumberModel(config.getGlobal().getRoutineTime(), 1,null,1);
        spinnerRoutineTime = new JSpinner(model);
        spinnerRoutineTime.setBounds(85,y,35,25);
        pnl.add(spinnerRoutineTime);
        y += 30;

        JSeparator separator2 = new JSeparator();
        separator2.setBounds(5,y, 505, 5);
        pnl.add(separator2);
        y += 10;

        JLabel displayTimeLbl = new JLabel("Display Time:");
        displayTimeLbl.setBounds(5, y, 300, 25);
        pnl.add(displayTimeLbl);

        checkBoxDisplayTime = new JCheckBox();
        checkBoxDisplayTime.setSelected(config.getGlobal().getDisplayTime());
        checkBoxDisplayTime.setBounds(80, y, 25,25);
        pnl.add(checkBoxDisplayTime);

        JLabel archiveFilesLbl = new JLabel("Archive files:");
        archiveFilesLbl.setBounds(110, y, 300, 25);
        pnl.add(archiveFilesLbl);

        checkBoxArchiveFiles = new JCheckBox();
        checkBoxArchiveFiles.setSelected(config.getGlobal().getArchiveFiles());
        checkBoxArchiveFiles.setBounds(183, y, 25,25);
        pnl.add(checkBoxArchiveFiles);

        this.add(pnl);
    }

    public JTextField getFieldVersionFolder() {
        return fieldVersionFolder;
    }

    public JTextField getFieldArchiveFolder() {
        return fieldArchiveFolder;
    }

    public JTextField getFieldRootFolder() {
        return fieldRootFolder;
    }

    public JSpinner getSpinnerRoutineTime() {
        return spinnerRoutineTime;
    }

    public JCheckBox getCheckBoxDisplayTime() {
        return checkBoxDisplayTime;
    }

    public JCheckBox getCheckBoxArchiveFiles() {
        return checkBoxArchiveFiles;
    }
}
