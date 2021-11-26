package com.Alvaro;

import com.Alvaro.h2.ConnectionUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    @Override
    public void start(Stage primaryStage){
        ConnectionUtil.connect();
        loadScene(primaryStage, "main", " Cronómetro", false, true);
    }

    private static Parent loadFXML(String fxml) {
        Parent result;
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        try {
            result = fxmlLoader.load();
        } catch (IOException e) {
            logger.error("Hubo un error al cargar la vista "+fxml+" con el mensaje:\n"+e.getMessage());
            //Dialog.showError("ERROR", "Hubo un error al cargar la vista", "La vista " + fxml + " no pudo cargarse debido a:\n " + e.getMessage());
            result = null;
        }
        return result;
    }

    public static void loadScene(Stage stage, String fxml, String title, boolean SaW, boolean isResizable) {
        stage.setScene(new Scene(loadFXML(fxml)));
        stage.getIcons().add(new Image(Objects.requireNonNull(App.class.getResourceAsStream("icon.png"))));
        stage.setTitle(title);
        stage.setResizable(isResizable);
        if (SaW)
            stage.showAndWait();
        else
            stage.show();
    }

    public static void main(String[] args) {
        launch();
        ConnectionUtil.disconnect();
    }
}
