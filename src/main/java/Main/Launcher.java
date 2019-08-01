package Main;

import ArchiveLoader.Loader;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONWriter;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class Launcher {
    public static void main(String[] args) throws IOException, ParseException {
        System.gc();
        Loader loader = new Loader();
        loader.load();
    }
}
