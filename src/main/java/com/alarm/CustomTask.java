package com.alarm;

import java.util.TimerTask;

public class CustomTask extends TimerTask {

    public CustomTask() {

        //Constructor

    }

    public void run() {
        try {

            System.out.println("Hello");

        } catch (Exception ex) {
            System.out.println("error running thread " + ex.getMessage());
        }
    }
}
