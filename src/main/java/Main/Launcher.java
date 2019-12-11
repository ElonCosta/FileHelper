package Main;

import ArchiveLoader.Configurations;
import ArchiveLoader.Loader;
import Interface.JFrame.AppUI;
import Utils.Log.Command;
import Utils.Log.Log;

public class Launcher {

    public static Log LOG;
    public static Configurations config;
    public static AppUI mainUI;
    public static Loader loader;

    public static void main(String[] args){
        Launch();
    }

    private static void Launch(){
            config = new Configurations();
            mainUI = new AppUI();
            LOG = new Log();
            config.loadCommands();
            LOG.newCommand(new Command("restart") {
                @Override
                public void run() {
                    Restart();
                }
            });
            loader = new Loader();
            loader.load();
    }

    private static void Restart(){
        Launch();
    }

}
