package com.Alvaro.models;

public class RequestSuspend {
    private boolean suspended; //false-> hilo está corriendo. true-> hilo parado

    public boolean getSuspend(){
        return this.suspended;
    }

    public synchronized void setSuspended(boolean b){
        this.suspended = b;
        notifyAll();
    }
}
