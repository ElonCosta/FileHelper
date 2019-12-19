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
        clear(null);
        newCommand(new Command("clear","i") {
            @Override
            public void run() {
                if (argsLoad){
                    Integer i = getArg("i").getAsInteger();
                    clear(i);
                }else {
                    clear(null);
                }
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
        String cmdLine = mainUI.readCmd();
        final String[] CMD = split(cmdLine,'-');
        Command cmd = commands.get(CMD[0].trim());
        if (cmd != null){
            Map<Boolean, String> argsResponse = cmd.setArgs(CMD);
            if(argsResponse.get(true) == null) {
                this.println(argsResponse.get(false));
                return;
            }
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

    private void clear(Integer n){
        mainUI.clearLog(n);
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
        if(!autoCompleteList.isEmpty() && partialCmd.equals(autoCompleteList.get(0)) && autoCompleteList.size() > 1){
            autoCompletePos++;
            if(autoCompletePos >= autoCompleteList.size()) autoCompletePos = 1;
        }else{
            autoCompleteList = new ArrayList<>();
            autoCompletePos = 1;
            autoCompleteList.add(partialCmd);
            for (String c: commands.keySet()) {
                if(c.startsWith(partialCmd.toLowerCase())){
                    autoCompleteList.add(c);
                }
            }
            if(autoCompleteList.size() == 1){
                return partialCmd;
            }
        }
        return autoCompleteList.get(autoCompletePos);
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
