package Interface.UI;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static Main.Launcher.*;

@SuppressWarnings("Duplicates")
public class NewFileUI extends JPanel {

    private JTextField nameTxtField;

    private JTabbedPane pathsTabs;

    protected NewFileUI(){
        this.setLayout(null);

        int x = 5;
        int y = 0;

        JPanel pnl = new JPanel();
        pnl.setLayout(null);
        pnl.setBorder(BorderFactory.createLineBorder(Color.gray));
        pnl.setBounds(5,0,515,241);

        JLabel nameLbl = new JLabel("Name: ");
        nameLbl.setBounds(x,y,300,25);
        pnl.add(nameLbl);

        y += nameLbl.getSize().height;

        nameTxtField = new JTextField();
        nameTxtField.setBounds(x,y,505,25);
        pnl.add(nameTxtField);

        y+= nameTxtField.getSize().height + 10;

        JSeparator separator = new JSeparator();
        separator.setBounds(x,y,505,5);
        pnl.add(separator);

        y += separator.getSize().height;

        pathsTabs = new JTabbedPane();
        pathsTabs.setBounds(x,y,505,170);
        pathsTabs.addTab("1",null, new pathsPanel(),null);
        pathsTabs.addTab("+", new JPanel());
        pnl.add(pathsTabs);

        this.add(pnl);
    }

    protected void newPanel(){
        int pos = pathsTabs.getTabCount();
        pathsPanel panel = new pathsPanel();
        pathsTabs.insertTab(pos+"",null, panel,null,pos-1);
        pathsTabs.setSelectedComponent(panel);
    }

    public JTextField getNameTxtField() {
        return nameTxtField;
    }

    public JTabbedPane getPathsTabs() {
        return pathsTabs;
    }

    private static class pathsPanel extends JPanel{

        private JTextField fleTxtField;
        private JButton fleBtn;
        private JFileChooser fileChooser;

        private JTextField dstTxtField;
        private JButton dstBtn;
        private JFileChooser destChooser;

        pathsPanel(){
            this.setLayout(null);

            int y = 5;
            int x = 5;

            JLabel fleLbl = new JLabel("File: ");
            fleLbl.setBounds(x,y,300,25);
            this.add(fleLbl);

            y += fleLbl.getSize().height;

            fleTxtField = new JTextField();
            fleTxtField.setBounds(x,y,490-25,25);
            this.add(fleTxtField);

            fleBtn = new JButton("...");
            fleBtn.setBounds(x+fleTxtField.getSize().width,y, 24,24);
            this.add(fleBtn);

            fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

            y += fleBtn.getSize().height;

            JLabel dstLbl = new JLabel("Dest: ");
            dstLbl.setBounds(x, y,300,25);
            this.add(dstLbl);

            y += dstLbl.getSize().height;

            dstTxtField = new JTextField();
            dstTxtField.setBounds(x,y,490-25,25);
            this.add(dstTxtField);

            dstBtn = new JButton("...");
            dstBtn.setBounds(x+dstTxtField.getSize().width,y,24,24);
            this.add(dstBtn);

            destChooser = new JFileChooser();
            destChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            destChooser.setSelectedFile(config.getGlobal().getVersionFolder());
            dstTxtField.setText(destChooser.getSelectedFile().getAbsolutePath());

            initEvents();
        }

        void initEvents() {
            fleBtn.addActionListener(e -> {
                int i = fileChooser.showOpenDialog(new JFrame());
                if(i == JFileChooser.APPROVE_OPTION){
                    File f = fileChooser.getSelectedFile();
                    fleTxtField.setText(f.getAbsolutePath());
                }
            });

            dstBtn.addActionListener(e -> {
                int i = destChooser.showOpenDialog(new JFrame());
                if(i == JFileChooser.APPROVE_OPTION){
                    File f = destChooser.getSelectedFile();
                    dstTxtField.setText(f.getAbsolutePath());
                }
            });
        }

        public JTextField getFleTxtField() {
            return fleTxtField;
        }

        public JButton getFleBtn() {
            return fleBtn;
        }

        public JFileChooser getFileChooser() {
            return fileChooser;
        }

        public JTextField getDstTxtField() {
            return dstTxtField;
        }

        public JButton getDstBtn() {
            return dstBtn;
        }

        public JFileChooser getDestChooser() {
            return destChooser;
        }
    }
}
