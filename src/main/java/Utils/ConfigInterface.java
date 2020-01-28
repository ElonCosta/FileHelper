package Utils;


public interface ConfigInterface {

    void load();

    void save();

    void createFieldsIfEmpty();

    Object getAsObject();
}
