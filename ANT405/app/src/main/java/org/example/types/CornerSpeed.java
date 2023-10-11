// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package org.example.types;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

public class CornerSpeed {
    private Integer lapNo;
    private Integer turnID;
    private Integer speed;
    private Double throttle;
    private Double brake;
    private Long timestamp;

    public CornerSpeed() {
    }

    public CornerSpeed(Integer lapNo, Integer turnID, Integer speed, Double throttle, Double brake, Long timestamp) {
        this.lapNo = lapNo;
        this.turnID = turnID;
        this.speed = speed;
        this.throttle = throttle;
        this.brake = brake;
        this.timestamp = timestamp;
    }

    @JsonProperty("LapNo")
    public Integer getLapNo() {
        return lapNo;
    }

    @JsonProperty("LapNo")
    public void setLapNo(Integer lapNo) {
        this.lapNo = lapNo;
    }

    @JsonProperty("TurnID")
    public Integer getTurnID() {
        return turnID;
    }

    @JsonProperty("TurnID")
    public void setTurnID(Integer turnID) {
        this.turnID = turnID;
    }

    @JsonProperty("Speed")
    public Integer getSpeed() {
        return speed;
    }

    @JsonProperty("Speed")
    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    @JsonProperty("Throttle")
    public Double getThrottle() {
        return throttle;
    }

    @JsonProperty("Throttle")
    public void setThrottle(Double throttle) {
        this.throttle = throttle;
    }

    @JsonProperty("Brake")
    public Double getBrake() {
        return brake;
    }

    @JsonProperty("Brake")
    public void setBrake(Double brake) {
        this.brake = brake;
    }

    @JsonProperty("Timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    @JsonProperty("Timestamp")
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "CornerSpeed{" +
                       "lapNo=" + lapNo +
                       ", turnID=" + turnID +
                       ", speed=" + speed +
                       ", throttle=" + throttle +
                       ", brake=" + brake +
                       ", timestamp=" + timestamp +
                       '}';
    }
}
