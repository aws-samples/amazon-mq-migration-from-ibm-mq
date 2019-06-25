# Step 4: Deploy one of the three sample services

### 1. Choose the sample which we will deploy.
In this step you will deploy one of the samples we have prepared for you. Please chose one of the following samples:

* **1. sample-with-env-variables** - The most basic sample which is using environment variables to supply configuration parameters to the JMS bridge.

> Before you can run the following commands, please replace '\<IBMMQBrokerHost\>' with the public IP address of your IBM MQ broker.

``` bash
cd sample-with-env-variables

aws cloudformation create-stack \
    --stack-name sample-with-env-variables \
    --template-body file://sample-with-env-variables.yaml \
    --capabilities CAPABILITY_IAM \
    --parameters ParameterKey=IBMMQBrokerHost,ParameterValue=<IBMMQBrokerHost>

aws cloudformation wait stack-create-complete \
    --stack-name sample-with-env-variables
```

* **2. sample-with-aws-ssm** - In this sample we are using the [AWS Systems Manager Parameter Store](https://aws.amazon.com/systems-manager/features/#Parameter_Store) to store the secrets in a secure manner. The JMS bridge sample application does an secure lookup to retrive the password for the Amazpn MQ broker and the IBM MQ broker at startup time.
This sample expects the parameter '/DEV/JMS-BRIDGE/AMAZONMQ/PASSWORD' and '/DEV/JMS-BRIDGE/IBMMQ/PASSWORD' to be present in the AWS SSM sarameter store. You can use the [AWS Systems Manager Parameter Store console](https://console.aws.amazon.com/systems-manager/parameters/) to create these entries, or simply by running the following commands (replace the <password> with the password you have chosen):

``` bash
aws ssm put-parameter --type SecureString --name '/DEV/JMS-BRIDGE/AMAZONMQ/PASSWORD' --value '<password>'

aws ssm put-parameter --type SecureString --name '/DEV/JMS-BRIDGE/IBMMQ/PASSWORD' --value '<password>'
```

``` bash
cd sample-with-aws-ssm

aws cloudformation create-stack \
    --stack-name sample-with-aws-ssm \
    --template-body file://sample-with-aws-ssm.yaml \
    --capabilities CAPABILITY_IAM \
    --parameters ParameterKey=IBMMQBrokerHost,ParameterValue=<IBMMQBrokerHost>

aws cloudformation wait stack-create-complete \
    --stack-name sample-with-aws-ssm
```

* **3. sample-with-native-mapping** - This sample is demonstrating, how to map native IBM® MQ attributes. This is for example necessary, if your current solutions is using the native IBM protocoll to interact with IBM® MQ and not the JMS API. 

``` bash
cd sample-with-native-mapping

aws cloudformation create-stack \
    --stack-name sample-with-native-mapping \
    --template-body file://sample-with-native-mapping.yaml \
    --capabilities CAPABILITY_IAM \
    --parameters ParameterKey=IBMMQBrokerHost,ParameterValue=<IBMMQBrokerHost>

aws cloudformation wait stack-create-complete \
    --stack-name sample-with-native-mapping
```

### 2. Ingest messages on the Amazon MQ site and listen on the IBM® MQ.

Send messages into the Amazon MQ broker queue **DEV.QUEUE.1** and listen on the IBM® MQ site.  

### 3. Ingest messages on the IBM® MQ site and listen on the Amazon MQ Console.

Send messages into the IBM® MQ broker queue **DEV.QUEUE.2** and listen on the Amazon MQ site.  

# Completion

Congratulations, you've successfully completed step 4! You can move on to [Step 5: Generate load to see auto-scaling in action](/step-5.md)

[Return the the sample landing page](/README.md)