# Step 2: Deploy the broker infrastructure via AWS CloudFormation

In this step you will deply the entire sample via [AWS CloudFormation](https://aws.amazon.com/cloudformation/).

### 1. Select your region and launch the AWS CloudFormation template.

> To be able to run this step, it's required to have the [AWS CLI](https://aws.amazon.com/cli/) installed!

Run the first command to launch the AWS CloudFormation template. The second command will wait, until the AWS CloudFormation stack was launched successfuly and ready to use. Alternatively, you can also open your CloudFormation console and watch the resource creation process. It takes ~ 15 minutes to complete:

```bash
aws cloudformation create-stack \
    --stack-name amazon-mq-broker \
    --template-body file://amazon-mq-broker.yaml

aws cloudformation wait stack-create-complete \
    --stack-name amazon-mq-broker
```

### 2. Login to the Amazon MQ Console / Active MQ Console.

Open a new tab and got to your [Amazon MQ broker console](https://console.aws.amazon.com/amazon-mq/home?#/brokers/) and click on the broker with the name **AmazonMQBroker**. In the **Connections** section, figure out which of the both **ActiveMQ Web Console** links are active. To access the web console, provide the Amazon MQ broker user and password. If you don't have provided a specific value, use the following ones:  
* User: AmazonMQBrokerUserName
* Password: AmazonMQBrokerPassword

You can use this web console to read messages from the broker and write messages to it.

# Completion

Congratulations, you've successfully completed step 2! You can move on to [Step 3: Set-up the JMS bridge sample services](/step-3.md)

[Return the the sample landing page](/README.md)