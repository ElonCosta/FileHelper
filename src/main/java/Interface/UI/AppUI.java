package Interface.UI;

import Interface.UI_Controller.ConfigsUIController;
import Interface.UI_Controller.DataUIController;
import Interface.UI_Controller.LogUIController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import Interface.UI_Controller.NewFileUIController;
import Utils.Constants.*;

import static Main.Launcher.*;

public class AppUI extends JFrame {

    private JPanel container;

    private JButton btnTerminal;
    private JButton btnConfigs;
    private JButton btnDataFiles;
    private JButton btnNewFile;

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

        container.add(new LogUIController(), UIVE.LOG_BUTTON_NAME.getVar());
        container.add(new ConfigsUIController(),UIVE.CONFIG_BUTTON_NAME.getVar());
        container.add(new DataUIController(), UIVE.FILES_BUTTON_NAME.getVar());
        container.add(new NewFileUIController(), UIVE.NEW_FILE_BUTTON_NAME.getVar());

        return container;
    }

    public JPanel getContainer() {
        return container;
    }

    public JButton getBtnLog() {
        return btnTerminal;
    }

    public JButton getBtnConfigs() {
        return btnConfigs;
    }

    public JButton getBtnDataFiles() {
        return btnDataFiles;
    }

    public JButton getBtnNewFile() {
        return btnNewFile;
    }
}
