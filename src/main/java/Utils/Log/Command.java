package Utils.Log;

import java.util.ArrayList;
import java.util.List;

public abstract class Command {

    private String cmd;
    private List<Args> args;
    public boolean hasArgs = false;
    public boolean noArgs = false;

    private String regexCmd;

    public Command(String cmd, Object... args){
        this.cmd = cmd;
        this.args = new ArrayList();
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
            this.regexCmd += " " + handle + "(\\[+[\\w]+\\])";
        }
        System.out.println(regexCmd + "|" + hasArgs);
    }

    public abstract void run();

    public void getArgs(String cmd){
        String[] split = cmd.split("(?=-+[a-z](\\[+[\\w]+\\]))");
        if (split.length > 1 && (split.length-1) == args.size()){
            for (int i = 1; i <= args.size(); i++){
                String s = split[i].replaceAll("(-+[a-z](\\[)|(\\]))","");
                args.get(i-1).setValue(s);
                System.out.println(s);
                System.out.println(args.get(i-1).getValue());
            }
        }
        if (split.length == 1){
            noArgs = true;
        }
    }

    public String getCmd() {
        return cmd;
    }

    public String getRegexCmd() {
        return regexCmd;
    }

    public List<Args> getArgs() {
        return args;
    }

    public class Args<T>{

        public String getHandle() {
            return handle;
        }

        public T getValue() {
            return value;
        }

        private String handle;

        public void setValue(Object value) {
            T ob = (T) value;
            this.value = ob;
        }

        private T value;

        protected Args(String handle, T value){
            this.handle = handle;
            this.value = value;
        }

    }
}
