# Analyse race car telemetry data with Managed Service for Apache Flink

## Prerequisites

This demo requires following applications installed in your development environment
- IntelliJ IDEA
- Docker
- Ensure that docker engine is configured with at least 8GB memory and 8 CPUs (this is required for OpenSearch containers)

## How to run locally

1. Run the following command to setup Kafka and load test data into relevant topics

```
docker-compose up
./load-data.sh
```

2. Open the IntelliJ IDEA project in app directory and start the debugger 

## Deploying to AWS

Deployment of this solution to AWS is broken into two steps. First step is to create infrastructure
required to run this application. It contains resources such as VPC, Security Groups, IAM roles, MSK cluster, Open Search Domain etc. Idea is to configure a VPC with required resources so that the Flink application can be deployed iteratively as we make progress. To create infrastructure resources, run the following command. It make take a few minutes to complete.

```
./create-vpc
```

When the command is successfully executed, you will see two important pieces of information. 
- DNS name of an EC2 instance that you can SSH into for troubleshooting
- OpenSearch Dashboards URL

You can view this information at anytime by running the following command:

```
aws cloudformation describe-stacks --stack-name csa-v1-dev-instance --query "Stacks[0].Outputs"
aws cloudformation describe-stacks --stack-name csa-v1-application-infrastructure --query "Stacks[0].Outputs"
```

Second step is to deploy Corner Speed Analysis Flink job to MSF. Run the following sequence of commands to do that.

```
cd app
./deploy.sh
```

## Cleaning up AWS Account

Once you finish exploring this demo, you can delete all resources by running the following command.

```
./delete-vpc
```

