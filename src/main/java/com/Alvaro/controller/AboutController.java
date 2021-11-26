package com.Alvaro.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AboutController {

    @FXML
    private MFXButton github;

    private static final Logger logger = LoggerFactory.getLogger(AboutController.class);

    @FXML
    protected void initialize() {
        github.setOnAction(event -> {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    URI uri;
                    try {
                        uri = new URI("https://github.com/Varo95/StopWatch");
                        desktop.browse(uri);
                    } catch (URISyntaxException | IOException e) {
                        if (e.getClass().equals(URISyntaxException.class))
                            logger.error("La cadena introducida viola el código RFC2396" + e.getMessage());
                         else
                            logger.error("No se encontró el navegador por defecto " + e.getMessage());
                    }
                }
            }
        });
    }

}
