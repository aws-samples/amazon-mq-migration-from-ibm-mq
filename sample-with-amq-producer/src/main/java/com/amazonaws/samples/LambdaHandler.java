package com.amazonaws.samples;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.amazonaws.serverless.proxy.jersey.JerseyLambdaContainerHandler;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * This is the main Lambda handler class. It implements the RequestStreamHandler interface from the lambda-core package (see
 * maven dependencies). The only method defined by the RequestStreamHandler interface is the handleRequest method implemented.
 * 
 */
public class LambdaHandler implements RequestStreamHandler {
    // initialize the jersey application. Load the resource classes from the com.amazonaws.lab.resources
    // package and register Jackson as our JSON serializer.
    private static final ResourceConfig jerseyApplication = new ResourceConfig()
            // for this sample, we are configuring Jersey to pick up all resource classes
            // in the com.amazonaws.lab.resources package. To speed up start time, you 
            // could register the individual classes.
            .packages("com.amazonaws.samples")
            .register(JacksonFeature.class)
            .register(UncaughtException.class);

    // Initialize the serverless-java-container library as a Jersey proxy
    private static final JerseyLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler
            = JerseyLambdaContainerHandler.getAwsProxyHandler(jerseyApplication);
    
    private static AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

    // Main entry point of the Lambda function, uses the serverless-java-container initialized in the global scope
    // to proxy requests to our jersey application
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) 
            throws IOException {
    	
        handler.proxyStream(inputStream, outputStream, context);

        // just in case it wasn't closed by the mapper
        outputStream.close();
    }

    public static AmazonS3 getS3Client() {
        return s3Client;
    }
}