package com.amazonaws.samples;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.json.JSONObject;

import name.wramner.jmstools.producer.AmqJmsProducer;



public class AmqJmsToolsProducerTrigger implements Processor {
	
	

	public void process(Exchange exchange) throws Exception {
		System.out.println("Entering process method...");

		String triggerMsg = exchange.getIn()
                            .getBody(String.class); 
		System.out.println("The trigger msg : "+triggerMsg);
		
		JSONObject jObj = new JSONObject(triggerMsg);
		ArrayList<String> arr=new ArrayList<String>();
		

		for (Object keyObj: jObj.keySet())
		{
		    String key = (String)keyObj;
		    arr.add(key);        
		}
		StringBuffer argList = new StringBuffer();
		for(String key:arr) {
			if(!key.equalsIgnoreCase("static")) {
				argList.append("-"+key);
				argList.append(" ");
			}
			argList.append((String)jObj.get(key));
			argList.append(" ");
		}
		String argParam = argList.toString().trim();
		System.out.println("The constructed arg list "+argParam);
		
		Pattern pattern = Pattern.compile(" ");
		String[] args=pattern.split(argParam);
		//System.out.println("The command line args : "+args);

		
		//AmqJmsProducer.main(args);
		new AmqJmsProducer().run(args);
        
	}
}