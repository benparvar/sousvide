package com.benparvar.sousvide.model;
/**
 * Created by alans on 19/05/17.
 */

public class InputTO {
    private String address;
    private Double targetTemperature;
    private Long targetTimer;

    public InputTO(String address, Double targetTemperature, Long targetTimer) {
        this.address = address;
        this.targetTemperature = targetTemperature;
        this.targetTimer = targetTimer;
    }

    public String getAddress() {
        return address;
    }

    public Double getTargetTemperature() {
        return targetTemperature;
    }

    public Long getTargetTimer() {
        return targetTimer;
    }

    @Override
    public String toString() {
        return new org.apache.commons.lang3.builder.ToStringBuilder(this)
                .append("address", address)
                .append("targetTemperature", targetTemperature)
                .append("targetTimer", targetTimer)
                .toString();
    }
}
