package Interface.JPanel;

import Utils.Constants;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static Utils.Constants.*;

import static Main.Launcher.*;

@SuppressWarnings("Duplicates")
public class NewFileUI extends AbstractUI {

    private JTextField nameTxtField;

    private JTabbedPane pathsTabs;

    private JButton saveBtn;
    private JButton removePathBtn;

    public NewFileUI(){
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
        nameTxtField.setBounds(5,25,505,25);
        pnl.add(nameTxtField);

        y+= nameTxtField.getSize().height + 10;

        JSeparator separator = new JSeparator();
        separator.setBounds(x,y,505,5);
        pnl.add(separator);

        y += separator.getSize().height;

        pathsTabs = new JTabbedPane();
        pathsTabs.setBounds(5,65,505,140);
        pathsTabs.addTab("1",null, new PathsPanel(0, "1"),null);
        pathsTabs.addTab("+", new PathsPanel(Integer.MAX_VALUE, "+"));
        pnl.add(pathsTabs);

        y += pathsTabs.getSize().height + 5;

        removePathBtn = new JButton("Remove Path");
        removePathBtn.setBounds(x+5,y,125,25);
        removePathBtn.setEnabled(false);
        this.add(removePathBtn);

        saveBtn = new JButton("Create");
        saveBtn.setBounds(515-125, y, 125, 25);
        this.add(saveBtn);

        this.add(pnl);

        initEvents();
    }

    protected void newPanel(){
        int pos = pathsTabs.getTabCount();
        PathsPanel panel = new PathsPanel(pos - 1, pos + "");
        pathsTabs.insertTab(pos+"",null, panel,null,pos-1);
        pathsTabs.setSelectedComponent(panel);
    }

    private void rearrangeTabs(){
        int pos = 0;
        for (Component c: pathsTabs.getComponents()){
            if (c instanceof PathsPanel){
                PathsPanel p = (PathsPanel) c;
                if(p.getPos() < Integer.MAX_VALUE){
                    p.pos = pos;
                    p.name = (pos+1)+"";
                    pathsTabs.setTitleAt(pos, (pos+1)+"");
                    pos++;
                }
            }
        }
        if(((PathsPanel) pathsTabs.getComponents()[0]).name.equals("+")){
            pathsTabs.remove(pathsTabs.getComponents()[0]);
            pathsTabs.addTab("+", new PathsPanel(Integer.MAX_VALUE, "+"));
        }
    }

    private void disposePathsPanel(int pos){
        if (pos == 0){
            pathsTabs.setSelectedIndex(pos+1);
        }else {
            pathsTabs.setSelectedIndex(pos-1);
        }
        pathsTabs.removeTabAt(pos);
        rearrangeTabs();
        if(pathsTabs.getTabCount() == 2){
            removePathBtn.setEnabled(false);
        }
    }

    private void save(){
        try{
            File dataPath = new File(config.getGlobal().getRootFolder().getAbsolutePath()+"/"+nameTxtField.getText()+"_Data.json");
            JSONObject dataFile = generateDataFile();

            FileWriter fw = new FileWriter(dataPath);
            fw.write(dataFile.toString(4));
            fw.close();

            config.getDataFiles().newDataFile(nameTxtField.getText());
            config.save();
            loader.checkForFiles();

            JOptionPane.showMessageDialog(this,"Archive created successfully!" );
            mainUI.newFile();
        }catch (Exception e){
            JOptionPane.showMessageDialog(this,e);
        }
    }

    private JSONObject generateDataFile() throws Exception{
        JSONObject dataFile = new JSONObject();
        JSONArray paths = generatePaths();
        if(nameTxtField.getText().trim().equals("")){
            this.nameTxtField.requestFocus();
            throw new Exception("Please fill the name field");
        }

        put(dataFile, KEY.ARCHIVE_FILE, false);
        put(dataFile, KEY.NAME, nameTxtField.getText());
        put(dataFile, KEY.PATHS, paths);
        put(dataFile, KEY.LAST_MOD, new SimpleDateFormat("yyyy/MM/dd hh:mm").format(new Date()));

        return dataFile;
    }

    private JSONArray generatePaths() throws Exception{
        JSONArray jar;
        List<JSONObject> pathsList = new ArrayList<>();
        for (Component c: pathsTabs.getComponents()){
            JSONObject jo = new JSONObject();
            PathsPanel p = (PathsPanel) c;

            if (p.getPos() < Integer.MAX_VALUE){
                if (p.getDstTxtField().getText().trim().equals("")){
                    pathsTabs.setSelectedComponent(c);
                    p.getDstTxtField().requestFocus();
                    throw new Exception("Please select a destination folder");
                }

                if (p.getFleTxtField().getText().trim().equals("")){
                    pathsTabs.setSelectedComponent(c);
                    p.getFleTxtField().requestFocus();
                    throw new Exception("Please select a file");
                }

                File file = new File(p.getFleTxtField().getText());
                if (!file.exists()){
                    pathsTabs.setSelectedComponent(c);
                    p.getFleTxtField().requestFocus();
                    throw new Exception("Please select an existing file");
                }

                File dest = new File(p.getDstTxtField().getText());
                if (!dest.exists() || !dest.isDirectory()){
                    pathsTabs.setSelectedComponent(c);
                    p.getDstTxtField().requestFocus();
                    throw new Exception("Please select an existing folder");
                }
                put(jo, KEY.PATH, file.getAbsolutePath());
                put(jo, KEY.DEST, dest.getAbsolutePath());
                put(jo, KEY.DISABLED, false);

                pathsList.add(jo);
            }
        }
        jar = new JSONArray(pathsList);

        return jar;
    }

    @Override
    void initEvents() {
        pathsTabs.addChangeListener(new ChangeListener() {
            boolean ignore = false;

            @Override
            public void stateChanged(ChangeEvent e) {
                if(!ignore){
                    ignore = true;
                    try{
                        int selected = pathsTabs.getSelectedIndex();
                        String title = pathsTabs.getTitleAt(selected);
                        if (title.equals("+")){
                            newPanel();
                            removePathBtn.setEnabled(true);
                        }
                    }finally {
                        ignore = false;
                    }
                }
            }
        });

        if(removePathBtn != null){
            removePathBtn.addActionListener(e -> {
                disposePathsPanel(pathsTabs.getSelectedIndex());
            });
        }

        saveBtn.addActionListener(e->{
            save();
        });
    }

    private static class PathsPanel extends AbstractUI{

        private JTextField fleTxtField;
        private JButton fleBtn;
        private JFileChooser fileChooser;

        private JTextField dstTxtField;
        private JButton dstBtn;
        private JFileChooser destChooser;

        private JButton removePath;

        private int pos;
        private String name;

        PathsPanel(int pos, String name){
            this.pos = pos;
            this.name = name;

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

        @Override
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

        JTextField getFleTxtField() {
            return fleTxtField;
        }

        JButton getFleBtn() {
            return fleBtn;
        }

        JFileChooser getFileChooser() {
            return fileChooser;
        }

        JTextField getDstTxtField() {
            return dstTxtField;
        }

        JButton getDstBtn() {
            return dstBtn;
        }

        JFileChooser getDestChooser() {
            return destChooser;
        }

        int getPos(){
            return pos;
        }

        private void setPos(int pos){
            this.pos = pos;
        }

        private void setRemovePathTo(boolean b){
            if(pos < Integer.MAX_VALUE){
                removePath.setEnabled(b);
            }
        }
    }
}
