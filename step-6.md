# Step 6: Terminate and delete all resources

### 1. To delete the **load-generator**, terminate the corresponding CloudFormation stack: 

``` bash
aws cloudformation delete-stack \
    --stack-name amazon-mq-load-generator
```

### 2. To delete the **sample-with-nativemq-mapping**, terminate the corresponding CloudFormation stack: 

``` bash
aws cloudformation delete-stack \
    --stack-name sample-with-nativemq-mapping
```

### 3. To delete the **sample-with-aws-ssm**, terminate the corresponding CloudFormation stack: 

``` bash
aws cloudformation delete-stack \
    --stack-name sample-with-aws-ssm
```

### 4. To delete the **sample-with-env-variables**, terminate the corresponding CloudFormation stack: 

``` bash
aws cloudformation delete-stack \
    --stack-name sample-with-env-variables
```

### 5. To delete the **amazon-mq-broker**, terminate the corresponding CloudFormation stack: 

``` bash
aws cloudformation delete-stack \
    --stack-name amazon-mq-broker
```

### 6. To delete the **ibm-mq-broker**, terminate the corresponding CloudFormation stack: 

``` bash
aws cloudformation delete-stack \
    --stack-name ibm-mq-broker
```


# Completion

Congratulations, you've successfully completed step 6! This is the last step in the workshop.

[Return the the sample landing page](/README.md)