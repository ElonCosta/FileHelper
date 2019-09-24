package Main;

import ArchiveLoader.Loader;
import Utils.Constants;
import Utils.Log.Command;
import Utils.Log.Log;
import Utils.PropertiesUtil.Properties;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Launcher {

    public static Log LOG;
    private static Loader loader;

    public static void main(String[] args){
        //Launch();
        Properties p = new Properties("./properties.config");
    }

    private static void Launch(){
        try{
            LOG = new Log();
            LOG.newCommand(new Command("restart") {
                @Override
                public void run() {
                    Restart();
                }
            });
            loader = new Loader();
            loader.load();
        }catch (IOException | ParseException e){
            LOG.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void Restart(){
        Launch();
    }

}
