package Log;

public class PrintBuffer {

    StringBuilder print;

    public PrintBuffer(){
        print = new StringBuilder();
    }

    PrintBuffer(StringBuilder print){
        this.print = print;
    }

    public void addLine(String line){
        print.append(line).append("\n");
    }

    public void add(String line){
        print.append(line);
    }

    public String print(){
        return print.toString();
    }
}
