package Interface.Components.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileHistoric {

    private List<FileDetails> historic = new ArrayList<>();
    private int pos = 0;

    public FileHistoric(FileDetails folder){
        historic.add(folder);
    }

    public void addToHistoric(FileDetails folder){
        if (pos != historic.size() - 1) {
            if (historic.size() > pos + 1) {
                historic.subList(pos + 1, historic.size()).clear();
            }
        }
        historic.add(folder);
        pos++;
        historic.forEach(f-> System.out.println(f.getName()));
        System.out.println("----------------- " + pos);
    }

    public void addToHistoric(File folder){
        addToHistoric(new FileDetails(folder));
    }

    public boolean hasNext(){
        return pos < historic.size()-1;
    }

    public FileDetails next(){
        pos++;
        if (pos >= historic.size()) pos = historic.size()-1;
        return historic.get(pos);
    }

    public boolean hasPrevious(){
        return pos > 0;
    }

    public FileDetails previous(){
        pos--;
        if (pos < 0) pos = 0;
        return historic.get(pos);
    }

}
