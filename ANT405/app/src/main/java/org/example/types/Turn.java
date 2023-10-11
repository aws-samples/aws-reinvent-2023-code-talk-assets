// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package org.example.types;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Turn extends Event {
    private Integer turnID;

    @JsonProperty("Turn")
    public Integer getTurnID() {
        return turnID;
    }

    @JsonProperty("Turn")
    public void setTurnID(Integer turnID) {
        this.turnID = turnID;
    }

    @Override
    public String toString() {
        return "Turn{" +
                       "turnID=" + turnID +
                       '}';
    }
}
