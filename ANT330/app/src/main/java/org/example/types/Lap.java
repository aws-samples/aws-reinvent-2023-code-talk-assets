// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package org.example.types;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Lap extends Event {
    private Integer currentLapNum;

    @JsonProperty("CurrentLapNum")
    public Integer getCurrentLapNum() {
        return currentLapNum;
    }

    @JsonProperty("CurrentLapNum")
    public void setCurrentLapNum(Integer currentLapNum) {
        this.currentLapNum = currentLapNum;
    }

    @Override
    public String toString() {
        return "Lap{" +
                       "currentLapNum=" + currentLapNum +
                       '}';
    }
}
