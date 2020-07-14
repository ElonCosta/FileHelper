package br.com.claw.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.claw.enums.KEY;

public class JSONUtils {

    public static void put(JSONObject jo, KEY key, Object value){
        jo.put(key.getVar(),value);
    }

    public static boolean isNull(JSONObject jo, KEY key){
        return jo.isNull(key.getVar());
    }

    public static Object get(JSONObject jo, KEY key){
        return jo.get(key.getVar());
    }
    public static String getString(JSONObject jo, KEY key){
        return jo.getString(key.getVar());
    }
    public static Boolean getBoolean(JSONObject jo, KEY key){
        return jo.getBoolean(key.getVar());
    }
    public static Float getFloat(JSONObject jo, KEY key){
        return jo.getFloat(key.getVar());
    }
    public static JSONObject getJSONObject(JSONObject jo, KEY key){
        return jo.getJSONObject(key.getVar());
    }
    public static JSONArray getJSONArray(JSONObject jo, KEY key){
        return jo.getJSONArray(key.getVar());
    }
    public static Integer getInteger(JSONObject jo, KEY key){
        return jo.getInt(key.getVar());
    }
}
