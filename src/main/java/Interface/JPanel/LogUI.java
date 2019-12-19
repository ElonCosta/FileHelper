package Interface.JPanel;

import Main.Launcher;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static Main.Launcher.LOG;

public class LogUI extends AbstractUI {

    private JTextArea logArea;
    private JTextField cmdField;

    public LogUI(){
        this.setLayout(null);

        this.add(createLogScroll());
        this.add(createCmdField());

        initEvents();
    }

    private  JTextField createCmdField(){
        cmdField = new JTextField();
        cmdField.setBounds(5,305,515,25);
        cmdField.setFocusTraversalKeysEnabled(false);

        return cmdField;
    }
    private JScrollPane createLogScroll(){
        logArea = new JTextArea();
        logArea.setEditable(false);
        DefaultCaret caret = (DefaultCaret)logArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBounds(5,0,515,300);

        return logScroll;
    }

    @Override
    void initEvents() {
        cmdField.addActionListener(e -> {
            LOG.readCommand();
            cmdField.setText("");
        });
        cmdField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(isKey(e, KeyEvent.VK_UP)){
                    cmdField.setText(LOG.lastIssuedCommand());
                }
                if(isKey(e, KeyEvent.VK_DOWN)){
                    cmdField.setText(LOG.advanceIssuedCommand());
                }
                if(isKey(e, KeyEvent.VK_TAB)){
                    cmdField.setText(LOG.autoComplete());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(!isKey(e, KeyEvent.VK_TAB)){
                    LOG.setPartialCmd(cmdField.getText());
                }
            }
        });
    }

    public void appendLog(String s){
        logArea.append(s);
    }

    public String readCmd(){
        return cmdField.getText();
    }

    public void clearLog(Integer n){
        if (n == null){
            logArea.setText("");
        }else{
            String[] s = logArea.getText().split("\n");
            StringBuilder log = new StringBuilder();
            if (n > s.length){
                LOG.println("Not enough lines to delete (\""+n+"\")");
                return;
            }
            for (int i = n; i < s.length; i++){
                log.append(s[i]).append("\n");
            }
            logArea.setText(log.toString());
        }
    }

    /*
        Getters && Setters
     */

    public JTextArea getLogArea() {
        return logArea;
    }

    public void setLogArea(JTextArea logArea) {
        this.logArea = logArea;
    }

    public JTextField getCmdField() {
        return cmdField;
    }

    public void setCmdField(JTextField cmdField) {
        this.cmdField = cmdField;
    }
}
