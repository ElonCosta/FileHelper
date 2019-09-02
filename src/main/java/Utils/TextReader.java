package Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TextReader {

    private List<String> lines;
    private String line;
    private String nextLine;
    private int position;

    public TextReader(InputStream inputStream) throws IOException {
        lines = new ArrayList<>();
        InputStreamReader fis = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(fis);
        String line;
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        System.out.println(lines.size());
        position = 0;
    }

    public TextReader(List<String> inputList){
        lines = new ArrayList(inputList);
        position = 0;
    }

    public TextReader(TextReader fr){
        lines = new ArrayList<>(fr.getLines());
        line = fr.getCurLine();
        nextLine = fr.getNextLine();
        position = fr.getPosition();
    }

    public boolean hasNext(){
        return (position) != lines.size();
    }

    public String getLine(){
        line = lines.get(position);
        position++;
        if (position < lines.size()){
            nextLine = lines.get(position);
        }else{
            nextLine = "null";
        }
        return line;
    }

    public String nextLine(){
        return nextLine;
    }

    public void jumpLines(){
        position++;
    }

    public void jumpLines(int lines){
        position += lines;
    }

    public void rerun(){
        position = 0;
    }

    public List<String> getLines() {
        return lines;
    }

    private String getNextLine(){
        return nextLine;
    }

    private String getCurLine(){
        return line;
    }

    public int getPosition() {
        return position;
    }

    public String getLastLine(){
        int pos = lines.size() > 0 ? lines.size() - 1 : 0;
        return lines.get(pos);
    }
}
