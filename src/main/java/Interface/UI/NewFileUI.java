package Interface.UI;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static Main.Launcher.*;

public class NewFileUI extends JPanel {

    private JTextField fleTxtField;
    private JButton fleBtn;
    private JFileChooser fileChooser;

    private JTextField dstTxtField;
    private JButton dstBtn;
    private JFileChooser destChooser;

    private JTextField nameTxtField;

    public NewFileUI(){
        this.setLayout(null);

        int x = 5;
        int y = 0;

        JPanel pnl = new JPanel();
        pnl.setLayout(null);
        pnl.setBorder(BorderFactory.createLineBorder(Color.gray));
        pnl.setBounds(5,0,515,241);

        JLabel nameLbl = new JLabel("Name: ");
        nameLbl.setBounds(x,y,300,25);
        pnl.add(nameLbl);

        y += nameLbl.getSize().height;

        nameTxtField = new JTextField();
        nameTxtField.setBounds(x,y,505,25);
        pnl.add(nameTxtField);

        y+= nameTxtField.getSize().height + 10;

        JSeparator separator = new JSeparator();
        separator.setBounds(x,y,505,5);
        pnl.add(separator);

        y += separator.getSize().height;

        JLabel fleLbl = new JLabel("File: ");
        fleLbl.setBounds(x,y,300,25);
        pnl.add(fleLbl);

        y += fleLbl.getSize().height;

        fleTxtField = new JTextField();
        fleTxtField.setBounds(x,y,505-25,25);
        pnl.add(fleTxtField);

        fleBtn = new JButton("...");
        fleBtn.setBounds(x+fleTxtField.getSize().width,y, 24,24);
        pnl.add(fleBtn);

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        y += fleBtn.getSize().height;

        JLabel dstLbl = new JLabel("Dest: ");
        dstLbl.setBounds(x, y,300,25);
        pnl.add(dstLbl);

        y += dstLbl.getSize().height;

        dstTxtField = new JTextField();
        dstTxtField.setBounds(x,y,505-25,25);
        pnl.add(dstTxtField);

        dstBtn = new JButton("...");
        dstBtn.setBounds(x+dstTxtField.getSize().width,y,24,24);
        pnl.add(dstBtn);

        destChooser = new JFileChooser();
        destChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        destChooser.setSelectedFile(config.getGlobal().getVersionFolder());
        dstTxtField.setText(destChooser.getSelectedFile().getAbsolutePath());

        this.add(pnl);
    }

    public JTextField getFleTxtField() {
        return fleTxtField;
    }

    public JButton getFleBtn() {
        return fleBtn;
    }

    public JFileChooser getFileChooser() {
        return fileChooser;
    }

    public JTextField getDstTxtField() {
        return dstTxtField;
    }

    public JButton getDstBtn() {
        return dstBtn;
    }

    public JFileChooser getDestChooser() {
        return destChooser;
    }

    public JTextField getNameTxtField() {
        return nameTxtField;
    }
}
