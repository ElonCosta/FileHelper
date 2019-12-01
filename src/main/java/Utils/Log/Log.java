package Utils.Log;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static ArchiveLoader.Configurations.*;
import static Main.Launcher.*;
import static Utils.Reader.split;

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

    public void printErr(int Code, String... vals){
        String err = "ERROR CODE("+Code+"): \n";
        switch (Code) {
            case 1:
                err += ("Invalid parameter");
            case 2:
                err += ("No parameter value found");
            case 3:
                err += ("Command has no parameters");
            case 4:
                err += ("Missing parameters");
            case 5:
                err += ("Too many parameters for command");
            case 6:
                err += ("Unknown archive \"" + vals[0] + "\"");
            case 7:
                err += ("Repeated parameter on command");
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
            int cmdStat = cmd.setArgs(CMD);
            if(cmdStat != 0){
                printErr(cmdStat);
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
            String cmd = issuedCommands.get(issuedCommandsPos);
            return cmd;
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
        String cmd = issuedCommands.get(issuedCommandsPos);
        return cmd;
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

}
