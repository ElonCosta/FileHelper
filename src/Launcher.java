import java.io.File;
import java.util.Timer;

public class Launcher {
    public static void main(String[] args) {
        Helper helper = new Helper();
        Long time = System.currentTimeMillis();
        helper.checker(new File("AAA"));
        while (true){
            if (System.currentTimeMillis() >= time+300000){
                helper.checker(new File("AAA"));
                time = System.currentTimeMillis();
            }
        }
    }
}
