package Interface.Controllers;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.BufferedReader;
import java.io.StringReader;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
            cmdFld.setText(log.undoCommand());
            cmdFld.positionCaret(cmdFld.getText().length());
        }else if(e.getCode().equals(KeyCode.DOWN)){
            cmdFld.requestFocus();
            cmdFld.setText(log.redoCommand());
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
            logArea.clear();
        }else{
            logArea.clear();
            List<String> s = (new BufferedReader(new StringReader(logArea.getText()))).lines().collect(Collectors.toList());
            StringBuilder logSb = new StringBuilder();
            if (n > s.size()){
                log.println("Not enough lines to delete (\""+n+"\")");
                return;
            }
            s.forEach(i -> logSb.append(i).append("\n"));
            for (int i = n; i < s.size(); i++){
                logSb.append(s.get(i)).append("\n");
            }
            logArea.setText(logSb.toString());
        }
    }
    public void appendLog(String ln) {
        logArea.appendText(ln);
    }
}
