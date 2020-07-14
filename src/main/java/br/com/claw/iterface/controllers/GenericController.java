package br.com.claw.iterface.controllers;

import javafx.fxml.Initializable;

public abstract class GenericController implements Initializable {
    protected abstract void postInit();

    protected abstract void initEvents();
}
