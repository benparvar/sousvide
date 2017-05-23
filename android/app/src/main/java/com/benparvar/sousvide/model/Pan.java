package com.benparvar.sousvide.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by alans on 19/05/17.
 */

public class Pan {
    private String status;
    private Double temperature;
    private Long timer;

    public Pan() {
        this.status = "";
        this.temperature = 0.0;
        this.timer = 0L;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Long getTimer() {
        return timer;
    }

    public void setTimer(Long timer) {
        this.timer = timer;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("status", status)
                .append("temperature", temperature)
                .append("timer", timer)
                .toString();
    }
}
