package br.com.claw.enums;

import br.com.claw.utils.Utils;
import lombok.Getter;

import java.io.InputStream;

public enum IMAGES{
    ADD("/UI/IMAGES/new.png");

    @Getter
    private final InputStream path;

    IMAGES(String url) { path = Utils.class.getResourceAsStream(url);}
}
