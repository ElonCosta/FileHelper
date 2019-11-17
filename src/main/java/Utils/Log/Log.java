package Utils.Log;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static ArchiveLoader.Configurations.*;
import static Main.Launcher.*;

public class Log {

    private List<String> issuedCommands = new ArrayList<>();

    private Integer issuedCommandsPos = 0;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss > ");
    private Map<String,Command> commands = new HashMap<>();

    public Log(){
        clear();
        newCommand(new Command("clear") {
            @Override
            public void run() {
                clear();
            }
        });
    }

    public void println(Object x){
        println(x, config.getGlobal().getDisplayTime());
    }

    private void println(Object x, boolean y){
        if (y) {
            String ln = sdf.format(new Date()) + (x) + "\n";
            mainUI.appendLog(ln);
        } else {
            mainUI.appendLog(x + "\n");
        }
    }

    public void readCommand(){
        String tmps = mainUI.readCmd();
        Command cmd = commands.get(toRegex(tmps));
        if (cmd != null){
            if(issuedCommands.isEmpty() || !tmps.equals(issuedCommands.get(issuedCommands.size()-1))){
                issuedCommands.add(tmps);
                issuedCommandsPos = issuedCommands.size() - 1;
            }
            if (cmd.hasArgs){
                cmd.setArgs(tmps);
                cmd.run();
                return;
            }else{
                cmd.run();
                return;
            }
        }
        println("Command not found");
    }

    public String lastIssuedCommand(){
        if(!issuedCommands.isEmpty()){
            String cmd = issuedCommands.get(issuedCommandsPos);
            System.out.println(cmd);
            if(issuedCommandsPos > 0){
                issuedCommandsPos--;
            }
            return cmd;
        }else {
            return "";
        }
    }

    public String advanceIssuedCommand(){
        if(issuedCommandsPos < issuedCommands.size()){
            issuedCommandsPos++;
        }
        if(issuedCommandsPos >= issuedCommands.size()){
            issuedCommandsPos--;
            return "";
        }
        System.out.println(issuedCommands.get(issuedCommandsPos));
        return issuedCommands.get(issuedCommandsPos);
    }

    private void clear(){
        mainUI.clearLog();
    }

    public void newCommand(Command... cmds){
        for (Command cmd: cmds){
            newCommand(cmd);
        }
    }

    public void newCommand(Command cmd){
            Command c = commands.get(cmd.getRegexCmd());
            if (c != null) return;
            commands.put(cmd.getRegexCmd(),cmd);
    }

    private String toRegex(String cmd){
        String[] tmpStr = cmd.replaceAll("\\s","").split("(?=-+[a-z](\\[+[\\w]+\\]))");
        StringBuilder tmp = new StringBuilder();
        for (String s: tmpStr){
            if (s.matches("(-+[a-z]+\\[+[\\w]+\\])")){
                s = " (-+[a-z]+\\[+[\\w]+\\])";
            }
            tmp.append(s);
        }
        return tmp.toString();
    }

}
