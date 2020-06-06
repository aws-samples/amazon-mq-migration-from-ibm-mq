package com.amazonaws.samples;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.apache.camel.Exchange;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.GetMetricWidgetImageRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricWidgetImageResult;

public class PerfTestResultsUtil {
	public void process(Exchange exchange) throws Exception {
		String metricsRequest = exchange.getIn().getBody(String.class);

		final AmazonCloudWatch cloudWatch = AmazonCloudWatchClientBuilder.defaultClient();
		GetMetricWidgetImageRequest getMetricWidgetImageRequest = new GetMetricWidgetImageRequest();

		getMetricWidgetImageRequest.setMetricWidget(metricsRequest);
		GetMetricWidgetImageResult getMetricWidgetImageResponse = cloudWatch
				.getMetricWidgetImage(getMetricWidgetImageRequest);

		ByteBuffer imageBytes = getMetricWidgetImageResponse.getMetricWidgetImage().asReadOnlyBuffer();
		byte[] arr = new byte[imageBytes.remaining()];
		imageBytes.get(arr);
		//ByteArrayOutputStream bos = new ByteArrayOutputStream();
		//FileOutputStream out = new FileOutputStream("temp.jpg");
		//out.getChannel().write(imageBytes);
		//out.close();
		exchange.getOut().setBody(arr);
		exchange.getOut().setHeader(Exchange.CONTENT_TYPE,"image/jpg");
		exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, "200");
	}

	public static void main(String []args) throws IOException{
		String metricsRequest = "{\n" + 
				"    \"metrics\": [\n" + 
				"        [ \"AWS/AmazonMQ\", \"EnqueueCount\", \"Broker\", \"Tanla-POC-V3-1\", \"Queue\", \"TEST.FOO12\" ],\n" + 
				"        [ \".\", \"DequeueCount\", \".\", \".\", \".\", \".\" ]\n" + 
				"    ],\n" + 
				"    \"view\": \"timeSeries\",\n" + 
				"    \"stacked\": false,\n" + 
				"    \"stat\": \"Maximum\",\n" + 
				"    \"period\": 60,\n" + 
				"    \"width\": 1704,\n" + 
				"    \"height\": 250,\n" + 
				"    \"start\": \"-PT3H\",\n" + 
				"    \"end\": \"P0D\"\n" + 
				"}";
		final AmazonCloudWatch cloudWatch = AmazonCloudWatchClientBuilder.defaultClient();
		GetMetricWidgetImageRequest getMetricWidgetImageRequest = new GetMetricWidgetImageRequest();

		getMetricWidgetImageRequest.setMetricWidget(metricsRequest);
		GetMetricWidgetImageResult getMetricWidgetImageResponse = cloudWatch
				.getMetricWidgetImage(getMetricWidgetImageRequest);

		ByteBuffer imageBytes = getMetricWidgetImageResponse.getMetricWidgetImage().asReadOnlyBuffer();
		byte[] arr = new byte[imageBytes.remaining()];
		imageBytes.get(arr);
		InputStream in = new ByteArrayInputStream(arr);
		
		BufferedImage image = ImageIO.read(in);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    
	    ImageIO.write(image, "png", baos);
	    byte[] imageData = baos.toByteArray();

		//ByteArrayOutputStream bos = new ByteArrayOutputStream();
		FileOutputStream out = new FileOutputStream("temp.png");
		out.write(imageData);
		//out.getChannel().write(imageData);
		out.close();
	}
}