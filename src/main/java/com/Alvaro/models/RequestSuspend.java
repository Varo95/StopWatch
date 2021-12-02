package com.Alvaro.models;

import javax.swing.plaf.synth.SynthOptionPaneUI;

public class RequestSuspend {
    private boolean suspended; //false-> hilo está corriendo. true-> hilo parado

    public boolean getSuspend(){
        return this.suspended;
    }

    public synchronized void setSuspended(boolean b){
        this.suspended = b;
        notifyAll();
    }
    public synchronized void waitResume(){
        while(this.suspended){
            try{
                wait();
            }catch (InterruptedException e){

            }
        }
    }
}
