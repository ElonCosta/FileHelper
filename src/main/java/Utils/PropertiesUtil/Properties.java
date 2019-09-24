package Utils.PropertiesUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Properties {

    Map<String, PropertiesObject> objectMap;
    Map<String, PropertiesObject> arrayMap;

    String[] objs;
    int pos = -1;

    Properties(File file){
        String textFile = "";
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            while (br.ready()){
                textFile += br.readLine().trim().replaceAll("\\s","");
            }
            objs = textFile.split("(?<=};|];)",-1);
        }catch (IOException ex){
            ex.printStackTrace();
        }
        for (String s : objs){
            System.out.println(s);
            findObjects(s);
        }
    }

    public Properties(String filePath){
        this(new File(filePath));
    }

    private void getObject(String key){

    }

    private void getArray(String key){

    }

    private boolean findObjects(String s){
        boolean isObject = false;
        char[] arr = s.toCharArray();

        String key = "";
        String value = "";


        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < arr.length; i++){
            char c = arr[i];
            if (c == ':' && arr[i+1] == '{' && !isObject){
                System.out.println("a");
                isObject = true;
                key = sb.toString();
                sb = new StringBuilder();
                continue;
            }else if(c == ':' && arr[i+1] == '[' && !isObject){
                System.out.println("b");
                return false;
            }
            if (c == '}' && arr[i+1] == ';'){
                System.out.println("e");
                sb.append(c);
                value = sb.toString();
                objectMap.put(key,new PropertiesObject(key,value));
                return true;
            }
            if (!isObject){
                System.out.println("c");
                sb.append(c);
            }
            if (isObject){
                System.out.println("d" + c);
                sb.append(c);
            }
        }


        return false;

    }


    /*
    * Character checker
     */

//    private boolean hasNext(){
//        return pos < chars.size();
//    }
//
//    private boolean hasLast(){
//        return pos >= -1;
//    }
//
//    private char nextChar() throws Exception{
//        pos++;
//        if (pos == chars.size()){
//            throw new Exception("Max character limit reached");
//        }
//        return chars.get(pos);
//    }
//
//    private char peekNextChar() throws Exception{
//        if (pos +1 == chars.size()){
//            throw new Exception("Max character limit reached");
//        }
//        return chars.get(pos + 1);
//    }
//
//    private char thisChar(){
//        return chars.get(pos);
//    }
//
//    private char lastChar() throws Exception{
//        pos--;
//        if (pos < -1){
//            throw new Exception("Min character limit reached");
//        }
//        return chars.get(pos);
//    }
//
//    private char peekLastChar() throws Exception{
//        if (pos - 1 < -1){
//            throw new Exception("Min character limit reached");
//        }
//        return chars.get(pos - 1);
//    }
}
