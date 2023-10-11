#!/bin/bash

set -e

if [ -z $STACK_PREFIX ]; then
    STACK_PREFIX="csa-v1"
fi

STACK_CORE_FUNCTIONS="${STACK_PREFIX}-core-functions"
STACK_CORE_BUCKETS="${STACK_PREFIX}-core-buckets"
STACK_CORE_VPC="${STACK_PREFIX}-core-vpc"
STACK_CORE_SGS="${STACK_PREFIX}-core-security-groups"
STACK_APP_INFRA="${STACK_PREFIX}-app-infra"
STACK_DEV_INSTANCE="${STACK_PREFIX}-dev-instance"
STACK_MSF_APP="${STACK_PREFIX}-app"

mvn clean package
DEPLOYMENT_ARTIFACTS_BUCKET=$(aws cloudformation describe-stacks --stack-name "${STACK_PREFIX}-core-buckets" | jq -r -c '.Stacks[0].Outputs[] | select(.OutputKey == "DeploymentArtifactsBucket") | .OutputValue')
aws s3 cp target/msf-demo-1.0-SNAPSHOT.jar s3://$DEPLOYMENT_ARTIFACTS_BUCKET/flink/msf-demo-1.0-SNAPSHOT.jar
aws cloudformation deploy --stack-name "${STACK_PREFIX}-app" --parameter-overrides "JarVersion=1.0-SNAPSHOT" "CoreBucketsStackName=${STACK_CORE_BUCKETS}" "SecurityGroupsStackName=${STACK_CORE_SGS}" "VPCStackName=${STACK_CORE_VPC}" "ApplicationInfrastructureStackName=${STACK_APP_INFRA}" --capabilities CAPABILITY_IAM --template-file ./stack.yml
