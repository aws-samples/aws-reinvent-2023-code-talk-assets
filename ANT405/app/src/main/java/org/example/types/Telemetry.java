// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package org.example.types;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Telemetry extends Event {
    private Integer speed;
    private Double throttle;
    private Double brake;

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

    @Override
    public String toString() {
        return "Telemetry{" +
                       "speed=" + speed +
                       ", throttle=" + throttle +
                       ", brake=" + brake +
                       '}';
    }
}
