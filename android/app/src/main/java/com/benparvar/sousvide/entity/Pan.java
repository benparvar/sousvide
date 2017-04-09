package com.benparvar.sousvide.entity;

/**
 * Created by alans on 06/04/2017.
 */

public class Pan {
    private String address;
    private Boolean working;
    private Temperature temperature;
    private Timer timer;

    public Pan() {
        super();
    }

    public Pan(String address, Temperature temperature, Timer timer, Boolean working) {
        super();
        this.address = address;
        this.temperature = temperature;
        this.timer = timer;
        this.working = working;
    }
}
