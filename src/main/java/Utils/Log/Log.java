package Utils.Log;

import Utils.TextReader;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ArchiveLoader.Loader.glbCfg;

public class Log {

    private static File log = new File("./logFile.txt");
    private TextReader logLines;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss > ");
    private List<Command> commands = new ArrayList<>();

    public Log(){
        try{
            clear();
            newCommand(new Command("clear","-n[I]") {
                @Override
                public void run() {
                    Integer lines = getArg("-n").getAsInteger();
                    try{
                        clear(lines);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            });
            newCommand(new Command("clear") {
                @Override
                public void run() {
                    try{
                        clear();
                        println(">",false);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            });
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void println(Object x){
        println(x, glbCfg.getDisplayTime());
    }

    public void println(Object x, boolean y){
        try{
            if (y) {
                String ln = sdf.format(new Date()) + (x) + "\n>";
                clearLine();
                FileWriter fw = new FileWriter(log, true);
                fw.write(ln);
                fw.close();
            } else {
                clearLine();
                FileWriter fw = new FileWriter(log, true);
                fw.write(x.toString() + "\n>");
                fw.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void readCommand() throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(log));
        List<String> tmp = new ArrayList<>();
        while (br.ready()){
            tmp.add(br.readLine());
        }
        if (tmp.size() > 0){
            if (tmp.get(tmp.size() - 1) != null && tmp.get(tmp.size() - 1).startsWith(">>")){
                String tmps = tmp.get(tmp.size() - 1).substring(2);
                for (Command cmd: commands){
                    if (tmps.matches(cmd.getRegexCmd())){
                        if (cmd.hasArgs){
                            cmd.setArgs(tmps);
                            cmd.run();
                            return;
                        }else{
                            cmd.run();
                            return;
                        }
                    }
                }
                println("Command not found");
            }
        }
    }

    private void clear() throws IOException{
        FileWriter fw = new FileWriter(log,false);
        fw.write("");
        fw.close();
    }

    private void clear(Integer i) throws IOException{
        if (i == 0){
            clear();
        }
        BufferedReader br = new BufferedReader(new FileReader(log));
        List<String> tmp = new ArrayList<>();
        while (br.ready()){
            tmp.add(br.readLine());
        }
        if (tmp.size() > 0 && i < tmp.size()){
            if (i > 0) {
                tmp.subList(0, i).clear();
            }
            String tmps = "";
            for (String s: tmp){
                tmps += s +"\n";
            }
            FileWriter fw = new FileWriter(log,false);
            fw.write(tmps);
            fw.close();
            this.println("Clearing " + i + " lines");
        }else{
            println("Not enough lines ("+i+") to clear");
        }
    }

    private void clearLine() throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(log));
        List<String> tmp = new ArrayList<>();
        while (br.ready()){
            tmp.add(br.readLine());
        }
        if (tmp.size() != 0){
            if (tmp.get(tmp.size()-1).equals(">")){
                tmp.remove(tmp.size()-1);
            }
            StringBuilder tmps = new StringBuilder();
            for (String s: tmp){
                tmps.append(s).append("\n");
            }
            FileWriter fw = new FileWriter(log,false);
            fw.write(tmps.toString());
            fw.close();
        }
    }

    public void newCommand(Command cmd){
        for (Command c:commands){
            if (c.getRegexCmd().equals(cmd.getRegexCmd())){
                return;
            }
        }
        commands.add(cmd);
    }
}
