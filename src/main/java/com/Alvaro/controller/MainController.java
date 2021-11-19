package com.Alvaro.controller;

import com.Alvaro.App;
import com.Alvaro.models.Chronometer;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXLabel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class MainController {

    @FXML
    private MFXLabel lbTime;
    @FXML
    private MFXButton btnStart;
    @FXML
    private MFXButton btnStopResume;
    @FXML
    private MFXButton btnRestart;
    @FXML
    private MenuItem close;
    @FXML
    private MenuItem about;
    private boolean onlyOnce;

    @FXML
    protected void initialize() {
        Chronometer c = new Chronometer();
        Thread t = new Thread(c);
        lbTime.textProperty().bind(Bindings.createStringBinding(() -> c.getTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")), c.getElapsedTime().currentTimeProperty()));
        btnStart.setOnAction(event -> {
            if(!onlyOnce)
                t.start();
            else
                c.start();
            onlyOnce=true;
            c.getRequestSuspend().setSuspended(false);
            btnStart.setDisable(true);
            btnStart.setText("Ejecutándose");
            btnStart.setStyle("-fx-background-color: rgb(132,227,70);");
            btnStopResume.setDisable(false);
            btnRestart.setDisable(false);
        });
        btnStopResume.setOnAction(event -> {
            if (btnStopResume.getText().equals("Parar")) {
                c.stop();
                btnStart.setText("Detenido");
                btnStart.setStyle("-fx-background-color: rgb(241,65,65);");
                btnStopResume.setText("Continuar");
            } else if (btnStopResume.getText().equals("Continuar")) {
                c.resume();
                btnStart.setText("Ejecutándose");
                btnStart.setStyle("-fx-background-color: rgb(132,227,70);");
                btnStopResume.setText("Parar");
            }
        });
        btnRestart.setOnAction(event -> {
            c.stop();
            btnRestart.setDisable(true);
            btnStopResume.setDisable(true);
            btnStopResume.setText("Parar");
            btnStart.setText("Iniciar");
            btnStart.setStyle("");
            btnStart.setDisable(false);
        });
        close.setOnAction(event -> {
            ((Stage) btnStart.getScene().getWindow()).close();
        });
        about.setOnAction(event -> {
            App.loadScene(new Stage(), "about", "Sobre Chronometer", true, false);
        });
    }
}
