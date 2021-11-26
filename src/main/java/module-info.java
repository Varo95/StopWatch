module PSPCrono {
    requires MaterialFX;
    requires java.desktop;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires javafx.base;
    requires org.slf4j;
    requires com.h2database;
    requires java.sql;

    opens com.Alvaro.controller to javafx.fxml, javafx.controls, javafx.graphics, javafx.media, javafx.base, MaterialFX;
    exports com.Alvaro;
}