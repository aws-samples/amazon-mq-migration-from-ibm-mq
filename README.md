# JMS Bridge Sample

## Overview

The [JMS Bridge Sample](http://) introduces an architecture which allows your to bridge from your existing on-premises messaging broker (IBM® MQ in our example) to a managed message broker in the cloud ([Amazon MQ](https://aws.amazon.com/amazon-mq/) in our case).  

Quite often, these message brokers are interfaced by many applications and customers struggling how to migrate these applications to the cloud. To reduce the risk, they don't want to migrate in a "big bang" one step scenario, but looking for an architecture which allows a step by migration to move one service after the other to the cloud.  

## Architecture

In this sample, we are setting up an environment as below. To simulate the on-premises message broker, we are running IBM® MQ in [AWS Fargate](https://aws.amazon.com/fargate/). For the managed message broker in the cloud, we are obviously using [Amazon MQ](https://aws.amazon.com/amazon-mq/). To run the JMS bridge sample to exchange messages between both message broker, we are also using [AWS Fargate](https://aws.amazon.com/fargate/) because we don't want to reduce the time as much as possible, to manage an operate the solution.

![JMS Bridge Sample architecture](/images/architecture.png)

## Go Build!

To build and run this example, you have to follow 6 steps we will discuss in detail.

* **[Step 1: Deploy the on-premises broker](/step-1.md)** - Here, we are creating a Docker image which contains the IBM® MQ broker to have an easy way to run this broker in AWS as [Amazon ECS](https://aws.amazon.com/ecs/) task, managed by AWS Fargate.

* **[Step 2: Deploy the Amazon MQ broker](/step-2.md)** - Here, we are provisioning a managed [Amazon MQ](https://aws.amazon.com/amazon-mq/) broker, we are using during this demo.

* **[Step 3: Set-up the JMS bridge sample services](/step-3.md)** - Here, we are creating Docker images for all three JMS bridge sample services and uploading these to [Amazon ECR](https://aws.amazon.com/ecr/), our managed private image repository. This gives as an easy way to run these applications in step 4.

* **[Step 4: Deploy the sample service of your choice](/step-4.md)** - Here, we are deploying one of the three JMS bridge samples, we have prepared for you.

* **[Step 5: Generate load to see auto-scaling in action](/step-5.md)** - Here, we are generating load to verify, how auto-scaling kicks in and scales our JMS bridge.

* **[Step 6: Run Amazon MQ benchmark tests](/step-6.md)** - Here, we will be deploying several producers and consumers and run a benchmark tool.

* **[Step 7: Terminate and delete all resources](/step-7.md)** - Here, we are deleting the resources we created so that no additional cost occurs.

