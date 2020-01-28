package Interface.Controllers;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

import static Main.Launcher.log;

public class LogController implements Initializable {

    @FXML private TextArea logArea;

    @FXML private TextField cmdFld;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logArea.setFocusTraversable(false);
    }

    @FXML private void logOnInteract(){
        cmdFld.requestFocus();
    }

    @FXML private void onKeyPress(KeyEvent e){
        if (e.getCode().equals(KeyCode.TAB)){
            cmdFld.requestFocus();
            cmdFld.setText(log.autoComplete());
            cmdFld.positionCaret(cmdFld.getText().length());
        }else if(e.getCode().equals(KeyCode.UP)){
            cmdFld.requestFocus();
            cmdFld.setText(log.lastIssuedCommand());
            cmdFld.positionCaret(cmdFld.getText().length());
        }else if(e.getCode().equals(KeyCode.DOWN)){
            cmdFld.requestFocus();
            cmdFld.setText(log.advanceIssuedCommand());
            cmdFld.positionCaret(cmdFld.getText().length());
        }
    }

    @FXML private void onKeyRelease(KeyEvent e){
        if(!e.getCode().equals(KeyCode.TAB)){
            log.setPartialCmd(cmdFld.getText());
        }
    }

    @FXML private void readCmd() {
        log.readCommand(cmdFld.getText());
        cmdFld.clear();
    }

    public void clearLog(Integer n) {
        if (n == null){
            logArea.setText("");
        }else{
            String[] s = logArea.getText().split("\n");
            StringBuilder logSb = new StringBuilder();
            if (n > s.length){
                log.println("Not enough lines to delete (\""+n+"\")");
                return;
            }
            for (int i = n; i < s.length; i++){
                logSb.append(s[i]).append("\n");
            }
            logArea.setText(logSb.toString());
        }
    }
    public void appendLog(String ln) {
        logArea.appendText(ln);
    }
}
