# Build the project
run  
`mvn clean install`  


# Build the Docker container
run  
`docker build -t sample-with-env-variables .`  

`docker tag sample-with-env-variables:latest <account-id>.dkr.ecr.<region>.amazonaws.com/sample-with-env-variables:latest`  


# Upload the Docker container to Amazon ECR 
run  
`$(aws ecr get-login --no-include-email --region eu-central-1)`  
`docker push <account-id>.dkr.ecr.<region>.amazonaws.com/sample-with-env-variables:latest`  

# Run your Docker container locally with your AWS credentials and env variables provided
for each plain-java application run  

`docker run -it --rm -e amazonMQ.userName=<user> -e amazonMQ.password=<password> -e amazonMQ.brokerURL=<brokerURL> -e websphereMQ.userName=<user> -e websphereMQ.password=<password> -e websphereMQ.hostName=<hostName> -e websphereMQ.queueManager=<queueManager> -e websphereMQ.channel=<channel> sample-with-nativemq-mapping` .