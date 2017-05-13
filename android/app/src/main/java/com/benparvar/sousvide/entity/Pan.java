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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Pan{" +
                "address='" + address + '\'' +
                ", working=" + working +
                ", temperature=" + temperature +
                ", timer=" + timer +
                '}';
    }
}
