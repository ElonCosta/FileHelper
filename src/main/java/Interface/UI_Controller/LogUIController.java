package Interface.UI_Controller;

import Interface.UI.LogUI;
import Main.Launcher;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class LogUIController extends LogUI {


    public LogUIController(){
        getCmdField().addActionListener(e -> {
            Launcher.LOG.readCommand();
            getCmdField().setText("");
        });
        getCmdField().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_UP){
                    getCmdField().setText(Launcher.LOG.lastIssuedCommand());
                }
                if(e.getKeyCode() == KeyEvent.VK_DOWN){
                    getCmdField().setText(Launcher.LOG.advanceIssuedCommand());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    public void appendLog(String s){
        getLogArea().append(s);
    }

    public String readCmd(){
        return getCmdField().getText();
    }

    void clearLog(){
        getLogArea().setText("");
    }
}
