package Main;

import ArchiveLoader.Configurations;
import ArchiveLoader.Loader;
import Interface.JFrame.AppUI;
import Utils.FileTransfer.SFTPConnection;
import Utils.Log.Command;
import Utils.Log.Log;
import com.Hasher.Hasher;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.File;
import java.util.Arrays;

public class Launcher {

    public static Log LOG;
    public static Configurations config;
    public static AppUI mainUI;
    public static Loader loader;
    public static Hasher hasher;

    public static void main(String[] args){
//        Launch();
        SFTPConnection con = new SFTPConnection("192.168.11.88","root","$%RTFGasd098");
        System.out.println(con.openConnection().get(false));
        System.out.println(con.downloadFile("/root/ELON/OS15127/configuration.json","./Source").get(false));
        System.out.println(con.uploadFile(new File("configuration.json"),"/root/ELON/OS15127/configuration.json").get(false));
        con.closeConnection();
    }

    private static void Launch(){
            config = new Configurations();
            mainUI = new AppUI();
            LOG = new Log();
            config.loadCommands();
            if (config.getGlobal().getHashKey().trim().equals("")){
                hasher = new Hasher();
                config.getGlobal().setHashKey(hasher.getHashKey());
            }else {
                hasher = new Hasher(config.getGlobal().getHashKey());
            }
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
