FROM amazonlinux:2018.03

# Install dependencies
RUN yum install -y java-1.8.0 \
    && yum clean all

ENV JAVA_OPTS="-Xmx4g"
ENTRYPOINT ["java", "-jar", "app.jar"]

# Provision the Proxy
VOLUME /tmp

ADD target/sample-with-amq-consumer.jar app.jar