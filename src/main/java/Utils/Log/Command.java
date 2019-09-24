package Utils.Log;

import java.util.ArrayList;
import java.util.List;

public abstract class Command {

    public static String[] vars = new String[]{"i","s","c","f","d","l","b"};
    private String cmd;
    private List<Args> args;
    public boolean hasArgs = false;
    public boolean noArgs = false;

    private String regexCmd;

    public Command(String cmd, Object... args){
        this.cmd = cmd;
        this.args = new ArrayList<>();
        this.regexCmd = cmd;
        if (args.length > 0){
            hasArgs = true;
        }
        for (Object o: args){
            String handle = o.toString().replaceAll("(\\[+[\\w]+\\])","");
            if (o.toString().replaceAll("(-+[a-z]+\\[)|(\\])","").equals("I")){
                Args<Integer> a = new Args<>(handle, 0);
                this.args.add(a);
            }
            if (o.toString().replaceAll("(-+[a-z]+\\[)|(\\])","").equals("S")){
                Args<String> a = new Args<>(handle, "");
                this.args.add(a);
            }
            if (o.toString().replaceAll("(-+[a-z]+\\[)|(\\])","").equals("C")){
                Args<Character> a = new Args<>(handle, ' ');
                this.args.add(a);
            }
            if (o.toString().replaceAll("(-+[a-z]+\\[)|(\\])","").equals("F")){
                Args<Float> a = new Args<>(handle, 0F);
                this.args.add(a);
            }
            if (o.toString().replaceAll("(-+[a-z]+\\[)|(\\])","").equals("D")){
                Args<Double> a = new Args<>(handle, 0D);
                this.args.add(a);
            }
            if (o.toString().replaceAll("(-+[a-z]+\\[)|(\\])","").equals("L")){
                Args<Long> a = new Args<>(handle, 0L);
                this.args.add(a);
            }
            if (o.toString().replaceAll("(-+[a-z]+\\[)|(\\])","").equals("B")){
                Args<Boolean> a = new Args<>(handle, false);
                this.args.add(a);
            }
            this.regexCmd += " (-+[a-z]+\\[+[\\w]+\\])";
        }
    }

    public abstract void run();

    public void setArgs(String cmd){
        cmd = cmd.replaceAll("\\s","");
        String[] split = cmd.split("(?=-+[a-z](\\[+[\\w]+\\]))");
        for (Args args : args){
            for (String s : split){
                if (s.replaceAll("(\\[+[\\w]+\\])","").equals(args.getHandle())){
                    args.setValue(s.replaceAll("(-+[a-z]+\\[)|(\\])",""));
                }
            }
        }
        if (split.length == 1){
            noArgs = true;
        }
    }

    public String getCmd() {
        return cmd;
    }

    String getRegexCmd() {
        return regexCmd;
    }

    protected List<Args> getArgs() {
        return args;
    }

    protected Args getArg(String param){
        for (Args a : args){
            if (a.getHandle().equals(param)){
                return a;
            }
        }

        return null;
    }

    public static class Args<T>{

        String getHandle() {
            return handle;
        }

        public T getValue() {
            return value;
        }

        public String getAsString(){
            return value.toString();
        }

        public Integer getAsInteger(){
            return Integer.parseInt(value.toString());
        }

        public Boolean getAsBoolean(){
            return Boolean.parseBoolean(value.toString());
        }

        public Double getAsDouble(){
            return Double.parseDouble(value.toString());
        }

        public Float getAsFloat(){
            return Float.parseFloat(value.toString());
        }

        public Long getAsLong(){
            return Long.parseLong(value.toString());
        }

        public Character getAsCharacter(){
            return value.toString().charAt(0);
        }

        private String handle;

        void setValue(Object value) {
            this.value = (T) value;
        }

        private T value;

        Args(String handle, T value){
            this.handle = handle;
            this.value = value;
        }

    }
}
