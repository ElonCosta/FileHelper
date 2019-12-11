package Interface.JFrame;

import ArchiveLoader.FilesArchive;
import Interface.JPanel.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import Utils.Constants.*;

import static Main.Launcher.*;

public class AppUI extends JFrame {

    private JPanel container;

    private JButton btnTerminal;
    private JButton btnConfigs;
    private JButton btnDataFiles;
    private JButton btnNewFile;

    private Boolean logDisabled = false;
    private Boolean cfgDisabled = false;
    private Boolean fleDisabled = false;
    private Boolean newFleDisabled = false;

    public AppUI(){

        this.setTitle(UIVE.TITLE.getVar());
        this.pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(530,410);
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        this.setResizable(false);
        this.getContentPane().setLayout(null);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                config.save();
            }
        });

        this.getContentPane().add(getJToolBar());
        this.getContentPane().add(getNavigationContainer());

        this.setVisible(true);
        
        initEvents();

        navigation(UIVE.LOG_BUTTON_NAME);
    }

    private JToolBar getJToolBar(){

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.setMargin(new Insets(1,3,0,0));
        toolBar.setBounds(0,0,530,40);


        btnTerminal = new JButton();
        btnTerminal.setFocusable(false);
        btnTerminal.setText(UIVE.LOG_BUTTON_NAME.getVar());
        toolBar.add(btnTerminal);

        btnConfigs = new JButton();
        btnConfigs.setFocusable(false);
        btnConfigs.setText(UIVE.CONFIG_BUTTON_NAME.getVar());
        toolBar.add(btnConfigs);

        btnDataFiles = new JButton();
        btnDataFiles.setFocusable(false);
        btnDataFiles.setText(UIVE.FILES_BUTTON_NAME.getVar());
        toolBar.add(btnDataFiles);

        btnNewFile = new JButton();
        btnNewFile.setFocusable(false);
        btnNewFile.setText(UIVE.NEW_FILE_BUTTON_NAME.getVar());
        toolBar.add(btnNewFile);

        return toolBar;
    }

    private JPanel getNavigationContainer(){

        container = new JPanel();
        container.setBounds(0,45,530,380);
        container.setLayout(new CardLayout());

        container.add(new LogUI(), UIVE.LOG_BUTTON_NAME.getVar());
        container.add(new ConfigsUI(),UIVE.CONFIG_BUTTON_NAME.getVar());
        container.add(new DataUI(), UIVE.FILES_BUTTON_NAME.getVar());
        container.add(new NewFileUI(), UIVE.NEW_FILE_BUTTON_NAME.getVar());

        return container;
    }

    private void initEvents(){
        btnConfigs.addActionListener(e -> navigation(UIVE.CONFIG_BUTTON_NAME));
        btnDataFiles.addActionListener(e -> navigation(UIVE.FILES_BUTTON_NAME));
        btnTerminal.addActionListener(e -> navigation(UIVE.LOG_BUTTON_NAME));
        btnNewFile.addActionListener(e -> navigation(UIVE.NEW_FILE_BUTTON_NAME));
    }

    private void navigation(UIVE key){

        CardLayout card = (CardLayout) container.getLayout();
        card.show(container, key.getVar());

        btnConfigs.setEnabled(!cfgDisabled);
        btnDataFiles.setEnabled(!fleDisabled);
        btnTerminal.setEnabled(!logDisabled);
        btnNewFile.setEnabled(!newFleDisabled);

        if (key.equals(UIVE.LOG_BUTTON_NAME)){
            this.setBounds(this.getX(), this.getY(), 530, 409);
            btnTerminal.setEnabled(false);
            LogUI t = (LogUI) container.getComponent(0);
            t.getCmdField().requestFocus();
        }else if (key.equals(UIVE.CONFIG_BUTTON_NAME)){
            this.setBounds(this.getX(), this.getY(), 530, 320);
            btnConfigs.setEnabled(false);
        }else if (key.equals(UIVE.FILES_BUTTON_NAME)){
            this.setBounds(this.getX(), this.getY(), 530, 335);
            btnDataFiles.setEnabled(false);
        }else if (key.equals(UIVE.NEW_FILE_BUTTON_NAME)){
            this.setBounds(this.getX(), this.getY(), 530, 320);
            btnNewFile.setEnabled(false);
        }

    }

    public void newFile(){
        navigation(UIVE.CONFIG_BUTTON_NAME);
        container.remove(3);
        container.add(new NewFileUI(), UIVE.NEW_FILE_BUTTON_NAME.getVar());
        navigation(UIVE.NEW_FILE_BUTTON_NAME);
    }

    public String readCmd(){
        return ((LogUI) container.getComponent(0)).readCmd();
    }

    public void appendLog(String s){
        ((LogUI) container.getComponent(0)).appendLog(s);
    }

    public void clearLog(){((LogUI) container.getComponent(0)).clearLog();}

    public void updateInterface(){
        ((ConfigsUI) container.getComponent(1)).updateInterface();
    }

    public void createTabs(Map<String, FilesArchive> m){
        ((DataUI) container.getComponent(2)).createTabs(m);
    }

    public void disableButton(UIVE key){
        if (key.equals(UIVE.LOG_BUTTON_NAME)){
            logDisabled = true;
            btnTerminal.setEnabled(false);
        }else if (key.equals(UIVE.CONFIG_BUTTON_NAME)){
            cfgDisabled = true;
            btnConfigs.setEnabled(false);
        }else if (key.equals(UIVE.FILES_BUTTON_NAME)){
            fleDisabled = true;
            btnDataFiles.setEnabled(false);
        }else if (key.equals(UIVE.NEW_FILE_BUTTON_NAME)){
            newFleDisabled = true;
            btnDataFiles.setEnabled(false);
        }
    }

    public void enableButton(UIVE key){
        if (key.equals(UIVE.LOG_BUTTON_NAME)){
            logDisabled = false;
            btnTerminal.setEnabled(true);
        }else if (key.equals(UIVE.CONFIG_BUTTON_NAME)){
            cfgDisabled = false;
            btnConfigs.setEnabled(true);
        }else if (key.equals(UIVE.FILES_BUTTON_NAME)){
            fleDisabled = false;
            btnDataFiles.setEnabled(true);
        }else if (key.equals(UIVE.NEW_FILE_BUTTON_NAME)){
            newFleDisabled = false;
            btnDataFiles.setEnabled(true);
        }
    }
}
