package com.amazonaws.samples;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.UUID;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.GetMetricWidgetImageRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricWidgetImageResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;


@Path("/get-broker-dashboard")


public class CloudwatchResource {
	@Context
	SecurityContext securityContext;


	static final Logger log = LogManager.getLogger(CloudwatchResource.class);
	
	private static final String METRIC_WIDGET_BUCKET = System.getenv("METRIC_WIDGET_BUCKET");
	
	public CloudwatchResource() {
		

	}
	
	@GET
	@Produces({ "text/html" })
	public Response getCloudWatchWidget(
			@DefaultValue("") @QueryParam("broker") String broker,
			@DefaultValue("") @QueryParam("queue") String queue,
			@Context SecurityContext securityContext) throws IOException {
		
		log.debug("Entering the widget method...");
		
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		
		ve.init();
		Template template = ve.getTemplate("image-api.template");

		VelocityContext context = new VelocityContext();
		context.put("broker", broker);
		context.put("queue", queue);
		
		/* now render the template into a StringWriter */
		StringWriter writer = new StringWriter();
		template.merge(context, writer);	
		System.out.println("Image api json..."+writer.toString());
		final AmazonCloudWatch cloudWatch = AmazonCloudWatchClientBuilder.defaultClient();
		GetMetricWidgetImageRequest getMetricWidgetImageRequest = new GetMetricWidgetImageRequest();

		getMetricWidgetImageRequest.setMetricWidget(writer.toString());
		GetMetricWidgetImageResult getMetricWidgetImageResponse = cloudWatch
				.getMetricWidgetImage(getMetricWidgetImageRequest);

		ByteBuffer imageBytes = getMetricWidgetImageResponse.getMetricWidgetImage().asReadOnlyBuffer();
		byte[] arr = new byte[imageBytes.remaining()];
		imageBytes.get(arr);
		
		InputStream inputStream = new ByteArrayInputStream(arr);
		
		//BufferedImage image = ImageIO.read(in);
	    //ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    
	    //ImageIO.write(image, "png", baos);
	    //byte[] imageData = baos.toByteArray();
		//return Response.ok(imageData).build();
		
		AmazonS3 s3Client = LambdaHandler.getS3Client();
		String objectKey = "metric-widget-"+UUID.randomUUID()+".png";
		ObjectMetadata s3ObjectMetadata = new ObjectMetadata();
		s3ObjectMetadata.setContentLength(arr.length);
		
		s3Client.putObject(
				   new PutObjectRequest(METRIC_WIDGET_BUCKET, objectKey, inputStream,s3ObjectMetadata)
				      .withCannedAcl(CannedAccessControlList.PublicRead));
		String publicUrl = 
				s3Client.getUrl(METRIC_WIDGET_BUCKET, objectKey).toExternalForm();
		
		String linkedText = "<html>\n" + 
				"    \n" + 
				"    <body>\n" + 
				"        <p style = \"font-family:georgia,garamond,serif;font-size:16px;font-style:italic;\"\">\n" + 
				"            Please access cloudwatch widget <a href=\""+publicUrl+"\">here </p>\n" + 
				"    </body>\n" + 
				"</html>";
		
		return Response.status(200).entity(linkedText).build();

	}

	
}
