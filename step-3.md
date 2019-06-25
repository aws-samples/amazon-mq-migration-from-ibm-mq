# Step 3: Set-up the JMS bridge sample services

In this step you will compile, package, dockerize and upload the JMS bridge samples, we have prepared for you.

### 1. Create the Amazon ECR repositories which will host our Docker images

> To be able to run this step, it's required to have the [AWS CLI](https://aws.amazon.com/cli/) installed! If you haven't it installed yet, you can also use the [Amazon ECR console](https://docs.aws.amazon.com/AmazonECR/latest/userguide/repository-create.html) to create the repository.

First, we will create the Amazon ECR repositories which will host our Docker images. You can look up these images later [here](https://console.aws.amazon.com/ecs/home?#/repositories):

``` bash
$(aws ecr get-login --no-include-email --region <region>)

aws ecr create-repository \
    --repository-name amazon-mq-to-websphere-mq-bridge/sample-with-env-variables

aws ecr create-repository \
    --repository-name amazon-mq-to-websphere-mq-bridge/sample-with-aws-ssm

aws ecr create-repository \
    --repository-name amazon-mq-to-websphere-mq-bridge/sample-with-nativemq-mapping

# and our load generator services
aws ecr create-repository \
    --repository-name amazon-mq-to-websphere-mq-bridge/load-generator
```

### 2. Compile, package, docerize and upload the samples

> To be able to run this step, it's required to have Java 8 (or later) and Apache Maven installed!

In this step we are using Apache Maven, to automaticelly achieve to following per sample application:
- compile the Java based sample application
- package the application in a self-contained uber-JAR
- create a Docker image which contains the sample
- upload this image to Amazon ECR, a private Docker repository

Next, run the following command:

``` bash
aws ecr get-login --no-include-email
```

It will return an output like the following, where you have to look up the password for the basic Auth against Amazon ECR (followed by the -p):

``` bash
docker login -u AWS -p eyJwY...1MX0= https://xxxxxxxxxxxx.dkr.ecr.eu-central-1.amazonaws.com
```

Now, we have to provide a few configuration parameter to Maven. This is done, by creating (if not present) or extending the Maven settings.xml configuration file, which is located at ~/.m2/settings.xml. It has to have the following configuration entries:

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<settings>
    <profiles>
        <profile>
            <id>default</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <!-- configure the AWS account id for your account -->
                <aws-account-id>xxxxxxxxxxxx</aws-account-id>
            </properties>
        </profile>
    </profiles>

    <servers>
        <!-- Maven is using these configurations for basic Auth to push your image to Amazon ECR -->
        <server>
            <!-- chose the region your are using. I'm using eu-central-1 (Frankfurt) -->
            <id>xxxxxxxxxxxx.dkr.ecr.eu-central-1.amazonaws.com</id>
            <username>AWS</username>
            <!-- The password you were looking up by running 'aws ecr get-login --no-include-email'. This password is temporary and you have to update it once a while -->
            <password>eyJw...zE5NH0=</password>
        </server>
    </servers>
</settings>
```

Now, in the root directory of this project, run the following command to do all the 4 steps we mentioned before:  

``` bash
mvn clean deploy
```

After a successful run, you should see a console output like this:

``` bash
[INFO] amazon-mq-to-websphere-mq-bridge 1.0.0-SNAPSHOT .... SUCCESS [  0.752 s]
[INFO] sample-with-env-variables .......................... SUCCESS [02:20 min]
[INFO] sample-with-aws-ssm ................................ SUCCESS [02:11 min]
[INFO] sample-with-nativemq-mapping ....................... SUCCESS [04:24 min]
[INFO] load-generator 1.0.0-SNAPSHOT ...................... SUCCESS [03:30 min]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

# Completion

Congratulations, you've successfully completed step 3! You can move on to [Step 4: Deploy the sample service of your choice](/step-4.md)

[Return the the sample landing page](/README.md)