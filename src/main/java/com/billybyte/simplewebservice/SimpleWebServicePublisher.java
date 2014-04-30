package com.billybyte.simplewebservice;

import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.ws.Endpoint;

import com.thoughtworks.xstream.XStream;
/**
 * 
 * @author Bill Perlman
 *
 * @param <K>
 * @param <T>
 */
public class SimpleWebServicePublisher<K,T> {
	private final XStream xstream;
	private final boolean isMultiThreading;
	private  final String serverUrlAddressBehindFirewall ;// = "http://127.0.0.1:9000/simple";
	private final String portNum;
	private final SimpleWebServiceProcessRequestInterface<K, T> requestProcessor;	
	private boolean published=false;
	
// ^(http\://){0,1}([0-9]{1,3}\.){3,3}[0-9]$
	private final String urlAddressRegexString = "^(http\\://){0,1}([0-9]{1,3}\\.){3,3}[0-9]{1,3}$";
	private final Pattern urlAddressPattern =Pattern.compile(urlAddressRegexString);
	private final String portNumRegexString = "^[1-9][0-9]{3,3}$";
	private final Pattern portNumPattern =Pattern.compile(portNumRegexString);
//	private final T nullReturn;

	public SimpleWebServicePublisher(String serverUrlAddressBehindFirewall,
			boolean isMultiThreading, String portNum, XStream xstream,
			SimpleWebServiceProcessRequestInterface<K, T> requestProcessor/*T nullReturn*/) {
		super();
		// validate ip address from caller
		if(serverUrlAddressBehindFirewall==null){
			throw new IllegalArgumentException(" null serverUrlAddressBehindFirewall");
		}
		// validate ipAddress
		if(!validatePattern(urlAddressPattern,serverUrlAddressBehindFirewall,1)){
			throw new IllegalArgumentException(" inValid serverUrlAddressBehindFirewall");
		}

		// validate port number
		if(portNum==null){
			throw new IllegalArgumentException(" null portNum");
		}
		if(!validatePattern(portNumPattern,portNum,1)){
			throw new IllegalArgumentException(" inValid portNum");
		}

		if(xstream==null){
			throw new IllegalArgumentException(" null xstream");
		}
		
		if(requestProcessor==null){
			throw new IllegalArgumentException(" null requestProcessor");
		}
		
		this.serverUrlAddressBehindFirewall = serverUrlAddressBehindFirewall;
		this.isMultiThreading = isMultiThreading;
		this.portNum = portNum;
		this.xstream = xstream;
		this.requestProcessor = requestProcessor;
//		this.nullReturn = nullReturn;
	}
	
	private boolean validatePattern(Pattern pattern, String stringToMatch,
			int expectedInstances){
		Matcher matcher = pattern.matcher(stringToMatch);
		int matchCount = 0;
		while (matcher.find()) {
			matchCount++;
		}
		if(matchCount!=expectedInstances)return false;
		return true;

	}
	
	public boolean isPublished(){
		return published;
	}
	
	/**
	 *  Execute (publish) server 
	 *  Can only publish it once. Other attempts will be ignored.
	 */
	public void publishService(){
		if(isPublished())return ;
		this.published = true;
		String fullAddress = ServerList.getFullAddress(serverUrlAddressBehindFirewall, portNum);

//		String fullAddress = serverIpAddressBehindFirewall+":"+this.portNum+"/simple";
//		if(!fullAddress.toLowerCase().contains("http://")){
//			fullAddress = "http://"+fullAddress;
//		}
//	    if(isMultiThreading){
//		  	  Endpoint ep = Endpoint.create(new SimpleServiceImpl<K,T>(this.xstream,
//		  			  this.requestProcessor));
//		  	  ep.setExecutor(Executors.newFixedThreadPool(5));
//		  	  ep.publish(fullAddress);
//	    }else{
//	    	  Endpoint ep = Endpoint.create(new SimpleServiceImpl<K,T>(this.xstream,
//	      			  this.requestProcessor));
//	      	  ep.publish(fullAddress);
//	    }
		SimpleService service = new SimpleServiceImpl<K,T>(this.xstream,
	  			this.requestProcessor);
		ServerList.registerInProcessService(this.serverUrlAddressBehindFirewall, this.portNum, service);
	  	Endpoint ep = Endpoint.create(service);
//	  	Endpoint ep = Endpoint.create(new SimpleServiceImpl<K,T>(this.xstream,
//	  			this.requestProcessor));
	  	ep.setExecutor(Executors.newFixedThreadPool(20));
	  	ep.publish(fullAddress);
	    System.out.println(this.getClass().getName()+ " server is ready on IP "+serverUrlAddressBehindFirewall+ ":"+portNum);
	}
	
	   

}
