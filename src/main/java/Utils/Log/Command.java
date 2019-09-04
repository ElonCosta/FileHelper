package Utils.Log;

import java.util.ArrayList;
import java.util.List;

public abstract class Command {

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    private String cmd;
    public List<Args> args;

    public Command(String cmd){
        this.cmd = cmd;
        args = null;
    }

    public Command(String cmd, Object... args){
        this.cmd = cmd;
        this.args = new ArrayList();
        for (Object o: args){
            String handle = o.toString().replaceAll("(\\[+[\\w]+\\])","");
            if (o.toString().replaceAll("(-+[a-z]+\\[)|(\\])","").equals("I")){
                o = new Integer(0);
                Args a = new Args(handle, o);
                this.args.add(a);
            }
            if (o.toString().replaceAll("(-+[a-z]+\\[)|(\\])","").equals("S")){
                o = new String("");
                Args a = new Args(handle, o);
                this.args.add(a);
            }
            if (o.toString().replaceAll("(-+[a-z]+\\[)|(\\])","").equals("C")){
                o = new Character(' ');
                Args a = new Args(handle, o);
                this.args.add(a);
            }
        }
        for (Args o: this.args){
            System.out.println(o.getValue() instanceof Integer);
            System.out.println(o.getValue() instanceof String);
        }
    }

    public abstract void run();

    public void getArgs(){

    }

    public class Args{

        public String getHandle() {
            return handle;
        }

        public Object getValue() {
            return value;
        }

        private String handle;

        public void setValue(Object value) {
            if (this.value.getClass().equals(value.getClass())) {
                this.value = value;
            }
        }

        private Object value;

        protected Args(String handle, Object value){
            this.handle = handle;
            this.value = value;
        }

    }
}
