package Interface.UI_Controller;

import Interface.UI.NewFileUI;

import javax.swing.*;
import java.io.File;

public class NewFileUIController extends NewFileUI {

    public NewFileUIController(){
        initEvents();
    }



    public void initEvents() {
        getFleBtn().addActionListener(e -> {
            int i = getFileChooser().showOpenDialog(new JFrame());
            if(i == JFileChooser.APPROVE_OPTION){
                File f = getFileChooser().getSelectedFile();
                getFleTxtField().setText(f.getAbsolutePath());
            }
        });

        getDstBtn().addActionListener(e -> {
            int i = getDestChooser().showOpenDialog(new JFrame());
            if(i == JFileChooser.APPROVE_OPTION){
                File f = getDestChooser().getSelectedFile();
                getDstTxtField().setText(f.getAbsolutePath());
            }
        });
    }
}
