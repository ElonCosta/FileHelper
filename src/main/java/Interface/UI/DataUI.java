package Interface.UI;

import ArchiveLoader.FilesArchive;
import Main.Launcher;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.List;
import java.util.Map;

import static Main.Launcher.*;

public class DataUI extends JPanel {

    JTabbedPane dataTabs;

    public DataUI(){
        this.setLayout(null);
        dataTabs = new JTabbedPane();
        dataTabs.setBounds(5,-2,515,258);
        this.add(dataTabs);
    }

    public JTabbedPane getDataTabs() {
        return dataTabs;
    }

    public class DataPanel extends JPanel{

        private FilesArchive data;

        private JLabel nameLbl;
        private JLabel lastModLbl;
        private JCheckBox archiveFilesCheckBox;

        private JTabbedPane pathsTabs;

        public DataPanel(FilesArchive data) {
            this.data = data;
            this.setLayout(null);

            nameLbl = new JLabel("Name: " + data.getName());
            nameLbl.setBounds(5, 0, 300, 25);
            nameLbl.setBackground(Color.CYAN);
            this.add(nameLbl);

            lastModLbl = new JLabel("Last modification: " + data.getLastMod());
            lastModLbl.setBounds(5, 20, 300, 25);
            this.add(lastModLbl);

            final JSeparator separator1 = new JSeparator();
            separator1.setBounds(5, 45, 500, 5);
            this.add(separator1);

            final JLabel archiveFilesLbl = new JLabel("Archive Files: ");
            archiveFilesLbl.setBounds(410,0,300,25);
            this.add(archiveFilesLbl);

            archiveFilesCheckBox = new JCheckBox();
            archiveFilesCheckBox.setSelected(data.getArchiveFiles());
            archiveFilesCheckBox.setBounds(485, 0, 25, 25);
            this.add(archiveFilesCheckBox);

            pathsTabs = new JTabbedPane();
            pathsTabs.setBounds(5,55,500,170);
            this.add(pathsTabs);

            for (FilesArchive.Paths p : data.getPathsList()){
                pathsTabs.add((1+data.getPathsList().indexOf(p)) + "",new pathsPanel(p));
            }
        }

        public FilesArchive getData() {
            return data;
        }

        public JLabel getNameLbl() {
            return nameLbl;
        }

        public JLabel getLastModLbl() {
            return lastModLbl;
        }

        public JCheckBox getArchiveFilesCheckBox() {
            return archiveFilesCheckBox;
        }

        public JTabbedPane getPathsTabs() {
            return pathsTabs;
        }

        public class pathsPanel extends JPanel{

            private FilesArchive.Paths paths;

            private JTextField fileTxtFld;
            private JTextField destTxtFld;

            private JCheckBox disabledChkBox;

            pathsPanel(FilesArchive.Paths paths){
                this.setLayout(null);

                this.paths = paths;

                JLabel fileLbl = new JLabel("File path:");
                fileLbl.setBounds(5,5,300,25);
                this.add(fileLbl);

                fileTxtFld = new JTextField();
                fileTxtFld.setText(config.getGlobal().getShorthandPath(paths.getFile()));
                fileTxtFld.setBounds(5,30,485,25);
                this.add(fileTxtFld);

                JLabel destLbl = new JLabel("Destination path:");
                destLbl.setBounds(5,60,300,25);
                this.add(destLbl);

                destTxtFld = new JTextField();
                destTxtFld.setText(config.getGlobal().getShorthandPath(paths.getDest()));
                destTxtFld.setBounds(5,90,485,25);
                this.add(destTxtFld);

                JLabel disabledLbl = new JLabel("disable path:");
                disabledLbl.setBounds(5,115,300,25);
                this.add(disabledLbl);

                disabledChkBox = new JCheckBox();
                disabledChkBox.setSelected(paths.getDisabled());
                disabledChkBox.setBounds(80,115,25,25);
                this.add(disabledChkBox);

                setEvents();
            }

            private void setEvents(){
                fileTxtFld.addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        fileTxtFld.setText(paths.getFile().getAbsolutePath());
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        paths.setFile(new File(fileTxtFld.getText()));
                        fileTxtFld.setText(config.getGlobal().getShorthandPath(paths.getFile()));
                    }
                });

                destTxtFld.addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        destTxtFld.setText(paths.getDest().getAbsolutePath());
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        paths.setDest(new File(destTxtFld.getText()));
                        destTxtFld.setText(config.getGlobal().getShorthandPath(paths.getDest()));
                    }
                });

                disabledChkBox.addActionListener(e ->{
                    Integer pos = DataPanel.this.pathsTabs.getSelectedIndex();
                    DataPanel.this.getData().disablePath(pos, disabledChkBox.isSelected());
                });
            }
        }
    }
}
