package com.Alvaro.models;

import com.Alvaro.h2.ConnectionUtil;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class StopWatch {
    private enum querys {
        SELECT_ALL("SELECT id,time FROM history ORDER BY id DESC"),
        INSERT("INSERT INTO history(time) VALUES(?)");
        private String q;

        querys(String q) {
            this.q = q;
        }

        public String getQ() {
            return this.q;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(StopWatch.class);
    private final Timeline elapsedTime;
    private LocalTime time;
    private final RequestSuspend requestSuspend = new RequestSuspend();
    //Necesitamos un puntero al hilo actual para poder interrumpirlo posteriormente en el stop
    private Thread t;
    private int i = 0;
    private final SimpleStringProperty threadEnded;

    public StopWatch() {
        this.elapsedTime = new Timeline(new KeyFrame(Duration.millis(1000), event -> this.time = this.time.plusSeconds(1)));
        this.elapsedTime.setCycleCount(Animation.INDEFINITE);
        this.time = LocalTime.of(0, 0, 0);
        this.requestSuspend.setSuspended(false);
        this.threadEnded = new SimpleStringProperty();
    }

    /**
     * Creamos un nuevo hilo, lo asignamos a la variable t para tenerla de puntero y lanzamos el hilo
     */
    public void start() {
        this.t = new Thread(this::loop, "Hilo " + i++);
        this.requestSuspend.setSuspended(false);
        t.start();
    }

    /**
     * Interrumpimos el hilo
     */
    public void stop() {
        t.interrupt();
    }

    /**
     * Cambiamos el valor de suspendido para parar el cronómetro, pero NO se interrumpe
     */
    public void pause() {
        this.requestSuspend.setSuspended(true);
    }

    /**
     * Cambiamos el valor de suspendido para renaudar el hilo
     */
    public void resume() {
        this.requestSuspend.setSuspended(false);
    }

    /**
     * Bucle infinito que itera mientras que el hilo no esté interrumpido
     * En función de si está suspendido o no se parará el cronómetro o de lo contrario continuará por donde lo dejó.
     * El system.out.print es para que tenga algo que iterar y no se detenga el hilo hasta que no lo interrumpamos
     */
    private void loop() {
        this.elapsedTime.playFromStart();
        this.time = LocalTime.of(0, 0, 0);
        while (t != null && !t.isInterrupted()) {
            System.out.print("");
            if (!this.getRequestSuspend().getSuspend())
                this.elapsedTime.play();
            else if (this.getRequestSuspend().getSuspend()) {
                this.elapsedTime.stop();
                this.getRequestSuspend().waitResume();
            }
        }
        querys q;
        List<Object> params = new ArrayList<>();
        params.add(this.time);
        q = querys.INSERT;
        ConnectionUtil.execUpdate(q.getQ(), params, true);
        Platform.runLater(() -> this.threadEnded.setValue("Soy: " + t.getName() + " y he terminado mi trabajo"));
        this.time = LocalTime.of(0, 0, 0);
    }

    public Timeline getElapsedTime() {
        return this.elapsedTime;
    }

    public LocalTime getTime() {
        return this.time;
    }

    public RequestSuspend getRequestSuspend() {
        return this.requestSuspend;
    }

    public Thread getThread() {
        return this.t;
    }

    public SimpleStringProperty getThreadEndedString() {
        return this.threadEnded;
    }

    /**
     * Esta función hace la consulta a la base de datos para devolver el historial
     * @return lista de los timers(como string)
     */
    public static List<String> getHistory(){
        List<String> result = null;
        try {
            ResultSet rs = ConnectionUtil.execQuery(querys.SELECT_ALL.getQ(), null);
            result = new ArrayList<>();
            while (rs.next()) {
                result.add(String.valueOf(rs.getTime("time")));
            }
        } catch (SQLException e) {
            logger.error("Hubo un error en la conexión a la base de datos al cargar la lista del historial:"+"\nCon el mensaje:\n"+e.getMessage());
        }
        return result;
    }

}
