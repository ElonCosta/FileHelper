package Utils;

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

    public Log(){
        log.delete();
    }

    public void println(Object x){
        println(x, true);
    }

    public void println(Object x, boolean y){
        if (y){
            String ln = sdf.format(new Date()) + (x) + "\n";
            try{
                FileWriter fw = new FileWriter(log,true);
                fw.write(ln);
                fw.close();
            }catch (IOException iE){
                System.out.println(iE);
            }
        }else{
            try{
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

    public boolean readLog(String x) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(log));
        List<String> tmp = new ArrayList<>();
        while (br.ready()){
            tmp.add(br.readLine());
        }
        if (tmp.size() != 0){
            if (tmp.get(tmp.size() - 1).equals(x)){
                this.println("\n",false);
                return true;
            }else {
                return false;
            }
        }else{
            return false;
        }
    }

}
