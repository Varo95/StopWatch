package com.Alvaro.models;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.time.LocalTime;

public class Chronometer implements Runnable {
    private final Timeline elapsedTime;
    private LocalTime time;
    private final RequestSuspend requestSuspend = new RequestSuspend();
    private boolean isStopped;

    public Chronometer() {
        this(0, 0, 0);
    }

    public Chronometer(int hour, int minutes, int seconds) {
        this.elapsedTime = new Timeline(new KeyFrame(Duration.millis(1000), event -> incrementSeconds()));
        this.elapsedTime.setCycleCount(Animation.INDEFINITE);
        this.time = LocalTime.of(hour, minutes, seconds);
        this.requestSuspend.setSuspended(false);
    }

    private void incrementSeconds() {
        if (!this.requestSuspend.getSuspend()) {
            this.time = this.time.plusSeconds(1);
        }
    }

    public void start() {
        this.requestSuspend.setSuspended(false);
        if (isStopped)
            this.time = LocalTime.of(0, 0, 0);
        this.elapsedTime.playFromStart();
    }

    public void stop() {
        if (this.elapsedTime.getStatus().equals(Animation.Status.RUNNING) || this.elapsedTime.getStatus().equals(Animation.Status.PAUSED)) {
            this.requestSuspend.setSuspended(true);
            Thread.currentThread().interrupt();
            this.elapsedTime.stop();
            this.isStopped = true;
        }
    }

    public void resume() {
        try {
            this.elapsedTime.play();
            this.requestSuspend.setSuspended(false);
            this.requestSuspend.waitResume();
        } catch (InterruptedException e) {
            this.time = LocalTime.of(0, 0, 0);
            this.elapsedTime.playFromStart();
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
        start();
    }

}
