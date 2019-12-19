package Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@SuppressWarnings("Duplicates")
public class Reader {

    public static String[] split(String str, char splitAt){
        char[] chars = str.toCharArray();
        ArrayList<String> words = new ArrayList<>();
        StringBuilder word = new StringBuilder();
        for (int i = 0; i < chars.length; i++){
            char c = chars[i];
            if (i == chars.length-1){
                word.append(c);
                words.add(word.toString().trim());
                word = new StringBuilder();
            }
            if(c != splitAt){
                word.append(c);
            }else{
                words.add(word.toString().trim());
                word = new StringBuilder();
            }
        }

        return words.toArray(new String[0]);
    }

}
