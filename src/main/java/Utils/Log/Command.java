package Utils.Log;

import java.util.HashMap;
import java.util.Map;

import static Main.Launcher.*;

public abstract class Command {

    private String cmd;
    private Map<String, Args> argsMap = new HashMap<>();
    private boolean nullable;
    private boolean hasArgs = false;
    protected boolean argsLoad = false;

    protected Command(boolean nullable, String cmd, String... args){
        this.nullable = nullable;
        this.cmd = cmd;
        if (args.length > 0){
            hasArgs = true;
        }
        for (String o: args){
            if(argsMap.get(o) == null) argsMap.put(o,new Args());
        }
    }

    protected Command(String cmd, String... args){
        this(true, cmd, args);
    }

    public abstract void run();

    void flush(){
        for (Args a: argsMap.values()) {
            a.flush();
        }
        argsLoad = false;
    }

    Map<Boolean, String> setArgs(String[] args){
        Map<Boolean, String> response = new HashMap<>();
        if (args.length > 1 && !hasArgs){
            response.put(false,"Command has no parameters");
            return response;
        }
        if(!nullable && args.length < argsMap.size() + 1){
            response.put(false,"Missing parameters");
            return response;
        }
        for (int i = 1; i < args.length; i++){
            String s = args[i];
            s = s.replaceFirst("\\s","");
            String arg = s.substring(0,1);
            Args a = argsMap.get(arg);
            if (a != null){
                if (s.length() == 1){
                    response.put(false,"Parameter \""+s+"\" missing value.");
                    return response;
                }
                a.setValue(s.substring(1));
                argsLoad = true;
            }else {
                response.put(false,"Unknown parameter \""+arg+"\"");
                return response;
            }
        }

        response.put(true,"Success!");
        return response;
    }

    String getCmd() {
        return cmd;
    }

    protected Args getArg(String param){
        return argsMap.get(param);
    }

    public static class Args{

        public String getAsString(){
            return value;
        }

        public Integer getAsInteger(){
            return value == null ? null : Integer.parseInt(value);
        }

        public Boolean getAsBoolean(){
            return !value.toLowerCase().equals("true") && !value.toLowerCase().equals("false") ? null : Boolean.parseBoolean(value);
        }

        public Double getAsDouble(){
            return value == null ? null : Double.parseDouble(value);
        }

        public Float getAsFloat(){
            return value == null ? null : Float.parseFloat(value);
        }

        public Long getAsLong(){
            return value == null ? null : Long.parseLong(value);
        }

        public Character getAsCharacter(){
            return value == null ? null : value.charAt(0);
        }

        private void flush(){
            value = null;
        }

        private String handle;

        void setValue(String value) {
            this.value = value;
        }

        private String value;

    }
}
