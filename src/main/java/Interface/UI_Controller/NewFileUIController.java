package Interface.UI_Controller;

import Interface.UI.NewFileUI;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.File;

public class NewFileUIController extends NewFileUI {

    public NewFileUIController(){
        initEvents();
    }

    private void initEvents(){
        getPathsTabs().addChangeListener(new ChangeListener() {
            boolean ignore = false;

            @Override
            public void stateChanged(ChangeEvent e) {
                if(!ignore){
                    ignore = true;
                    try{
                        int selected = getPathsTabs().getSelectedIndex();
                        String title = getPathsTabs().getTitleAt(selected);
                        if (title.equals("+")){
                            newPanel();
                        }
                    }finally {
                        ignore = false;
                    }
                }
            }
        });
    }
}
