#!/bin/bash -ex
exec > >(tee /var/log/user-data.log|logger -t user-data -s 2>/dev/console) 2>&1

DOWNLOADS_DIR="/home/ec2-user/downloads"
BOOTSTRAP_SERVER=$(aws ssm get-parameter --region $AWS_REGION --name "${STACK_NAME}-msk-cluster-bootstrap-broker-string" --query  "Parameter.Value" --output text)
aws s3 cp s3://$DEPLOYMENT_ARTIFACTS_BUCKET/test-data/telemetry.json "$DOWNLOADS_DIR/telemetry.json"
aws s3 cp s3://$DEPLOYMENT_ARTIFACTS_BUCKET/test-data/laps.json "$DOWNLOADS_DIR/laps.json"
aws s3 cp s3://$DEPLOYMENT_ARTIFACTS_BUCKET/test-data/turns.json "$DOWNLOADS_DIR/turns.json"

echo "INFO: Deleting existing test topics"
kafka-topics.sh --bootstrap-server $BOOTSTRAP_SERVER --delete --topic telemetry || true
kafka-topics.sh --bootstrap-server $BOOTSTRAP_SERVER --delete --topic laps || true
kafka-topics.sh --bootstrap-server $BOOTSTRAP_SERVER --delete --topic turns || true

echo "INFO: Creating test topics"
kafka-topics.sh --bootstrap-server $BOOTSTRAP_SERVER --create --topic telemetry || true
kafka-topics.sh --bootstrap-server $BOOTSTRAP_SERVER --create --topic laps || true
kafka-topics.sh --bootstrap-server $BOOTSTRAP_SERVER --create --topic turns || true

echo "INFO: Loading telemetry data"
kafka-console-producer.sh --bootstrap-server $BOOTSTRAP_SERVER --topic telemetry < "$DOWNLOADS_DIR/telemetry.json"
kafka-console-producer.sh --bootstrap-server $BOOTSTRAP_SERVER --topic laps < "$DOWNLOADS_DIR/laps.json"
kafka-console-producer.sh --bootstrap-server $BOOTSTRAP_SERVER --topic turns < "$DOWNLOADS_DIR/turns.json"

echo "INFO: Test data load completed"
