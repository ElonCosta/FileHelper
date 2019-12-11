package Interface.JPanel;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public abstract class AbstractUI extends JPanel {

    abstract void initEvents();

    boolean isKey(KeyEvent e, int key){
        return e.getKeyCode() == key;
    }

    boolean isMouseClick(MouseEvent e, int key){
        return e.getButton() == key;
    }
}
