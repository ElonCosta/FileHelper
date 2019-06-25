import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.io.*;

public class Helper {

    File source;
    File archive;
    File latest;
    String[] toAvoid;
    Helper(){
    }

    public void checker(ArchiveData data){
        File file = data.getFile();
        toAvoid = data.getToAvoid();
        File inLatest = new File(latest.toString() + "/" + data.getName() + "/" + file.getName());
        if (inLatest.getName().equals(file.getName())){
            if(!checker(inLatest,file)){
                createFile(data);
            }
        }
    }

    private void createFile(ArchiveData data){
        File file = data.getFile();
        File inLatest = new File(latest.toString() + "/" + data.getName() + "/" + file.getName());
        if (!inLatest.exists()){
            if (file.isDirectory()){
                inLatest.mkdir();
                try{
                    FileUtils.copyDirectory(file,inLatest);
                }catch (Exception e){
                    System.err.println(e);
                }
            }else{
                inLatest = new File(latest.toString() + "/" + data.getName());
                inLatest.mkdir();
                try{
                    FileUtils.copyFileToDirectory(file, inLatest);
                }catch (Exception e){
                    System.err.println(e);
                }
            }
        }else{
            try{
                File arch = new File(archive.toString() + (new SimpleDateFormat("/hh_mm")).format(new Date()) + "/" + inLatest.getName());
                System.out.println("Archiving \"" + arch.getName() + "\" on" + archive.toString() + (new SimpleDateFormat("/hh_mm")).format(new Date()));
                arch.mkdirs();
                FileUtils.copyDirectory(inLatest,arch);
                FileUtils.deleteDirectory(inLatest);
            }catch (Exception e){
                System.err.println(e);
            }
            createFile(data);
        }
    }

    private boolean checker(File file1, File file2){
        if (!file1.exists() || !file2.exists()){
            return false;
        }
        for (String ext: toAvoid){
            if (file1.getName().endsWith(ext)|| file2.getName().endsWith(ext)){
                return true;
            }
        }
        if (file1.isDirectory() && file2.isDirectory()){
            File[] file1Arr = file1.listFiles();
            File[] file2Arr = file2.listFiles();
            if (file1Arr.length == file2Arr.length){
                for (int i = 0; i < file1Arr.length; i++){
                    if (!file1Arr[i].getName().equals(file2Arr[i].getName())){
                        return false;
                    }
                    if (!checker(file1Arr[i],file2Arr[i])){
                        return false;
                    }
                }
            }else {
                return false;
            }


        }else if (file1.isFile() && file2.isFile()){
            try{
                BufferedReader br1 = new BufferedReader(new FileReader(file1));
                BufferedReader br2 = new BufferedReader(new FileReader(file2));

                String str1 = "";
                while (br1.ready()){
                    str1 += br1.readLine() + "\n";
                }
                br1.close();
                String str2 = "";
                while (br2.ready()){
                    str2 += br2.readLine() + "\n";
                }
                br2.close();
                return str1.equals(str2);
            }catch (Exception e){

            }
        }
        return true;
    }

    public void createFolders(){
        if (this.source.mkdir()){
            System.out.println("Creating " + source.toString());
        }
        if (archive.mkdirs()){
            System.out.println("Creating " + archive.toString());
        }
        if (latest.mkdir()){
            System.out.println("Creating " + latest.toString());
        }
    }

    public void setArchive(String archive) {
        this.archive = new File(source.toString()+"/"+archive);
    }

    public void setLatest(String latest) {
        this.latest = new File(source.toString()+"/"+latest);
    }

    public void setSource(String source) {
        this.source = new File(source);
    }
}
