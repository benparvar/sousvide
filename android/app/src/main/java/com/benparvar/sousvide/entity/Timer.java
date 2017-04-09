package com.benparvar.sousvide.entity;

/**
 * Created by alans on 06/04/2017.
 */

public class Timer {
    private Long current;
    private Long target;

    public Timer() {
        super();
    }

    public Timer(Long current, Long target) {
        super();
        this.current = current;
        this.target = target;
    }

    public Long getCurrent() {
        return current;
    }

    public void setCurrent(Long current) {
        this.current = current;
    }

    public Long getTarget() {
        return target;
    }

    public void setTarget(Long target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "Timer{" +
                "current=" + current +
                ", target=" + target +
                '}';
    }
}
