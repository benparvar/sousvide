package com.benparvar.sousvide.entity;

/**
 * Created by alans on 06/04/2017.
 */

public class Temperature {
    private Double current;
    private Double target;

    public Temperature() {
        super();
    }

    public Temperature(Double current, Double target) {
        super();
        this.current = current;
        this.target = target;
    }

    public Double getCurrent() {
        return current;
    }

    public void setCurrent(Double current) {
        this.current = current;
    }

    public Double getTarget() {
        return target;
    }

    public void setTarget(Double target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "Temperature{" +
                "current=" + current +
                ", target=" + target +
                '}';
    }
}
