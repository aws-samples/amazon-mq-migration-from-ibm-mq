# (Optional) Step 6 : Benchmarking Amazon MQ broker

**This section assumes that you have executed [Step 3](/step-3.md) to install some of the pre-requisites like maven and docker. Please go back to step 3 if you have not installed the pre-requisites. It also needs jdk 1.8. Please upgrade your environment in case you are at a lower jdk version**

1. ### Overview of the architecture

   In this section we will deploy a set of Amazon MQ senders and receivers. The sender and receiver threads are created using the JmsTools framework. The details for this framework can be found [here](https://github.com/mithun008/JmsTools). The Jmstools package is wrapped as a bean in a camel container. The tests would be triggered by sending a message to a topic with details of the test to run. Jmstools provides several configurable options to initiate test for a duration or for a certain no of messages, various message sizes and even various protocols. In this section we will only focus on Openwire but it can be changed for AMQP as well.

   ![Benchmark Architecture](/images/benchmark-arch.png)

   We will first deploy the receiver image and then the sender image. The tests can be repeated as many times as needed for various configurations.

1. ### Setting up Jmstools

   We will first download Jmstools from github and build it so that it can used as a dependency in our producer and consumer processes. The first step is to download the code. Go to the root directory of the workshop and run the following commandd.

   ```bash
   git clone https://github.com/mithun008/JmsTools.git

   cd JmsTools

   mvn clean package
   ```

   The maven build created several jar files under the shaded-jars file. Run the following command to include the Apache Active MQ JMS related jars in maven repository.

   ```bash
   mvn install:install-file -Dfile=./shaded-jars/AmqJmsConsumer.jar -DgroupId=name.wramner.jmstools.consumer -DartifactId=jmstools -Dversion=1.10 -Dpackaging=jar

   mvn install:install-file -Dfile=./shaded-jars/AmqJmsProducer.jar -DgroupId=name.wramner.jmstools.producer -DartifactId=jmstools -Dversion=1.10 -Dpackaging=jar

   ```

   We will use the AmqJsmProducer and AmqJmsConsumer jar file as part of test framework. Run the following commands.

1. ### Deploy the Amazon MQ producer container

   In this section we will deploy a camel conatiner which will trigger a message sender with several threads sending messages at the same time. The number of threads can be controlled by a trigger file which is present under sample-with-amq-producer/src/main/resources/trigger.json. The json file has settings which indicate the min message size,max message size, no of threads, the duration in mins to run the producer. It also has the credentials and url to connect to the broker.

   The test tool is based on the JmsTools utility. More information can be found [here](https://github.com/mithun008/JmsTools).

   We will first create a set a Amazon MQ producer ECS service which will have several producer threads. The producer is initiated by sending a trigger message to a notify topic.


    Create a S3 bucket to upload the code package that will be used to run the producers. It also requires a new ECR repository to host the docker container for producer.

    ``` bash
    aws s3 mb s3://<<CODE_PACKAGE_BUCKET>>

    aws ecr create-repository  --repository-name amazon-mq-migration-from-ibm-mq/sample-with-amq-producer

    ```

    We will now create the deployment package using SAM(Serverless Access Model) template to upload the code and provision the resources. SAM makes it easy to deploy serverless resources like API gateway and lamndda functions. More information about SAM can be found [here](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-template-basics.html).The template only deploys one instance of the container but you can add additional instances of the container by modifying the ECS [TaskDefintion](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-ecs-service.html#cfn-ecs-service-desiredcount).You can easily go upto 50 producer threads on a single instance of the container. If you need more than that you can increase the desired count. **The thread count specified in the trigger message would then apply to each of the container.** Go back to your root directory and run the following command.

    ``` bash
    cd sample-with-amq-producer/
    ```
    In the next step, we will build the source code and deploy it as a docker conatiner within ECS.

    ``` bash
    mvn clean deploy
    ```

    Note: The above step may require you to re-enter the auth token for docker login. It can be accessed by doing a ecr login again and copying the auth token to the settings.xml file. The steps are fully documented in [step 3](step-3.md) of the workshop.

    ``` bash
    aws cloudformation create-stack \
        --stack-name sample-with-amq-producer \
        --template-body file://sample-with-amq-producer.yaml \
        --capabilities CAPABILITY_IAM \
        --parameter-overrides MetricWidgetBucket=<<CODE_PACKAGE_BUCKET>> \
        AmazonMQBrokerUserNameRef=<<BROKER_USER_NAME>> \
        AmazonMQBrokerPasswordRef=<<BROKER_PASSWORD>> \
        AmazonMQBrokerURLRef=<<BROKER_END_POINT>>

    aws cloudformation wait stack-create-complete \
    --stack-name sample-with-amq-producer


    ```
    Run the above command to deploy the resources. The command will wait till the deployment is complete. You can check the status of the deployment by navigating to the cloudformation console. **The broker credentials used in this command are for creating the topic on which the producer controller would subscribe and trigger the producer threads. The trigger message will have the credentials for the broker which needs to be performance tested.**


    We have now deployed the container to send messages to a broker. The producer deployment will start a topic listener on the Amazon MQ broker that was specified in the command line of the deplpy command. The name of the topic will be prod.notify. If you have deployed more than one container instances it will show those many topic consumers.

1.  ### Deploy the Amazon MQ consumer container

    In this section we will deploy a camel conatiner which will trigger a message consumer with several threads receiving messages at the same time. The number of threads can be controlled by a trigger file which is present under sample-with-amq-consumer/src/main/resources/trigger.json. The json file has settings which indicate the min message size, max message size, no of threads, the duration in mins to run the producer. It also has the credentials and url to connect to the broker.

    The test tool is based on the JmsTools utility. More information can be found [here](https://github.com/mithun008/JmsTools).


    ``` bash

    aws ecr create-repository  --repository-name amazon-mq-migration-from-ibm-mq/sample-with-amq-consumer
    ```

    We will now create the deployment package using SAM(Serverless Access Model) template to upload the code and provision the resources. SAM makes it easy to deploy serverless resources like API gateway and lamndda functions. More information about SAM can be found [here](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-template-basics.html). Go back to your home directory and run the following commands.

    ``` bash
    cd sample-with-amq-consumer
    ```
    In the next step, we will build the source code and deploy it as a docker conatiner within ECS.

    ``` bash
    mvn clean deploy
    ```

    Note: The above step may require you to re-enter the auth token for docker login. It can be accessed by doing a ecr login again and copying the auth token to the settings.xml file. The steps are fully documented in [step 3](step-3.md) of the workshop.

    ``` bash
    aws cloudformation create-stack \
        --stack-name sample-with-amq-consumer \
        --template-body file://sample-with-amq-producer.yaml \
        --capabilities CAPABILITY_IAM \
        --parameter-overrides MetricWidgetBucket=<<CODE_PACKAGE_BUCKET>> \
        AmazonMQBrokerUserNameRef=<<BROKER_USER_NAME>> \
        AmazonMQBrokerPasswordRef=<<BROKER_PASSWORD>> \
        AmazonMQBrokerURLRef=<<BROKER_END_POINT>>

    aws cloudformation wait stack-create-complete \
        --stack-name sample-with-amq-consumer


    ```

    We have now deployed the container to receive messages from a broker. The template only deploys one instance of the container but you can add additional instances of the container by modifying the ECS [TaskDefintion](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-ecs-service.html#cfn-ecs-service-desiredcount.You can easily go upto 50 consumer threads on a single instance of the container. If you need more than that you can increase the desired count. The thread count specified in the trigger message would then apply to each of the container.

    The consumer deployment will start a topic listener on the Amazon MQ broker that was created in step-1 of the workshop. The name of the topic will be cons.notify. If you have deployed more than one container instances it will show those many topic consumers.

    We are now ready to start the test.

1. ### Start the consumer

   Producers and consumers can be started in any order but for this workshop lets start the consumer first. The consumer process can be triggered by sending a trigger message on the **cons.notify** topic. The trigger message is present under sample-with-amq-consumer/src/resources/trigger.json. It contains all the parameters that Jmstools uses to launch its test.

   Edit the trigger.json to set parameters based on your test. Save it once you are done. **The settings on trigger.json refer to the broker which needs to be performance tested.**

   Run the following command to send the message to the notify topic. The parameters(host,user,password and port) used in the command refer to the broker credentials used as part of the producer and consumer deploymnent. Please specify the hostname of the active broker in case you have an active standby broker config. **Please note that the port used is the STOMP transport port.**

   ```bash
   cd sample-with-amq-consumer

   pip install stomp.py

   python init-consumer.py <<broker-user>> \
       <<broker-password>> \
       <<broker-host>> \
       <stomp-port>> \
       src/main/resources/trigger.json \
       topic \
       cons.notify

   ```

   Go to active mq console and you will notice several consumers created to the queue. The no of consumers should be the same as you specified in trigger.json or it will be the container desired count multiplied by the threads on each container.

1. ### Start the producer

   The producer process can be triggered by sending a trigger message on the prod.notify topic. The trigger message is present under sample-with-amq-producer/src/resources/trigger.json. It contains all the parameters that Jmstools uses to launch its test.

   Edit the trigger.json to set parameters based on your test. Save it once you are done. **The settings on trigger.json refer to the broker which needs to be performance tested.**

   Run the following command to send the message to the notify topic. The parameters used in the command are for the notify broker which in our case is the one created in step-2 of the workshpo. **Please note that the port used is the STOMP transport port.**

   ```bash
   cd sample-with-amq-producer

   pip install stomp.py

   python init-producer.py <<broker-user>> \
       <<broker-password>> \
       <<broker-host>> \
       <stomp-port>> \
       src/main/resources/trigger.json \
       topic \
       prod.notify

   ```

   At this time, you will notice several connections created to the broker and the queue(specified in trigger.json) should start enqueing and dequeing messages. The test will run according to the parameters configured in the trigger json file. Once the test completes, you will not see any consumers on the queue or any producets sending messages.

1. ### Capture the results

   As part of the producer and consumer deployment, we also deployed an API gateway and lambda function which queries cloudwatch to get the desired widget with enqueue and dequeue count for a specified period of time. The widget is controlled by configuration file called image-api.template. Its present under src/main/resources folder for each of the samples. You can modify it based on your widget. However, it would need redeployment of the package.

   The cloudwatch widget can be customized based on your specific requirements. The widget used in the worksop shows the enqueue rate and dequeue rate for the test queue. It can be modified to display the CPU stats of the broker or the memory stats of the broker.
   More details about cloudwatch widget can be found [here](https://docs.aws.amazon.com/AmazonCloudWatch/latest/APIReference/API_GetMetricWidgetImage.html).


    You can use either of the two stacks to get the API endpoint. Run the following command to get the API_END_POINT.

    ``` bash
    aws cloudformation describe-stacks \
        --stack-name sample-with-amq-producer \
        --query 'Stacks[].Outputs[?OutputKey==`GetPerformanceWidget`][OutputValue]' \
        --output text
    ```

    Copy the url output from the above command and edit it to replace the broker and queue name placeholder. Please note that the broker names will need to -1 suffix if its the broker that is active or else it will be -2 suffix. The queue name is the name of the queue that was used as part of the test. Paste the edited link on a browser and it will respond back with a link. Click on the link to get the performanc widget graph.

    Here is a sample widget graph showing the enqueue and dequeue rate of the messages during a test. The widget can be customized based on the metrics that are needed to be captured.

    ![PerformanceWidget](/images/metric-widget.png)

# Cleanup

You can stop and delete the sender and receover processes by deleting the CloudFormation stacks.

    aws cloudformation delete-stack \
        --stack-name sample-with-amq-producer

    aws cloudformation delete-stack \
        --stack-name sample-with-amq-consumer

# Completion

Congratulations, you've successfully completed step 6! You can move on to [Step 7: Terminate and delete all resources](/step-7.md)

[Return the the sample landing page](/README.md)
