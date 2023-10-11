// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package org.example.types;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

public class Event {
    private Integer correlationID;
    private Long timestamp;

    @JsonProperty("CorrelationID")
    public Integer getCorrelationID() {
        return correlationID;
    }

    @JsonProperty("CorrelationID")
    public void setCorrelationID(Integer correlationID) {
        this.correlationID = correlationID;
    }

    @JsonProperty("Timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    @JsonProperty("Timestamp")
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
