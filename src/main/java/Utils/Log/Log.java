package Utils.Log;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static ArchiveLoader.Configurations.*;
import static Main.Launcher.*;
import static Utils.Reader.split;

public class Log {

    private List<String> issuedCommands = new ArrayList<>();
    private List<String> autoCompleteList = new ArrayList<>();

    private Integer issuedCommandsPos = 0;
    private Integer autoCompletePos = 1;

    private String partialCmd = "";

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

    public void printErr(int Code, String... vals){
        StringBuilder err = new StringBuilder("ERROR CODE("+Code+"): \n") ;
        switch (Code) {
            case 1:
                err.append("Invalid parameter \"").append(vals[0]).append("\"");
            case 2:
                err.append("No parameter value found \"").append(vals[0]).append("\"");
            case 3:
                err.append("Command has no parameters");
            case 4:
                err.append("Missing parameters");
            case 5:
                err.append("Too many parameters for command");
            case 6:
                err.append("Unknown archive \"").append(vals[0]).append("\"");
            case 7:
                err.append("Repeated parameter \"").append(vals[0]).append("\"on command");
            case 8:
                err.append("Invalid parameter type ");
        }
        println(err, false);
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
        String cmdLine = mainUI.readCmd();
        final String[] CMD = split(cmdLine,'-');
        Command cmd = commands.get(CMD[0].trim());
        if (cmd != null){
            if(!cmd.setArgs(CMD)) return;
            cmd.run();
            cmd.flush();
            if(issuedCommands.isEmpty() || !cmdLine.equals(issuedCommands.get(issuedCommands.size()-1))){
                issuedCommands.add(cmdLine);
            }
            issuedCommandsPos = issuedCommands.size();
        }else {
            println("Command not found");
        }
    }

    public String lastIssuedCommand(){
        if(!issuedCommands.isEmpty()){
            if(issuedCommandsPos > 0){
                issuedCommandsPos--;
            }else {
                issuedCommandsPos = 0;
            }
            return issuedCommands.get(issuedCommandsPos);
        }else {
            return "";
        }
    }

    public String advanceIssuedCommand(){
        if(issuedCommandsPos < issuedCommands.size()){
            issuedCommandsPos++;
        }
        if (issuedCommandsPos >= issuedCommands.size()){
            issuedCommandsPos = issuedCommands.size();
            return "";
        }
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
        Command c = commands.get(cmd.getCmd());
        if (c != null) return;
        commands.put(cmd.getCmd(),cmd);
    }

    public String autoComplete(){
        if(!autoCompleteList.isEmpty() && partialCmd.equals(autoCompleteList.get(0))){
            autoCompletePos++;
            if(autoCompletePos == autoCompleteList.size()) autoCompletePos = 1;
            return autoCompleteList.get(autoCompletePos);
        }else{
            autoCompleteList = new ArrayList<>();
            autoCompletePos = 1;
            autoCompleteList.add(partialCmd);
            for (String c: commands.keySet()) {
                if(c.startsWith(partialCmd)){
                    autoCompleteList.add(c);
                }
            }
            if(autoCompleteList.size() == 1){
                return partialCmd;
            }
            return autoCompleteList.get(autoCompletePos);
        }
    }

    public void setPartialCmd(String partialCmd){
        this.partialCmd = partialCmd;
    }

    public void spitCommands(){
        for (String s:
             commands.keySet()) {
            System.out.println(s);
        }
    }
}
