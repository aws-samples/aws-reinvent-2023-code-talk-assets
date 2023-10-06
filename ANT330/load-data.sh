#!/bin/sh 

set -e

BOOTSTRAP_SERVER=$BOOTSTRAP_SERVER

if [ -z $BOOTSTRAP_SERVER ]; then
    BOOTSTRAP_SERVER="localhost:29092"
fi

echo "INFO: Deleting existing test topics"
kafka-topics.sh --bootstrap-server $BOOTSTRAP_SERVER --delete --topic telemetry || true
kafka-topics.sh --bootstrap-server $BOOTSTRAP_SERVER --delete --topic laps || true
kafka-topics.sh --bootstrap-server $BOOTSTRAP_SERVER --delete --topic turns || true

echo "INFO: Creating test topics"
kafka-topics.sh --bootstrap-server $BOOTSTRAP_SERVER --create --topic telemetry || true
kafka-topics.sh --bootstrap-server $BOOTSTRAP_SERVER --create --topic laps || true
kafka-topics.sh --bootstrap-server $BOOTSTRAP_SERVER --create --topic turns || true

echo "INFO: Loading telemetry data"
kafka-console-producer.sh --bootstrap-server $BOOTSTRAP_SERVER --topic telemetry < datalog/clean/telemetry.json
kafka-console-producer.sh --bootstrap-server $BOOTSTRAP_SERVER --topic laps < datalog/clean/laps.json
kafka-console-producer.sh --bootstrap-server $BOOTSTRAP_SERVER --topic turns < datalog/clean/turns.json

echo "INFO: Setup complete"