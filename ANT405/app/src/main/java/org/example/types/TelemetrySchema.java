// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package org.example.types;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class TelemetrySchema implements DeserializationSchema<Telemetry> {
    @Override
    public Telemetry deserialize(byte[] message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(message, Telemetry.class);
    }

    @Override
    public boolean isEndOfStream(Telemetry nextElement) {
        return false;
    }

    @Override
    public TypeInformation<Telemetry> getProducedType() {
        return TypeInformation.of(Telemetry.class);
    }
}
