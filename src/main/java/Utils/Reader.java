package Utils;

import java.util.Arrays;
import java.util.Collections;

@SuppressWarnings("Duplicates")
public class Reader {

    public static String[] split(String str){
        int wordSize = 0;
        char[] chars = str.toCharArray();
        String[] words = new String[wordSize];
        StringBuilder word = new StringBuilder();
        for (int i = 0; i < chars.length; i++){
            char c = chars[i];
            if (i == chars.length-1){
                word.append(c);
                wordSize++;
                String[] tmp = words;
                words = new String[wordSize];
                for (int y = 0; y < tmp.length; y++) {
                    words[y] = tmp[y];
                }
                words[wordSize-1] = word.toString().trim();
                word = new StringBuilder();
            }
            if(c != '-'){
                word.append(c);
            }else{
                wordSize++;
                String[] tmp = words;
                words = new String[wordSize];
                for (int y = 0; y < tmp.length; y++) {
                    words[y] = tmp[y];
                }
                words[wordSize-1] = word.toString().trim();
                word = new StringBuilder();
            }
        }

        return words;
    }

}
