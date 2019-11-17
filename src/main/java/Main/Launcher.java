package Main;

import ArchiveLoader.Configurations;
import ArchiveLoader.Loader;
import Interface.UI_Controller.AppUIController;
import Utils.Log.Command;
import Utils.Log.Log;

import java.io.*;
import java.text.ParseException;

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
