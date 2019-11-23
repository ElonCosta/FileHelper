package Utils.Log;

import java.util.HashMap;
import java.util.Map;

public abstract class Command {

    private String cmd;
    private Map<String, Args> argsMap = new HashMap<>();
    private boolean nullable = false;
    private boolean hasArgs = false;
    public boolean argsLoad = false;

    private String regexCmd;

    protected Command(boolean nullable, String cmd, String... args){
        this.nullable = nullable;
        this.cmd = cmd;
        this.regexCmd = cmd;
        if (args.length > 0){
            hasArgs = true;
        }
        for (String o: args){
            argsMap.put(o,new Args());
            this.regexCmd += " (-+[a-z]+\\[+[\\w]+\\])";
        }
    }

    protected Command(String cmd){
        this.cmd = cmd;
        hasArgs = false;
    }

    public abstract void run();

    void flush(){
        for (Args a: argsMap.values()) {
            a.setValue(null);
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
            String arg = s.substring(0,1);
            Args a = argsMap.get(arg);
            if (a != null){
                a.setValue(s.substring(2));
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

        private Object getValue() {
            if(value == null){
                return null;
            }else{
                return value;
            }
        }

        public String getAsString(){
            return (String) getValue();
        }

        public Integer getAsInteger(){
            return (Integer) getValue();
        }

        public Boolean getAsBoolean(){
            return (Boolean) getValue();
        }

        public Double getAsDouble(){
            return (Double) getValue();
        }

        public Float getAsFloat(){
            return (Float) getValue();
        }

        public Long getAsLong(){
            return (Long) getValue();
        }

        public Character getAsCharacter(){
            return (Character) getValue();
        }

        private String handle;

        void setValue(Object value) {
            this.value = value;
        }

        private Object value;

    }
}
