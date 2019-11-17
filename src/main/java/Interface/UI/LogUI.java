package Interface.UI;

import sun.dc.pr.PRError;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;

public class LogUI extends JPanel {

    private JTextArea logArea;
    private JTextField cmdField;

    protected LogUI(){
        this.setLayout(null);

        cmdField = new JTextField();
        cmdField.setBounds(5,305,515,25);

        this.add(getLogScroll());
        this.add(cmdField);
    }

    private JScrollPane getLogScroll(){
        logArea = new JTextArea();
        logArea.setEditable(false);
        DefaultCaret caret = (DefaultCaret)logArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBounds(5,0,515,300);

        return logScroll;
    }

    public JTextArea getLogArea() {
        return logArea;
    }

    public JTextField getCmdField() {
        return cmdField;
    }

}
