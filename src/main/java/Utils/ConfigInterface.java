package Utils;

public abstract class ConfigInterface {

    public abstract void load();

    public abstract void save();

    public abstract void setValue(String param, Object value);
}
