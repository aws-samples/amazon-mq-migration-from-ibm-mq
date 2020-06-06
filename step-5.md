# Step 5: Generate load to see auto-scaling in action

### 1. Deploy the Amazon MQ load generator service.
In this step we are deploying a load generator which is sending messages to 'DEV.QUEUE.1' in Amazon MQ and to 'DEV.QUEUE.2' in IBM MQ. It increases the load over 6 minutes, until it reached the maximum and continues with sending messages until you stop the load generator:

``` bash
cd load-generator

aws cloudformation create-stack \
    --stack-name amazon-mq-load-generator \
    --template-body file://load-generator.yaml \
    --capabilities CAPABILITY_IAM \
    --parameters ParameterKey=IBMMQBrokerHost,ParameterValue=<IBMMQBrokerHost>

aws cloudformation wait stack-create-complete \
    --stack-name amazon-mq-load-generator
```

You can pause the load generator, by updating the desired task count for the 'load-generator-service' in ECS and (set it to 0)[https://console.aws.amazon.com/ecs/home?#/clusters/load-generator-cluster/services/load-generator-service/details].


You can stop and delete the load generator by deleting the CloudFormation stack: 

``` bash
aws cloudformation delete-stack \
    --stack-name amazon-mq-load-generator
```


# Completion

Congratulations, you've successfully completed step 5! You can move on to [Step 6: Benchmarking Amazon MQ broker](/step-6.md)

[Return the the sample landing page](/README.md)