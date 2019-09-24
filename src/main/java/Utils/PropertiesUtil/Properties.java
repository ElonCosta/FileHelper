package Utils.PropertiesUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class Properties {

    Map<String, Object> objectMap;
    Map<String, Object> arrayMap;

    char[] chars;
    int pos = -1;

    Properties(File file){
        String textFile = "";
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            while (br.ready()){
                textFile += br.readLine().trim().replaceAll("\\s","");
            }
            chars = textFile.toCharArray();
        }catch (IOException ex){
            ex.printStackTrace();
        }
        String key = "";
        while (hasNext()){
            try{
                System.out.print(nextChar());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        while (hasLast()){
            try{
                System.out.print(lastChar());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public Properties(String filePath){
        this(new File(filePath));
    }

    private void getObject(String key){

    }

    private void getArray(String key){

    }

    private void findObjects(){
        String key = "";
    }
    private boolean hasNext(){
        return pos < chars.length;
    }

    private boolean hasLast(){
        return pos >= -1;
    }

    private char nextChar() throws Exception{
        pos++;
        if (pos == chars.length){
            System.out.println(pos);
            throw new Exception("Max character limit reached");
        }
        return chars[pos];
    }

    private char thisChar(){
        return chars[pos];
    }

    private char lastChar() throws Exception{
        pos--;
        if (pos < -1){
            throw new Exception("Min character limit reached");
        }
        return chars[pos];
    }
}
