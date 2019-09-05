package Utils.Log;

import Utils.TextReader;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Log {

    static File log = new File("./logFile.txt");
    private TextReader logLines;
    private String LOG = "";
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss ");
    private List<Command> commands = new ArrayList<>();

    public Log() throws IOException{
        clear();
        newCommand(new Command("clear","-n[I]") {
            @Override
            public void run() {
                try{
                    clear(Integer.valueOf(getArgs().get(0).getValue().toString()));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        newCommand(new Command("clear") {
            @Override
            public void run() {
                try{
                    clear();
                    println(">");
                }catch (Exception e){
                    System.out.println(e);
                }
            }
        });
    }

    public void println(Object x){
        println(x, true);
    }

    public void println(Object x, boolean y){
        if (y){
            String ln = sdf.format(new Date()) + (x) + "\n>";
            try{
                clearLine();
                FileWriter fw = new FileWriter(log,true);
                fw.write(ln);
                fw.close();
            }catch (IOException iE){
                System.out.println(iE);
            }
        }else{
            try{
                clearLine();
                FileWriter fw = new FileWriter(log,true);
                fw.write(x.toString());
                fw.close();
            }catch (IOException iE){
                System.out.println(iE);
            }
        }
    }

    public byte[] getBytes(){
        return this.LOG.getBytes();
    }

    public void readCommand() throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(log));
        List<String> tmp = new ArrayList<>();
        while (br.ready()){
            tmp.add(br.readLine());
        }
        if (tmp.size() > 0){
            if (tmp.get(tmp.size() - 1).startsWith(">>")){
                String tmps = tmp.get(tmp.size() - 1).substring(2);
                for (Command cmd: commands){
                    if (tmps.matches(cmd.getRegexCmd())){
                        System.out.println(tmps);
                        if (cmd.hasArgs){
                            System.out.println(tmps);
                            cmd.getArgs(tmps);
                            cmd.run();
                        }else{
                            System.out.println(tmps);
                            cmd.run();
                        }
                    }
                }
            }
        }
    }

    private void clear() throws IOException{
        FileWriter fw = new FileWriter(log,false);
        fw.write("");
        fw.close();
    }

    public void clear(Integer i) throws IOException{
        if (i == 0){
            clear();
        }
        BufferedReader br = new BufferedReader(new FileReader(log));
        List<String> tmp = new ArrayList<>();
        while (br.ready()){
            tmp.add(br.readLine());
        }
        if (tmp.size() > 0 && i < tmp.size()){
            for (int y = 0; y < i; y++){
                tmp.remove(0);
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
            String tmps = "";
            for (String s: tmp){
                tmps += s +"\n";
            }
            FileWriter fw = new FileWriter(log,false);
            fw.write(tmps);
            fw.close();
        }
    }

    public void newCommand(Command cmd){
        commands.add(cmd);
    }
}
