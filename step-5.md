# Step 4: Generate load to see auto-scaling in action

### 1. Deploy the Amazon MQ load generator service.
In this step ...:

``` bash
cd load-generator

aws cloudformation create-stack \
    --stack-name load-generator \
    --template-body file://load-generator.yaml \
    --capabilities CAPABILITY_IAM \
    --parameters ParameterKey=IBMMQBrokerHost,ParameterValue=<IBMMQBrokerHost>

aws cloudformation wait stack-create-complete \
    --stack-name amazon-mq-load-generator
```

TODO



# Completion

Congratulations, you've successfully completed step 5! This is the last step in the workshop.

[Return the the sample landing page](/README.md)