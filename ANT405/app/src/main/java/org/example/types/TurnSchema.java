// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package org.example.types;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class TurnSchema implements DeserializationSchema<Turn> {
    private transient ObjectMapper objectMapper;

    public TurnSchema() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Turn deserialize(byte[] message) throws IOException {
        this.objectMapper = new ObjectMapper();
        return this.objectMapper.readValue(message, Turn.class);
    }

    @Override
    public boolean isEndOfStream(Turn nextElement) {
        return false;
    }

    @Override
    public TypeInformation<Turn> getProducedType() {
        return TypeInformation.of(Turn.class);
    }
}
