package Main;

import ArchiveLoader.Loader;
import Utils.Log.Command;
import Utils.Log.Log;

import java.io.IOException;
import java.text.ParseException;

public class Launcher {

    public static Log LOG;

    public static void main(String[] args){
        String s = "clear -i[20]";
        String[] ss = s.split("(?=-+[a-z]+\\[)",-1);
        for (String sss:
             ss) {
            System.out.println(sss.matches("(-+[a-z]+\\[)+([\\w])+(\\])"));
        }

        Command cmd = new Command("Clear", "-i[I]") {
            @Override
            public void run() {

            }


        };

        for (Command.Args a: cmd.args){
            a.setValue(30);
            a.setValue("a");
            a.setValue('C');
            System.out.println(a.getHandle() + "|" + a.getValue());
        }
    }

    public static void Launch(){
        try{
            LOG = new Log();
            System.gc();
            Loader loader = new Loader();
            loader.load();
        }catch (IOException iE){
            LOG.println(iE.getMessage());
        }catch (ParseException pE){
            LOG.println(pE.getMessage());
            System.out.println(pE);
        }
    }


}
