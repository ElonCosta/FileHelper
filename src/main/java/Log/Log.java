package Log;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

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
        newCommand(new Command("clear","n") {
            @Override
            public void run() {
                if (argsLoad){
                    Integer i = getArg("n").getAsInteger();
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
            app.appendLog(ln);
        } else {
            app.appendLog(x + "\n");
        }
    }

    public void readCommand(String cmdLine){
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

    public String undoCommand(){
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

    public String redoCommand(){
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
        app.clearLog(n);
    }

    public void newCommand(Command... cmds){
        Arrays.stream(cmds).filter(c -> commands.get(c.getCmd()) == null).forEach(c -> commands.put(c.getCmd(), c));
    }

    public String autoComplete(){
        if(!autoCompleteList.isEmpty() && partialCmd.equals(autoCompleteList.get(0)) && autoCompleteList.size() > 1){
            autoCompletePos++;
            if(autoCompletePos >= autoCompleteList.size()) autoCompletePos = 1;
        }else{
            autoCompleteList = new ArrayList<>();
            autoCompletePos = 1;
            autoCompleteList.add(partialCmd);
            commands.keySet().stream().filter(c -> c.startsWith(partialCmd.toLowerCase())).forEach(c -> autoCompleteList.add(c));
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
        commands.keySet().forEach(System.out::println);
    }
}
