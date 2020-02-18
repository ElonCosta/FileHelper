package ArchiveLoader.Archive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Collections<T> {

    String name;
    Map<String, T> children = new HashMap<String, T>();

    public Collections(String name){
        this.name = name;
    }

    public void add(String name,T child){
        children.put(name, child);
    }

    public T get(String s){
        return children.get(s);
    }

    public Map<String, T> getChildren() {
        return children;
    }
}
