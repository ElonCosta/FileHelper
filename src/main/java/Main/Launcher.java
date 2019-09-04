package Main;

import ArchiveLoader.Loader;
import Utils.Log.Command;
import Utils.Log.Log;

import java.io.IOException;
import java.text.ParseException;

public class Launcher {

    public static Log LOG;

    public static void main(String[] args){
        Launch();
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
