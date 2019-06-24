import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.io.*;

public class Helper {

    File source = new File("Source");
    File archive = new File("Source/Archive");
    File yearArchive = new File(archive.toString()+"/"+(new SimpleDateFormat("yyyy").format(new Date())));
    File monthArchive = new File(yearArchive.toString() + "/" + (new SimpleDateFormat("MM").format(new Date())));
    File dayArchive = new File(monthArchive.toString() + "/" + (new SimpleDateFormat("dd").format(new Date())));
    File latest = new File("Source/Latest");


    Helper(){
        createFolders();
    }

    public void checker(File file){
        File inLatest = new File(latest.toString() + "/" + file.getName());
        if (inLatest.getName().equals(file.getName())){
            if(!checker(inLatest,file));{
                try {
                    FileUtils.deleteDirectory(inLatest);
                }catch (Exception e){

                }
                createFile(file);
            }

        }
    }

    private void createFile(File file){
        File inLatest = new File(latest.toString() + "/" + file.getName());
        if (!inLatest.exists()){
            inLatest.mkdir();
            try{
                FileUtils.copyDirectory(file,inLatest);
            }catch (Exception e){
                System.err.println(e);
            }
        }else{
            try{
                FileUtils.deleteDirectory(inLatest);
            }catch (Exception e){
                System.err.println(e);
            }
            createFile(file);
        }
    }

    private boolean checker(File file1, File file2){
            if (file1.isDirectory() && file2.isDirectory()){
                File[] file1Arr = file1.listFiles();
                File[] file2Arr = file2.listFiles();
                if (file1Arr.length == file2Arr.length){
                    for (int i = 0; i < file1Arr.length; i++){
                        System.out.println(file1Arr[i].getName() + "|" + file2Arr[i].getName());
                        if (!checker(file1Arr[i],file2Arr[i])){
                            return false;
                        }
                    }
                }else {
                    return false;
                }
            }
            return true;
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
