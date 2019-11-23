package Main;

import ArchiveLoader.Configurations;
import ArchiveLoader.Loader;
import Interface.UI_Controller.AppUIController;
import Utils.Log.Command;
import Utils.Log.Log;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static Utils.Reader.*;

@SuppressWarnings("Duplicates")
public class Launcher {

    public static Log LOG;
    public static Configurations config;
    public static AppUIController mainUI;

    public static void main(String[] args){
        Launch();
    }

    private static void Launch(){
            config = new Configurations();
            mainUI = new AppUIController();
            LOG = new Log();
            config.loadCommands();
            LOG.newCommand(new Command(false,"AAAA", "a", "b","c") {
                @Override
                public void run() {
                    String a = getArg("a").getAsString();
                    String b = getArg("b").getAsString();
                    String c = getArg("c").getAsString();
                    LOG.println(a + " " + b + " " + c);
                }
            });
            LOG.newCommand(new Command("restart") {
                @Override
                public void run() {
                    Restart();
                }
            });
            Loader loader = new Loader();
            loader.load();
    }

    private static void Restart(){
        Launch();
    }

}
