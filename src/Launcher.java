import java.io.File;

public class Launcher {
    public static void main(String[] args) {
        Helper helper = new Helper();
        helper.checker(new File("AAA/BBB"));
    }
}
