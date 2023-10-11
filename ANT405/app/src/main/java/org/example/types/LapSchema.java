// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package org.example.types;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class LapSchema implements DeserializationSchema<Lap> {
    public LapSchema() {

    }

    @Override
    public Lap deserialize(byte[] message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(message, Lap.class);
    }

    @Override
    public boolean isEndOfStream(Lap nextElement) {
        return false;
    }

    @Override
    public TypeInformation<Lap> getProducedType() {
        return TypeInformation.of(Lap.class);
    }
}
