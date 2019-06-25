import org.json.*;

import java.io.*;
import java.nio.file.Files;
import java.util.Timer;

public class Launcher {
    public static void main(String[] args) throws Exception{
        System.gc();
        Helper helper = new Helper();
        ConfigLoader configLoader = new ConfigLoader(helper);
        configLoader.load();
        Long time = System.currentTimeMillis();

//        while (true){
//            System.out.println("Initiating routine: \n");
//            if (System.currentTimeMillis() >= time+300000){
//                helper.checker(new File("/home/desenv/workspace_Flash2015/Flash2015/build"));
//                time = System.currentTimeMillis();
//            }
//        }
    }
}
