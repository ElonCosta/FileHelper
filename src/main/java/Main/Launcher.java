package Main;

import ArchiveLoader.Loader;
import Utils.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Launcher {

    public static Log LOG = new Log();

    public static void main(String[] args){
        try{
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
