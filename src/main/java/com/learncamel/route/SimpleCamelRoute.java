package com.learncamel.route;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.learncamel.bean.MyBean;

@Component
public class SimpleCamelRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        
    	from("timer:hello?period=10s")
    	   	.pollEnrich("file:data/input?delete=true&readLock=none")
        	.recipientList(constant("direct:start,direct:tap,direct:d"))
    	   	.recipientList(header("camelfileName")).streaming().parallelProcessing().ignoreInvalidEndpoints()
    		.to("direct-vm:start");
    	
    	  from("direct-vm:start")
    	  	.to("http://fooservice.com/slow")
    	  	 .circuitBreaker()
    	  	.transform().constant("Fallback message")
    	  	.end()
    	  	.to("mock:result"); 

    	  
    }
}
