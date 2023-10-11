#!/bin/bash -ex
exec > >(tee /var/log/user-data.log|logger -t user-data -s 2>/dev/console) 2>&1
# put your script here
sudo yum update -y
sudo yum install -y nc

mkdir -p /home/ec2-user/downloads

# git
yum install git -y

# go
curl -L -o /home/ec2-user/downloads/go1.19.1.linux-amd64.tar.gz https://go.dev/dl/go1.19.1.linux-amd64.tar.gz 
rm -rf /usr/local/go && tar -C /usr/local -xzf /home/ec2-user/downloads/go1.19.1.linux-amd64.tar.gz
echo "export PATH=\$PATH:/usr/local/go/bin" >> /home/ec2-user/.bash_profile

# java
sudo amazon-linux-extras install java-openjdk11 -y

# python
sudo amazon-linux-extras install python3.8 -y
echo "alias python=python3" >> /home/ec2-user/.bash_profile

# kafka
mkdir -p /usr/local/kafka
curl -L -o /home/ec2-user/downloads/kafka.tgz https://downloads.apache.org/kafka/3.4.1/kafka_2.13-3.4.1.tgz
tar -C /usr/local/kafka --strip-components=1 -xzf /home/ec2-user/downloads/kafka.tgz
echo "export PATH=\$PATH:/usr/local/kafka/bin" >> /home/ec2-user/.bash_profile

# kafka IAM auth lib
curl -L -o /usr/local/kafka/libs/aws-msk-iam-auth-1.1.1-all.jar https://github.com/aws/aws-msk-iam-auth/releases/download/v1.1.1/aws-msk-iam-auth-1.1.1-all.jar
cat << EOF > /usr/local/kafka/config/iam-auth.properties
security.protocol=SASL_SSL
sasl.mechanism=AWS_MSK_IAM
sasl.jaas.config=software.amazon.msk.auth.iam.IAMLoginModule required;
sasl.client.callback.handler.class=software.amazon.msk.auth.iam.IAMClientCallbackHandler
EOF

# config
sudo chown ec2-user:ec2-user /home/ec2-user/downloads

# docker
sudo amazon-linux-extras install docker -y

# cfn-signal
yum install python2-pip -y
python2 -m pip install https://s3.amazonaws.com/cloudformation-examples/aws-cfn-bootstrap-latest.tar.gz
