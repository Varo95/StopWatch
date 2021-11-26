package com.Alvaro.controller;

import com.Alvaro.App;
import com.Alvaro.models.StopWatch;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXLabel;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyTableView;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
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
    @FXML
    private MFXLabel threadEnd;
    @FXML
    private MFXLegacyTableView<String> table_history;
    @FXML
    private TableColumn<String, String> column_history;

    @FXML
    protected void initialize() {
        StopWatch c = new StopWatch();
        configureTable();
        table_history.setItems(FXCollections.observableList(StopWatch.getHistory()));
        lbTime.setText("00:00:00");
        c.getThreadEndedString().addListener((observable, oldValue, newValue) -> threadEnd.setText(newValue));
        btnStart.setOnAction(event -> {
            lbTime.textProperty().bind(Bindings.createStringBinding(() -> c.getTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")), c.getElapsedTime().currentTimeProperty()));
            c.start();
            btnStart.setDisable(true);
            btnStart.setText("Ejecutándose");
            btnStart.setStyle("-fx-background-color: rgb(132,227,70);");
            btnStopResume.setDisable(false);
            btnRestart.setDisable(false);
        });
        btnStopResume.setOnAction(event -> {
            if (btnStopResume.getText().equals("Parar")) {
                c.pause();
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
            lbTime.textProperty().unbind();
            lbTime.setText("00:00:00");
            table_history.getItems().add(0,String.valueOf(c.getTime()));
            c.stop();
            btnRestart.setDisable(true);
            btnStopResume.setDisable(true);
            btnStopResume.setText("Parar");
            btnStart.setText("Iniciar");
            btnStart.setStyle("");
            btnStart.setDisable(false);
        });
        close.setOnAction(event -> {
            if (c.getThread() != null)
                c.stop();
            ((Stage) btnStart.getScene().getWindow()).close();
        });
        about.setOnAction(event -> App.loadScene(new Stage(), "about", "Sobre StopWatch", true, false));
        Platform.runLater(() -> btnStart.getScene().getWindow().setOnCloseRequest(event ->{ if(c.getThread()!=null) c.stop(); }));
    }

    private void configureTable(){
        column_history.setCellValueFactory(each -> new SimpleStringProperty(each.getValue()));
    }

}
