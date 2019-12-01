package Utils.Log;

import java.util.HashMap;
import java.util.Map;

import static Main.Launcher.*;

public abstract class Command {

    private String cmd;
    private Map<String, Args> argsMap = new HashMap<>();
    private boolean nullable = false;
    private boolean hasArgs = false;
    protected boolean argsLoad = false;

    private String regexCmd;

    protected Command(boolean nullable, String cmd, String... args){
        this.nullable = nullable;
        this.cmd = cmd;
        this.regexCmd = cmd;
        if (args.length > 0){
            hasArgs = true;
        }
        for (String o: args){
            if(argsMap.get(o) == null) argsMap.put(o,new Args());
            else LOG.printErr(7);
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

    int setArgs(String[] args){
        if (args.length > 1 && !hasArgs){
            return 3;
        }
        if(args.length > argsMap.size() + 1){
            return 5;
        }
        if(!nullable && args.length < argsMap.size() + 1){
            return 4;
        }
        for (int i = 1; i < args.length; i++){
            String s = args[i];
            if (s.length() == 1){
                return 2;
            }
            s = s.replaceFirst("\\s","");
            String arg = s.substring(0,1);
            Args a = argsMap.get(arg);
            if (a != null){
                a.setValue(s.substring(1));
                argsLoad = true;
            }else {
                return 1;
            }
        }

        return 0;
    }

    String getCmd() {
        return cmd;
    }

    String getRegexCmd() {
        return regexCmd;
    }

    protected Args getArg(String param){
        return argsMap.get(param);
    }

    public static class Args{

        public String getAsString(){
            return value;
        }

        public Integer getAsInteger(){
            return Integer.parseInt(value);
        }

        public Boolean getAsBoolean(){
            return Boolean.parseBoolean(value);
        }

        public Double getAsDouble(){
            return Double.parseDouble(value);
        }

        public Float getAsFloat(){
            return Float.parseFloat(value);
        }

        public Long getAsLong(){
            return Long.parseLong(value);
        }

        public Character getAsCharacter(){
            return value.charAt(0);
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
