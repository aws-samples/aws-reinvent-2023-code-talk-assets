// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package org.example;

import com.amazonaws.services.kinesisanalytics.runtime.KinesisAnalyticsRuntime;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.opensearch.sink.OpensearchEmitter;
import org.apache.flink.connector.opensearch.sink.OpensearchSink;
import org.apache.flink.connector.opensearch.sink.OpensearchSinkBuilder;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.http.HttpHost;
import org.example.types.*;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.Requests;

import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DataStreamJob {

    private static String PROPERTY_KAFKA_BOOTSTRAP_SERVERS = "KAFKA_BOOTSTRAP_SERVERS";
    private static String PROPERTY_OPEN_SEARCH_HOST = "OPEN_SEARCH_HOST";
    private static String PROPERTY_OPEN_SEARCH_PORT = "OPEN_SEARCH_PORT";
    private static String PROPERTY_OPEN_SEARCH_SCHEME = "OPEN_SEARCH_SCHEME";
    private static String PROPERTY_OPEN_SEARCH_USERNAME = "OPEN_SEARCH_USERNAME";
    private static String PROPERTY_OPEN_SEARCH_PASSWORD = "OPEN_SEARCH_PASSWORD";

    private static String DEFAULT_KAFKA_BOOTSTRAP_SERVERS = "localhost:29092";
    private static String DEFAULT_OPENSEARCH_HOST = "localhost";
    private static String DEFAULT_OPENSEARCH_PORT = "9200";
    private static String DEFAULT_OPENSEARCH_SCHEME = "http";

    public static Properties getConfig(StreamExecutionEnvironment env) throws IOException {
        if (env instanceof LocalStreamEnvironment) {
            return new Properties();
        }
        return KinesisAnalyticsRuntime.getApplicationProperties().get("FlinkApplicationProperties");
    }

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        if (env instanceof LocalStreamEnvironment) {
            env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
            env.enableCheckpointing(5000);
            env.setParallelism(1);
        }

        final Properties config = getConfig(env);

        final String kafkaBootstrapServers =
                (String) config.getOrDefault(PROPERTY_KAFKA_BOOTSTRAP_SERVERS, DEFAULT_KAFKA_BOOTSTRAP_SERVERS);

        final KafkaSource<Turn> turnsTopic =
                KafkaSource.<Turn>builder()
                        .setBootstrapServers(kafkaBootstrapServers)
                        .setTopics("turns")
                        .setStartingOffsets(OffsetsInitializer.earliest())
                        .setValueOnlyDeserializer(new TurnSchema())
                        .build();

        final KafkaSource<Lap> lapsTopic =
                KafkaSource.<Lap>builder()
                        .setBootstrapServers(kafkaBootstrapServers)
                        .setTopics("laps")
                        .setStartingOffsets(OffsetsInitializer.earliest())
                        .setValueOnlyDeserializer(new LapSchema())
                        .build();

        final KafkaSource<Telemetry> telemetryTopic =
                KafkaSource.<Telemetry>builder()
                        .setBootstrapServers(kafkaBootstrapServers)
                        .setTopics("telemetry")
                        .setStartingOffsets(OffsetsInitializer.earliest())
                        .setValueOnlyDeserializer(new TelemetrySchema())
                        .build();

        final DataStream<Turn> turns =
                env.fromSource(
                        turnsTopic,
                        WatermarkStrategy
                                .<Turn>forBoundedOutOfOrderness(Duration.ofSeconds(30))
                                .withTimestampAssigner((element, recordTimestamp) -> element.getTimestamp())
                                .withIdleness(Duration.ofSeconds(30)),
                        "turns"
                );

        final DataStream<Lap> laps =
                env.fromSource(
                        lapsTopic,
                        WatermarkStrategy
                                .<Lap>forBoundedOutOfOrderness(Duration.ofSeconds(30))
                                .withTimestampAssigner((element, recordTimestamp) -> element.getTimestamp())
                                .withIdleness(Duration.ofSeconds(30)),
                        "laps"
                );

        final DataStream<Telemetry> telemetry =
                env.fromSource(
                        telemetryTopic,
                        WatermarkStrategy
                                .<Telemetry>forBoundedOutOfOrderness(Duration.ofSeconds(30))
                                .withTimestampAssigner((element, recordTimestamp) -> element.getTimestamp())
                                .withIdleness(Duration.ofSeconds(30)),
                        "telemetry"
                );

        final DataStream<CornerSpeed> cornerSpeed =
                turns.join(telemetry)
                        .where((KeySelector<Turn, Integer>) value -> value.getCorrelationID())
                        .equalTo(new KeySelector<Telemetry, Integer>() {
                            @Override
                            public Integer getKey(Telemetry value) throws Exception {
                                return value.getCorrelationID();
                            }
                        })
                        .window(TumblingEventTimeWindows.of(Time.seconds(1)))
                        .allowedLateness(Time.minutes(15))
                        .apply(new JoinFunction<Turn, Telemetry, Tuple2<Turn, Telemetry>>() {
                            @Override
                            public Tuple2 join(Turn first, Telemetry second) throws Exception {
                                return Tuple2.of(first, second);
                            }
                        })
                        .join(laps)
                        .where((KeySelector<Tuple2<Turn, Telemetry>, Integer>) value -> value.f0.getCorrelationID())
                        .equalTo(new KeySelector<Lap, Integer>() {
                            @Override
                            public Integer getKey(Lap value) throws Exception {
                                return value.getCorrelationID();
                            }
                        })
                        .window(TumblingEventTimeWindows.of(Time.seconds(1)))
                        .allowedLateness(Time.minutes(15))
                        .apply((JoinFunction<Tuple2<Turn, Telemetry>, Lap, CornerSpeed>) (first, second) -> {
                            Turn turn = first.f0;
                            Telemetry telem = first.f1;
                            Lap lap = second;
                            return new CornerSpeed(
                                    lap.getCurrentLapNum(),
                                    turn.getTurnID(), telem.getSpeed(),
                                    telem.getThrottle(),
                                    telem.getBrake(),
                                    telem.getTimestamp());
                        });


        final HttpHost openSearchHost = new HttpHost(
                (String) config.getOrDefault(PROPERTY_OPEN_SEARCH_HOST, DEFAULT_OPENSEARCH_HOST),
                Integer.valueOf((String) config.getOrDefault(PROPERTY_OPEN_SEARCH_PORT, DEFAULT_OPENSEARCH_PORT)),
                (String) config.getOrDefault(PROPERTY_OPEN_SEARCH_SCHEME, DEFAULT_OPENSEARCH_SCHEME)
        );
        OpensearchSinkBuilder<CornerSpeed> sinkBuilder =
                new OpensearchSinkBuilder<CornerSpeed>()
                        .setHosts(openSearchHost)
                        .setEmitter((OpensearchEmitter<CornerSpeed>) (cornerSpeed1, context, requestIndexer) -> {
                            Map<String, Object> document = new HashMap<>();
                            document.put("turnId", cornerSpeed1.getTurnID());
                            document.put("lapNo", cornerSpeed1.getLapNo());
                            document.put("speed", cornerSpeed1.getSpeed());
                            document.put("throttle", cornerSpeed1.getThrottle());
                            document.put("brake", cornerSpeed1.getBrake());
                            document.put("timestamp", new Date(cornerSpeed1.getTimestamp()));

                            IndexRequest request = Requests.indexRequest()
                                                           .index("corner-speed-analysis")
                                                           .id(cornerSpeed1.getTimestamp().toString())
                                                           .source(document);

                            requestIndexer.add(request);
                        });

        final String username = (String) config.getOrDefault(PROPERTY_OPEN_SEARCH_USERNAME, "");
        if (!username.equals("")) {
            sinkBuilder = sinkBuilder
                                  .setConnectionUsername(username)
                                  .setConnectionPassword((String) config.get(PROPERTY_OPEN_SEARCH_PASSWORD))
                                  .setAllowInsecure(true);
        }

        final OpensearchSink<CornerSpeed> sink = sinkBuilder.build();
        cornerSpeed.sinkTo(sink);

        env.execute("Corner speed analysis");
    }
}
