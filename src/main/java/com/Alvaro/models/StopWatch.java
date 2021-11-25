package com.Alvaro.models;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.time.LocalTime;

public class StopWatch implements Runnable {
    private final Timeline elapsedTime;
    private LocalTime time;
    private final RequestSuspend requestSuspend = new RequestSuspend();
    //Necesitamos un puntero al hilo actual para poder interrumpirlo posteriormente en el stop
    private Thread t;

    public StopWatch() {
        this(0, 0, 0);
    }

    public StopWatch(int hour, int minutes, int seconds) {
        this.elapsedTime = new Timeline(new KeyFrame(Duration.millis(1000), event -> this.time = this.time.plusSeconds(1)));
        this.elapsedTime.setCycleCount(Animation.INDEFINITE);
        this.time = LocalTime.of(hour, minutes, seconds);
    }

    /**
     * Creamos un nuevo hilo, lo asignamos a la variable t para tenerla de puntero y lanzamos el hilo
     */
    public void start() {
        this.t = new Thread(this);
        t.start();
    }

    /**
     * Interrumpimos el hilo si la animación está en ejecucción o pausada
     */
    public void stop() {
        if (this.elapsedTime.getStatus().equals(Animation.Status.RUNNING) || this.elapsedTime.getStatus().equals(Animation.Status.PAUSED)) {
            t.interrupt();
        }
    }

    /**
     * Cambiamos el valor de suspendido para parar el cronómetro, pero NO se interrumpe
     */
    public void pause(){
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
    public void loop() {
        this.elapsedTime.playFromStart();
        this.time = LocalTime.of(0, 0, 0);
        while (!t.isInterrupted()) {
            System.out.print("");
            if (!this.getRequestSuspend().getSuspend())
                this.elapsedTime.play();
            else
                this.elapsedTime.stop();
        }
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

    @Override
    public void run() {
        loop();
    }

}
