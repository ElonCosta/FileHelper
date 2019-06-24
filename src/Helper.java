import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Helper {

    File source = new File("Source");
    File archive = new File("Source/Archive");
    File latest = new File("Source/Latest");
    File yearArchive = new File(archive.toString()+"/"+(new SimpleDateFormat("yyyy").format(new Date())));
    File monthArchive = new File(yearArchive.toString() + "/" + (new SimpleDateFormat("MM").format(new Date())));
    File dayArchive = new File(monthArchive.toString() + "/" + (new SimpleDateFormat("dd").format(new Date())));


    Helper(){
        createFolders();
    }

    public void checker(File file){
        isInLatest(file);
    }

    private void isInLatest(File file){
        File inLatest = new File(latest.toString() + file.getName());
        if (!inLatest.exists()){
            if ()
        }
    }

    private void createFolders(){
        if (this.source.mkdir()){
            System.out.println("Creating source folder");
        }
        if (archive.mkdir()){
            System.out.println("Creating archive folder");
        }
        if (latest.mkdir()){
            System.out.println("Creating \"latest\" folder");
        }
        if (yearArchive.mkdir()){
            System.out.println("Creating " + yearArchive.getName() + " Archive Folder");
        }
        if (monthArchive.mkdir()){
            System.out.println("Creating " + monthArchive.getName() + " Archive Folder");
        }
        if (dayArchive.mkdir()){
            System.out.println("Creating " + dayArchive.getName() + " Archive Folder");
        }
    }
}
