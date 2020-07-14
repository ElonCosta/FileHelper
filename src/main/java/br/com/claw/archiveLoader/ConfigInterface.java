package br.com.claw.archiveLoader;


public interface ConfigInterface {

    void load();

    void save();

    void createFieldsIfEmpty();

    Object getAsObject();
}
